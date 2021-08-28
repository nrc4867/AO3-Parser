package wrapper

import constants.*
import exception.InvalidLoginException
import model.SearchQuery
import model.result.AutoCompleteResult
import model.result.SearchResult
import model.user.Session
import model.work.Work
import util.Logging
import util.extractSetCookie
import util.logger
import wrapper.parser.AutoCompleteParser
import wrapper.parser.LoginPageParser
import wrapper.parser.SearchParser
import java.net.HttpCookie
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
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
    private val urlConnection: (URL) -> HttpURLConnection,
    private val base_loc: String = ao3_url,
    private val search_loc: String = ao3_search,
    private val login_loc: String = ao3_login,
    private val auto_complete: (String, String) -> String = ao3_autocomplete,
    private val work_location: (work_id: Int, chapter_id: Int) -> String = ao3_work,
) : Logging{

    var searchWrapper = Wrapper(SearchParser())
    var autoCompleteWrapper = Wrapper(AutoCompleteParser())
    var loginWrapper = Wrapper(LoginPageParser())

    fun search(
        searchQuery: SearchQuery,
        session: Session? = null,
        page: Int = 1,
    ): SearchResult {
        val conn = urlConnection(URL(base_loc + search_loc + "page=$page&" + searchQuery.searchString()))

        session?.let {
            conn.setRequestProperty("Cookie", it.getCookie())
        }

        val result = searchWrapper.read(conn.inputStream)
        conn.disconnect()
        return result
    }

    fun checkUpdate(work: List<Work>, session: Session? = null, rateLimit: Int = 600): List<Work> {
        return emptyList()
    }

    fun suggestAutoComplete(autoCompleteField: AutoCompleteField, userTerm: String): List<AutoCompleteResult> {
        val conn = urlConnection(
            URL(
                base_loc + auto_complete(
                    autoCompleteField.search_param,
                    URLEncoder.encode(userTerm, Charsets.UTF_8)
                )
            )
        )
        val result = autoCompleteWrapper.read(conn.inputStream)
        conn.disconnect()
        return result
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
    fun login(username: String, password: String, rememberMe: Boolean = false): Session {

        /**
         * I have to get a real login page to acquire a rails token
         */
        var conn: HttpURLConnection = urlConnection(URL(base_loc + login_loc))

        val authToken: String = loginWrapper.read(conn.inputStream)
        conn.inputStream.close()

        val cookies: MutableMap<String, String> = HashMap()
        conn.headerFields["set-cookie"]?.let {
            it.forEach { cookie_string ->
                val cookie: MutableList<HttpCookie>? = HttpCookie.parse(cookie_string)
                cookie?.forEach { httpCookie ->
                    cookies[httpCookie.name] = httpCookie.value
                }
            }
        }

        conn.disconnect()
        conn = urlConnection(URL(base_loc + login_loc))

        val message: String = util.loginForm(username, password, authToken, rememberMe)

        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.instanceFollowRedirects = false

        val postData: ByteArray = message.toByteArray(StandardCharsets.UTF_8)
        conn.setRequestProperty("charset", "utf-8")
        conn.setRequestProperty("Content-length", postData.size.toString())
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        conn.setRequestProperty("Cookie", "_otwarchive_session=${cookies["_otwarchive_session"]}")

        conn.outputStream.use {
            it.write(postData)
            it.flush()
        }
        conn.disconnect()

        if (conn.responseCode != HttpURLConnection.HTTP_MOVED_TEMP) {
            throw InvalidLoginException()
        }

        val session = Session()
        conn.extractSetCookie {
            it?.let {
                when (it.name) {
                    "_otwarchive_session" -> session.session_id = it.value
                    "remember_user_token" -> session.remember_user_token = it.value
                    "user_credentials" -> session.userCredentials = it.value
                    else -> logger().debug("unused cookie ${it.name}, ${it.value}")
                }
            }
        }

        return session
    }

}