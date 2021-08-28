package util

import java.net.HttpCookie
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

private val encodedArrayBraces = "[]".encode()
private val check = "✓".encode()

private val userLoginField = "user[login]".encode()
private val userPasswordField = "user[password]".encode()
private val userRememberMeField = "user[remember_me]".encode()

fun encodeParameter(name: String, value: String, array: Boolean = false) =
    "${name.encode()}${if (array) encodedArrayBraces else ""}=${value.encode()}"

fun String.encode(charset: Charset = StandardCharsets.UTF_8): String = URLEncoder.encode(this, charset)

fun loginForm(userName: String, userPassword: String, authenticity_token: String, rememberMe: Boolean) =
    "utf8=$check&" +
    "authenticity_token=$authenticity_token&" +
    "$userLoginField=$userName&" +
    "$userPasswordField=$userPassword&" +
    "$userRememberMeField=${if (rememberMe) "1" else "0"}"

fun generateHttpsConnection(keyStore: KeyStore): (URL) -> HttpsURLConnection {
    val sslContext = SSLContext.getInstance("SSL")
    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(keyStore)
    sslContext.init(null, trustManagerFactory.trustManagers, SecureRandom())

    return { url: URL ->
        val conn = url.openConnection() as HttpsURLConnection
        conn.sslSocketFactory = sslContext.socketFactory
        conn
    }
}

fun HttpURLConnection.extractSetCookie(action: (HttpCookie?) -> Unit): Unit? {
    this.headerFields["set-cookie"]?.let {
        it.forEach { cookie_string ->
            val cookie: MutableList<HttpCookie>? = HttpCookie.parse(cookie_string)
            return cookie?.forEach(action)
        }
    }
    return null
}