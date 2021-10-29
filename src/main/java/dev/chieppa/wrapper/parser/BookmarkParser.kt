package dev.chieppa.wrapper.parser

import dev.chieppa.constants.workproperties.BookmarkType
import dev.chieppa.constants.workproperties.parseBookmarkType
import dev.chieppa.model.result.bookmark.BookmarkResult
import dev.chieppa.model.result.bookmark.BookmarkSearchResult
import dev.chieppa.model.result.bookmark.BookmarkUserSection
import dev.chieppa.model.result.work.Creator
import dev.chieppa.model.result.work.Work
import dev.chieppa.wrapper.parser.DateTimeFormats.ddMMMYYYY
import dev.chieppa.wrapper.parser.ParserRegex.authorUserRegex
import dev.chieppa.wrapper.parser.ParserRegex.bookmarkerPseudo
import dev.chieppa.wrapper.parser.ParserRegex.digitsRegex
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class BookmarkParser : Parser<BookmarkSearchResult> {

    override fun parsePage(queryResponse: String): BookmarkSearchResult {
        val doc: Document = Jsoup.parse(queryResponse)
        val mainBody = doc.byIDOrThrow("main")

        val bookmarkResults = getBookmarks(mainBody.getElementsByAttributeValue("role", "article"))

        val foundPages =
            digitsRegex.getRegexFound(mainBody.getElementsByClass("heading").getOrNull(2)?.text().orEmpty(), 0)
        val navigation = extractPage(mainBody.getElementsByClass("pagination").getOrNull(0))

        return BookmarkSearchResult(bookmarkResults, foundPages, navigation)

    }

    private fun getBookmarks(articles: Elements): List<BookmarkResult> {
        val bookmarkResults = mutableListOf<BookmarkResult>()
        articles.forEach {
            bookmarkResults.add(
                BookmarkResult(
                    parseWork(it),
                    parseBookmarkSymbol(it),
                    parseUserSection(it)
                )
            )
        }
        return bookmarkResults
    }

    private fun parseWork(article: Element): Work? {
        if (article.getElementsByClass("message").isNotEmpty()) {
            return null
        }

        return extractWork(article)
    }

    private fun parseBookmarkSymbol(article: Element): BookmarkType? {
        val status = article.getElementsByClass("status").getOrNull(0)
        if (status != null)
            return parseBookmarkType(
                status
                    .getElementsByClass("symbol")[0]
                    .getElementsByTag("span")[0]
                    .attr("class")
            )
        return null
    }


    private fun parseUserSection(article: Element): BookmarkUserSection {
        val userSection = article.getElementsByClass("user")[0]
        val creator = userSection.getElementsByClass("byline")[0].getElementsByTag("a")[0].attr("href")
        var bookmarkerTags: List<String>? = null
        userSection.getElementsByClass("meta").getOrNull(1)?.let { listItem ->
            bookmarkerTags = listItem.getElementsByTag("li").map { element ->  element.text() }
        }
        return BookmarkUserSection(
            creator = Creator(authorUserRegex.getRegexFound(creator, ""), bookmarkerPseudo.getRegexFound(creator, "")),
            notes = userSection.getElementsByClass("notes").text().orEmpty(),
            bookmarkerTags = bookmarkerTags.orEmpty(),
            bookmarkDate = ddMMMYYYY.parse(userSection.getElementsByClass("datetime").text())
        )
    }

    /**
     * Get the current page and the amount of pages from a search
     * @return pair.first = current page, pair.second = total pages
     */
    private fun getPages(pagination: Element?): Pair<Int, Int> {
        if (pagination == null)
            return Pair(1, 1)
        val paginationLinks =  pagination.getElementsByTag("a")
        return Pair(
            pagination.getElementsByClass("current")[0].text().toInt(),
            paginationLinks[paginationLinks.size - 2].text().toInt()
        )
    }
}