package util

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

fun encodeParameter(name: String, value: String, array: Boolean = false) =
    "${name.encode()}${if (array) encodedArrayBraces else ""}=${value.encode()}"

fun String.encode(charset: Charset = StandardCharsets.UTF_8): String = URLEncoder.encode(this, charset)

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
