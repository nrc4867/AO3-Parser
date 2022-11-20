package dev.chieppa.wrapper.parser

import dev.chieppa.wrapper.model.result.SearchResult
import dev.chieppa.wrapper.model.result.work.ArticleResult
import dev.chieppa.wrapper.parser.ParserRegex.resultsFoundRegex
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class SearchParser<E : ArticleResult>(
    val resultsFoundParser: (Element) -> Int = { mainBody: Element ->
        resultsFoundRegex.getWithZeroDefault(
            mainBody.getFirstByTag("h3").text()
        )
    },
    val articleExtraction: (Element) -> E = { article -> extractArticle(article) as E }
) : Parser<SearchResult<E>> {

    override fun parsePage(queryResponse: String): SearchResult<E> {
        val doc: Document = Jsoup.parse(queryResponse)
        val mainBody = doc.byIDOrThrow("main")

        val resultsFound: Int = resultsFoundParser(mainBody)

        val navigation = extractPage(mainBody.getElementsByAttributeValue("role", "navigation").getOrNull(1))

        val works = mainBody.getElementsByAttributeValue("role", "article").map { articleExtraction(it) }
        return SearchResult(resultsFound, navigation, works)
    }

}