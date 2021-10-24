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
        val mainBody = doc.byIDOrThrow("main")

        val resultsFound: Int = resultsFoundParser(mainBody)

        val navigation = extractPage(mainBody.getElementsByAttributeValue("role", "navigation").getOrNull(1))

        val works = mainBody.getElementsByAttributeValue("role", "article").map { article -> extractWork(article) }
        return SearchResult(resultsFound, navigation, works)
    }

}