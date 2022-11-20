package dev.chieppa.wrapper.parser

import dev.chieppa.wrapper.model.result.PeopleResult
import dev.chieppa.wrapper.model.result.people.FandomWork
import dev.chieppa.wrapper.model.result.people.Person
import dev.chieppa.wrapper.parser.ParserRegex.authorPseudoRegex
import dev.chieppa.wrapper.parser.ParserRegex.authorUserRegex
import dev.chieppa.wrapper.parser.ParserRegex.digitsRegex
import dev.chieppa.wrapper.parser.ParserRegex.externalLinkRegex
import dev.chieppa.wrapper.parser.ParserRegex.fandomIDRegex
import dev.chieppa.wrapper.parser.ParserRegex.personAttributeRegex
import dev.chieppa.wrapper.parser.ParserRegex.workInRegex
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class PersonParser : Parser<PeopleResult> {
    override fun parsePage(queryResponse: String): PeopleResult {
        val document = Jsoup.parse(queryResponse)
        val main = document.byIDOrThrow("main")

        val navigation = extractPage(main.getElementsByAttributeValue("role", "navigation")[1])

        return PeopleResult(
            found = digitsRegex.getWithZeroDefault(main.getFirstByTag("strong").text()),
            navigation = navigation,
            people = parsePeople(main.getElementsByAttributeValue("role", "article"))
        )

    }

    private fun parsePeople(articles: Elements): List<Person> {
        val people = mutableListOf<Person>()
        for (person in articles) {
            val user = person.getFirstByTag("a")
            val (works, bookmarks, fandomWorks) = parseWorksAndBookmarks(person)
            people.add(
                Person(
                    username = authorUserRegex.getWithEmptyDefault(user.href()),
                    pseudo = authorPseudoRegex.getWithEmptyDefault(user.href()),
                    profileImage = externalLinkRegex.getRegexFound(person.getFirstByTag("img").attr("src")),
                    works = works,
                    fandomWorks = fandomWorks,
                    bookmarks = bookmarks,
                    description = person.getElementsByTag("blockquote").getOrNull(0)?.outerHtml()
                )
            )
        }
        return people
    }

    private fun parseWorksAndBookmarks(person: Element): Triple<Int?, Int?, List<FandomWork>?> {
        person.getElementsByTag("h5").getOrNull(0)?.let {
            var works: Int? = null
            var bookmarks: Int? = null
            var fandomWorks: MutableList<FandomWork>? = null
            for (link in it.getElementsByTag("a")) {
                val linkText = link.text()
                val amount = digitsRegex.getWithZeroDefault(linkText)
                when (personAttributeRegex.getWithEmptyDefault(linkText)) {
                    "work" -> works = amount
                    "bookmark" -> bookmarks = amount
                    "work in", "works in" -> {
                        if (fandomWorks == null) fandomWorks = mutableListOf()
                        fandomWorks.add(
                            FandomWork(
                                digitsRegex.getWithZeroDefault(linkText),
                                workInRegex.getWithEmptyDefault(linkText),
                                fandomIDRegex.getWithZeroDefault(link.href())
                            )
                        )
                    }
                }
            }
            return Triple(works, bookmarks, fandomWorks)
        } ?: return Triple(null, null, null)
    }
}