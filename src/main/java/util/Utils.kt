package util

import io.ktor.client.request.*
import io.ktor.http.*
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

private val encodedArrayBraces = "[]".encode()
private val check = "✓".encode()

private val userLoginField = "user[login]".encode()
private val userPasswordField = "user[password]".encode()
private val userRememberMeField = "user[remember_me]".encode()

fun encodeParameter(name: String, value: String, array: Boolean = false) =
    "${name.encode()}${if (array) encodedArrayBraces else ""}=${value.encode()}"

fun String.encode(charset: Charset = StandardCharsets.UTF_8): String = URLEncoder.encode(this, charset)

fun String.commaSeparatedToInt() = this.replace(",", "").toInt()

fun loginForm(userName: String, userPassword: String, authenticity_token: String, rememberMe: Boolean) =
    "utf8=$check&" +
    "authenticity_token=$authenticity_token&" +
    "$userLoginField=$userName&" +
    "$userPasswordField=$userPassword&" +
    "$userRememberMeField=${if (rememberMe) "1" else "0"}&" +
    "commit=Log+In"

/**
 * The default cookie method does not work for this application.
 *  I believe that it is encoding the values before setting the cookie. AO3 requires the raw cookie value
 */
fun HttpRequestBuilder.setCookie(name: String, value: String) {
    val renderedCookie = "${name}=${value};"
    if (HttpHeaders.Cookie !in this.headers) {
        this.headers.append(HttpHeaders.Cookie, renderedCookie)
        return
    }
    this.headers[HttpHeaders.Cookie] += renderedCookie
}