package dev.chieppa.wrapper

import dev.chieppa.wrapper.model.Session
import io.ktor.http.*
import mu.KotlinLogging
import java.net.HttpCookie

private val logger = KotlinLogging.logger { }

fun extractSession(headers: Headers): Session  {
    val session = Session()
    headers.getAll(HttpHeaders.SetCookie)?.forEach {
        HttpCookie.parse(it)[0].also { cookie ->
            when (cookie.name) {
                "_otwarchive_session" -> session.session_id = cookie.value
                "remember_user_token" -> session.remember_user_token = cookie.value
                "user_credentials" -> session.userCredentials = cookie.value
                "flash_is_set" -> {
                }
                else -> logger.warn("unused cookie ${cookie.name}, ${cookie.value}")
            }
        }
    }
    return session
}