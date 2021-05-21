package wrapper

import constants.AO3Constant
import wrapper.parser.LoginPageParser
import wrapper.parser.Parser
import java.net.HttpURLConnection
import java.net.URL

class LoginWrapper(
    private val base_loc: String = AO3Constant.ao3_url,
    private val login_loc: String = AO3Constant.ao3_login,
    private val parser: Parser<String> = LoginPageParser(),
) {

    fun login(username: String, password: String, remember_me: Boolean = true) {

        /**
         * I have to get a real login page to acquire a rails token
         */
        val url = URL(base_loc  + login_loc)
        val conn : HttpURLConnection = url.openConnection() as HttpURLConnection

        lateinit var authToken: String
        conn.inputStream.bufferedReader().use {
            authToken = parser.parsePage(it.readText())
        }
        val session = conn.getHeaderField("set-cookie")
        conn.disconnect()

        println("$authToken || $session")

//        conn = url.openConnection() as HttpURLConnection
//        conn.requestMethod = "POST"



    }

}

fun main() {
    LoginWrapper().login("", "", false)
}