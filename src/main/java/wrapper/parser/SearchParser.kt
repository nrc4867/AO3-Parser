package wrapper.parser

import model.result.SearchResult
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import wrapper.parser.ParserRegex.resultsFoundRegex

class SearchParser : Parser<SearchResult> {

    internal var resultsFoundParser =
        { mainBody: Element -> resultsFoundRegex.getRegexFound(mainBody.getElementsByTag("h3")[0].text(), 0) }

    override fun parsePage(queryResponse: String): SearchResult {
        val doc: Document = Jsoup.parse(queryResponse)
        val mainBody = doc.getElementById("main")

        val resultsFound: Int = resultsFoundParser(mainBody)

        var page = 1
        var pages = 1
        if (mainBody.getElementsByAttributeValue("role", "navigation").size > 1) {
            val navigation = mainBody.getElementsByAttributeValue("role", "navigation")[1]
            page = navigation.getElementsByClass("current")[0].text().toInt()
            val pageButtons = navigation.getElementsByTag("a")
            pages = pageButtons[pageButtons.size - 2].text().toInt()
        }

        val works = mainBody.getElementsByAttributeValue("role", "article").map { article -> extractWork(article) }
        return SearchResult(resultsFound, pages, page, works)
    }

}