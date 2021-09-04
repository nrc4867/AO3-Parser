package model.searchQuries

import constants.SearchDomain
import constants.work_properties.BookmarkSortColumn
import constants.work_properties.Language
import constants.work_properties.SearchBoolean
import constants.work_properties.SearchQueryParam.*

data class BookmarkQuery(
    val query: String? = null,
    val otherTags: List<String>? = null,
    val language: Language? = null,
    val bookmarkedDate: String? = null,
    val bookmarkQuery: String? = null,
    val bookmarkerTags: List<String>? = null,
    val bookmarkerNotes: String? = null,
    val recommendation: SearchBoolean? = null,
    val withNotes: SearchBoolean? = null,
    val date: String? = null,
    val sortColumn: BookmarkSortColumn? = null
) : SearchQuery() {

    fun bookmarkQuery(stringBuilder: StringBuilder) {
        val header = SearchDomain.BOOKMARK_SEARCH.search_param
        with(stringBuilder) {
            query?.let { appendParameter(header, BOOKMARKABLE_QUERY, it) }
            otherTags?.let { appendParameter(header, OTHER_TAGS_NAMES, it.joinToString(",")) }
            language?.let { appendParameter(header, LANGUAGE, it.search_param) }
            bookmarkedDate?.let { appendParameter(header, BOOKMARKABLE_DATE, it) }
            bookmarkQuery?.let { appendParameter(header, BOOKMARK_QUERY, it) }
            bookmarkerTags?.let { appendParameter(header, OTHER_BOOKMARK_TAG_NAMES, it.joinToString(",")) }
            bookmarkerNotes?.let { appendParameter(header, BOOKMARKER, it) }
            recommendation?.let { appendParameter(header, REC, it.search_param.toString()) }
            withNotes?.let { appendParameter(header, WITH_NOTES, it.search_param.toString()) }
            date?.let { appendParameter(header, DATE, it) }
            sortColumn?.let { appendParameter(header, COLUMN, it.search_param) }
        }
    }

}

fun bookmarkSearch(bookmarkQuery: BookmarkQuery) = bookmarkQuery.bookmarkQuery(StringBuilder())


