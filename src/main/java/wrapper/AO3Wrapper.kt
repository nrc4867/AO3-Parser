package wrapper

import constants.AO3Constant
import constants.AutoCompleteField
import exception.InvalidLoginException
import model.SearchQuery
import model.result.AutoCompleteResult
import model.result.SearchResult
import model.user.Session
import model.work.Work
import util.Utils
import wrapper.parser.AutoCompleteParser
import wrapper.parser.LoginPageParser
import wrapper.parser.SearchParser
import java.io.File
import java.net.HttpCookie
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.KeyStore

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
    private val base_loc: String = AO3Constant.ao3_url,
    private val search_loc: String = AO3Constant.ao3_search,
    private val login_loc: String = AO3Constant.ao3_login,
    private val auto_complete: (String, String) -> String = AO3Constant.ao3_autocomplete,
    private val work_location: (work_id: Int, chapter_id: Int) -> String = AO3Constant.ao3_work,
) {

    var searchWrapper = Wrapper(SearchParser())
    var autoCompleteWrapper = Wrapper(AutoCompleteParser())
    var loginWrapper = Wrapper(LoginPageParser())

    var session: Session? = null

    fun search(
        searchQuery: SearchQuery,
        session: Session? = this.session,
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

    fun checkUpdate(work: List<Work>, session: Session? = this.session, rateLimit: Int = 600): List<Work> {
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

    /**
     * Login - Create a Session for AO3
     *
     * @param username plaintext
     * @param password plaintext
     * @param rememberMe if true the session token should work across sessions
     * @param updateInternalSession if true any future searches with this AO3 wrapper instance will be preformed with the result of this login by default
     * @return a logged in user session
     * @throws InvalidLoginException username or password is incorrect, or ao3 did not return a login indicator
     */
    @Throws(InvalidLoginException::class)
    fun login(username: String, password: String, rememberMe: Boolean = false, updateInternalSession: Boolean = false) {

        /**
         * I have to get a real login page to acquire a rails token
         */
        val conn = urlConnection(URL(base_loc + login_loc))

        val authToken: String = loginWrapper.read(conn.inputStream)
//        val session = conn.getHeaderField("set-cookie")
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

        println("$authToken || ${cookies["_otwarchive_session"]}")

        val postdata = ""
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.instanceFollowRedirects = false

        conn.disconnect()
    }

}

fun main() {
    lateinit var aO3Wrapper: AO3Wrapper
    val file = File("./localconfig/password")
    file.inputStream().bufferedReader().use {
        aO3Wrapper = AO3Wrapper(Utils.generateHttpsConnection(KeyStore.getInstance(File("./localconfig/ao3_cert"), it.readText().toCharArray())))
    }
    aO3Wrapper.login("a", "a")
}