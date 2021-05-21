package util

import java.net.URL
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

sealed class Utils {

    fun generateHttpsConnection(keyStore: KeyStore): (URL) -> HttpsURLConnection {
        val sslContext = SSLContext.getInstance("SSL")
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        sslContext.init(null, trustManagerFactory.trustManagers, SecureRandom())

        return {url: URL ->
            val conn = url.openConnection() as HttpsURLConnection
            conn.sslSocketFactory = sslContext.socketFactory
            conn
        }
    }

}
