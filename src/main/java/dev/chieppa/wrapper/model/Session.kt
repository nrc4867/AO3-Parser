package dev.chieppa.wrapper.model

import dev.chieppa.wrapper.constants.ao3_session_cookie
import dev.chieppa.wrapper.constants.ao3_session_remember_user_token
import dev.chieppa.wrapper.constants.ao3_session_user_conditionals
import dev.chieppa.wrapper.util.setCookie
import io.ktor.client.request.*

data class Session(var session_id: String? = null, var remember_user_token: String? = null, var userCredentials: String? = "1") {
    fun HttpRequestBuilder.setSessionCookies() {
        session_id?.let { setCookie(ao3_session_cookie, it) }
        remember_user_token?.let { setCookie(ao3_session_remember_user_token, it) }
        userCredentials?.let { setCookie(ao3_session_user_conditionals, it) }
    }
}