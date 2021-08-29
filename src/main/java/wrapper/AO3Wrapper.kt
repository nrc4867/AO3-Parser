package wrapper

import constants.*
import exception.InvalidLoginException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import model.SearchQuery
import model.result.AutoCompleteResult
import model.result.SearchResult
import model.user.Session
import model.work.Work
import util.Logging
import util.logger
import util.loginForm
import util.setCookie
import wrapper.parser.AutoCompleteParser
import wrapper.parser.LoginPageParser
import wrapper.parser.SearchParser
import java.net.HttpCookie
import java.net.URLEncoder
import java.util.*

/**
 * AO3wrapper
 *
 * @property urlConnection
 * @property base_loc
 * @property search_loc
 * @property login_loc
 * @property auto_complete
 * @property work_location
 * @constructor Create empty A o3wrapper
 */
class AO3Wrapper(
    private val base_loc: String = ao3_url,
    private val search_loc: String = ao3_search,
    private val login_loc: String = ao3_login,
    private val auto_complete: (String, String) -> String = ao3_autocomplete,
    private val work_location: (work_id: Int, chapter_id: Int) -> String = ao3_work,
) : Logging, Closeable {

    var httpClient = HttpClient {
        expectSuccess = false
        followRedirects = false

        install(UserAgent) {
            agent = "generic-ao3-wrapper"
        }

        install(io.ktor.client.features.logging.Logging)
    }

    var searchWrapper = Wrapper(SearchParser())
    var autoCompleteWrapper = Wrapper(AutoCompleteParser())
    var loginWrapper = Wrapper(LoginPageParser())

    suspend fun search(
        searchQuery: SearchQuery,
        session: Session? = null,
        page: Int = 1,
    ): SearchResult {
        val response: HttpResponse =
            httpClient.request(base_loc + search_loc + "page=$page&" + searchQuery.searchString()) {
                method = HttpMethod.Get
                session?.let {
                    with(it) {
                        setSessionCookies()
                    }
                }
            }

        return searchWrapper.read(response.receive())
    }

    fun checkUpdate(work: List<Work>, session: Session? = null, rateLimit: Int = 600): List<Work> {
        return emptyList()
    }

    suspend fun suggestAutoComplete(autoCompleteField: AutoCompleteField, userTerm: String): List<AutoCompleteResult> {

        val response: HttpResponse =
            httpClient.request(
                base_loc + auto_complete(
                    autoCompleteField.search_param,
                    URLEncoder.encode(userTerm, Charsets.UTF_8)
                )
            ) {
                method = HttpMethod.Get
            }

        return autoCompleteWrapper.read(response.receive())
    }

    fun getChaptersSince(work: Work, date: Date = Date(0)) = getChaptersSince(work.workId, date)

    fun getChaptersSince(workId: Int, date: Date = Date(0)) {
    }

    /**
     * Login - Create a Session for AO3
     *
     * @param username plaintext
     * @param password plaintext
     * @param rememberMe if true the session token should work across sessions
     * @return a logged in user session
     * @throws InvalidLoginException username or password is incorrect, or ao3 did not return a login indicator
     */
    @Throws(InvalidLoginException::class)
    suspend fun login(username: String, password: String, rememberMe: Boolean = false): Session {

        /**
         * I have to get a real login page to acquire a rails token
         */
        val response: HttpResponse = httpClient.get(base_loc + login_loc)

        val authToken: String = loginWrapper.read(response.receive())

        val cookies: MutableMap<String, HttpCookie> = mutableMapOf()
        response.headers.getAll(HttpHeaders.SetCookie)?.forEach {
            HttpCookie.parse(it)[0].also { cookie ->
                cookies[cookie.name] = cookie
            }
        }

        val loginResponse: HttpResponse = httpClient.post(base_loc + login_loc) {
            contentType(ContentType.Application.FormUrlEncoded)
            body = loginForm(username, password, authToken, rememberMe)
            cookies[ao3_session_cookie]?.let { setCookie(ao3_session_cookie, it.value) }
        }

        if (loginResponse.status.value != HttpStatusCode.Found.value) {
            throw InvalidLoginException()
        }

        val session = Session()
        loginResponse.headers.getAll(HttpHeaders.SetCookie)?.forEach {
            HttpCookie.parse(it)[0].also { cookie ->
                when (cookie.name) {
                    "_otwarchive_session" -> session.session_id = cookie.value
                    "remember_user_token" -> session.remember_user_token = cookie.value
                    "user_credentials" -> session.userCredentials = cookie.value
                    else -> logger().error("unused cookie ${cookie.name}, ${cookie.value}")
                }
            }
        }

        return session
    }

    suspend fun validateSession(session: Session) : Boolean {
        val response: HttpResponse = httpClient.get(base_loc + login_loc) {
            with(session) {
                setSessionCookies()
            }
        }

        return response.status == HttpStatusCode.Found
    }

    override fun close() {
        httpClient.close()
    }

}