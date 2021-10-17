package dev.chieppa.wrapper.parser

import dev.chieppa.model.result.UserPseuds
import dev.chieppa.model.result.UserQueryResult
import dev.chieppa.model.result.work.Creator
import dev.chieppa.wrapper.parser.ParserRegex.authorPseudoRegex
import dev.chieppa.wrapper.parser.ParserRegex.authorUserRegex
import dev.chieppa.wrapper.parser.ParserRegex.digitsRegex
import dev.chieppa.wrapper.parser.ParserRegex.userDashboardRegex
import dev.chieppa.wrapper.parser.UserQueryParser.Dashboard.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class UserQueryParser<E>(private val queryParser: Parser<E>) : Parser<UserQueryResult<E>> {

    private enum class Dashboard {
        WORKS, DRAFTS, SERIES, BOOKMARKS, COLLECTIONS, GIFTS, INBOX, SIGNUPS, ASSIGNMENTS, CLAIMS, RELATED_WORKS
    }

    override fun parsePage(queryResponse: String): UserQueryResult<E> {
        val document = Jsoup.parse(queryResponse)
        val dashboard = document.getElementById("dashboard")

        with(parseUserDashboard(dashboard)) {
            return UserQueryResult(
                queryResult = queryParser.parsePage(queryResponse),
                userPseuds = parseUserPseuds(dashboard.getElementsByClass("pseud").firstOrNull()),
                works = getOrZero(WORKS),
                series = getOrZero(SERIES),
                bookmarks = getOrZero(BOOKMARKS),
                collections = getOrZero(COLLECTIONS),
                gifts = getOrZero(GIFTS),
                drafts = get(DRAFTS),
                inbox = get(INBOX),
                signups = get(SIGNUPS),
                assignments = get(ASSIGNMENTS),
                claims = get(CLAIMS),
                relatedWorks = get(RELATED_WORKS),
            )
        }
    }

    private fun Map<Dashboard, Int>.getOrZero(dashboardElement: Dashboard): Int = getOrDefault(dashboardElement, 0)


    private fun parseUserPseuds(pseudList: Element?): UserPseuds {
        if (pseudList == null) return UserPseuds(listOf(), 0)

        val links = pseudList.getFirstByTag("ul").getElementsByTag("a")
        val pseuds = links.subList(0, links.size - 1).map { link ->
            Creator(
                authorUserRegex.getWithEmptyDefault(link.href()),
                authorPseudoRegex.getWithEmptyDefault(link.href())
            )
        }

        return UserPseuds(pseuds, digitsRegex.getWithZeroDefault(links.last().text()))
    }

    private fun parseUserDashboard(dashboard: Element): Map<Dashboard, Int> {
        val dashboardMap = HashMap<Dashboard, Int>()

        fun Element.setMapKey(key: Dashboard) = dashboardMap.put(key, digitsRegex.getWithZeroDefault(this.text()))

        val links =
            dashboard.getElementsByTag("ul").apply { subList(1, this.size) }.flatMap { it.getElementsByTag("a") }
        for (link in links) {
            with(link) {
                when (userDashboardRegex.getWithEmptyDefault(link.text())) {
                    "Works" -> setMapKey(WORKS)
                    "Drafts" -> setMapKey(DRAFTS)
                    "Series" -> setMapKey(SERIES)
                    "Bookmarks" -> setMapKey(BOOKMARKS)
                    "Collections" -> setMapKey(COLLECTIONS)
                    "Inbox" -> setMapKey(INBOX)
                    "Sign-ups" -> setMapKey(SIGNUPS)
                    "Assignments" -> setMapKey(ASSIGNMENTS)
                    "Claims" -> setMapKey(CLAIMS)
                    "Related Works" -> setMapKey(RELATED_WORKS)
                    "GIFTS" -> setMapKey(GIFTS)
                    else -> {
                    }
                }
            }
        }
        return dashboardMap
    }

}