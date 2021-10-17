package dev.chieppa.wrapper.parser

import dev.chieppa.model.result.SearchResult
import dev.chieppa.wrapper.parser.ParserRegex.resultsFoundRegex
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class SearchParser : Parser<SearchResult> {

    internal var resultsFoundParser =
        { mainBody: Element -> resultsFoundRegex.getWithZeroDefault(mainBody.getFirstByTag("h3").text()) }

    override fun parsePage(queryResponse: String): SearchResult {
        val doc: Document = Jsoup.parse(queryResponse)
        val mainBody = doc.getElementById("main")

        val resultsFound: Int = resultsFoundParser(mainBody)

        var page = 1
        var pages = 1
        if (mainBody.getElementsByAttributeValue("role", "navigation").size > 1) {
            val navigation = mainBody.getElementsByAttributeValue("role", "navigation")[1]
            page = navigation.getElementsByClass("current").getOrNull(0)?.text()?.toInt() ?: Int.MAX_VALUE
            val pageButtons = navigation.getFirstByClass("next")
            pages = pageButtons.previousElementSibling().text().toInt()
        }

        val works = mainBody.getElementsByAttributeValue("role", "article").map { article -> extractWork(article) }
        return SearchResult(resultsFound, pages, page, works)
    }

}