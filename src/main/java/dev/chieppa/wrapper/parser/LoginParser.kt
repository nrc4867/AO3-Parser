package dev.chieppa.wrapper.parser

import dev.chieppa.exception.loginexception.NoCSRFTokenException
import org.jsoup.Jsoup

/**
 * We need to request a page to get the rails server authenticity_token
 */
class LoginPageParser : Parser<String> {

    override fun parsePage(queryResponse: String): String {
        return Jsoup.parse(queryResponse).getElementsByAttributeValue("name", "csrf-token").elementAtOrNull(0)
            ?.attr("content")
            ?: throw NoCSRFTokenException()
    }

}