package dev.chieppa.wrapper.parser

import dev.chieppa.model.result.BookmarkSearchResult
import dev.chieppa.model.result.filterSidebar.BookmarkSortAndFilterResult
import dev.chieppa.model.result.filterSidebar.Recommendation.*
import org.jsoup.Jsoup

class UserBookmarkParser(private val bookmarkParser: Parser<BookmarkSearchResult>) : Parser<BookmarkSortAndFilterResult> {

    override fun parsePage(queryResponse: String): BookmarkSortAndFilterResult {
        // TODO: Find a way to get the amount of results found without copying tons of code
        val bookMarkResult = bookmarkParser.parsePage(queryResponse)

        val mainBody = Jsoup.parse(queryResponse).byIDOrThrow("main")

        return BookmarkSortAndFilterResult(
            bookMarkResult,
            extractSortAndFilterSidebar(
                bookMarkResult.found,
                mainBody,
                setOf(RELATIONSHIP, FREEFORM, FANDOM, CHARACTER, BOOKMARKER)
            ),
        )
    }

}