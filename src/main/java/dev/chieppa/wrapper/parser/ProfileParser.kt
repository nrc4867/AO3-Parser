package dev.chieppa.wrapper.parser

import dev.chieppa.wrapper.exception.parserexception.ExpectedElementException
import dev.chieppa.wrapper.model.result.UserProfileResult
import dev.chieppa.wrapper.model.result.work.Creator
import dev.chieppa.wrapper.parser.DateTimeFormats.YYYYMMddEscaped
import dev.chieppa.wrapper.parser.ParserRegex.authorPseudoRegex
import dev.chieppa.wrapper.parser.ParserRegex.authorUserRegex
import dev.chieppa.wrapper.parser.ProfileParser.UserProfile.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.temporal.TemporalAccessor

class ProfileParser : Parser<UserProfileResult> {

    private enum class UserProfile {
        PSEUDS, JOIN_DATE, USER_ID, LIVES_IN, BIRTHDAY
    }

    @Suppress("UNCHECKED_CAST")
    override fun parsePage(queryResponse: String): UserProfileResult {
        val document = Jsoup.parse(queryResponse)
        val mainBody = document.byIDOrThrow("main")

        val profileImage: String = mainBody.getElementsByTag("img").firstOrNull()?.attr("src") ?: ""
        val title = mainBody.getFirstByClass("profile").getElementsByClass("heading").getOrNull(1)?.text()
        val bio = mainBody.getElementsByClass("bio").firstOrNull()?.getFirstByTag("blockquote")?.outerHtml()

        val metaItems = parseUserMeta(mainBody.getFirstByClass("profile").getFirstByClass("meta"))

        return UserProfileResult(
            profileImage = profileImage,
            title = title,
            pseuds = metaItems[PSEUDS] as List<Creator>,
            joinDate = metaItems[JOIN_DATE] as TemporalAccessor,
            userID = metaItems[USER_ID] as Int,
            livesIn = metaItems[LIVES_IN] as String?,
            birthday = metaItems[BIRTHDAY] as TemporalAccessor?,
            bio = bio
        )
    }

    private fun parseUserMeta(meta: Element): Map<UserProfile, Any?> {
        val userMeta = HashMap<UserProfile, Any?>()
        for (category in meta.getElementsByTag("dt")) {
            val dd = category.nextElementSibling() ?: throw ExpectedElementException("next sibling", "expected element")
            when (category.text()) {
                "My pseuds:" -> userMeta[PSEUDS] = dd.getElementsByTag("a")?.map {
                    Creator(
                        authorUserRegex.getWithEmptyDefault(it.href()),
                        authorPseudoRegex.getWithEmptyDefault(it.href())
                    )
                }
                "I joined on:" -> userMeta[JOIN_DATE] = YYYYMMddEscaped.parse(dd.text())
                "My user ID is:" -> userMeta[USER_ID] = dd.text().toInt()
                "I live in:" -> userMeta[LIVES_IN] = dd.text()
                "My birthday:" -> userMeta[BIRTHDAY] = YYYYMMddEscaped.parse(dd.text())
            }
        }
        return userMeta
    }
}