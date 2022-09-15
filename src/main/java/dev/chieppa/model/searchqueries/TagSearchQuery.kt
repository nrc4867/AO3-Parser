package dev.chieppa.model.searchqueries

import dev.chieppa.constants.SearchDomain
import dev.chieppa.constants.SearchDomain.EXCLUDE_WORK_SEARCH
import dev.chieppa.constants.SearchDomain.INCLUDE_WORK_SEARCH
import dev.chieppa.constants.workproperties.*
import dev.chieppa.constants.workproperties.SearchQueryParam.*
import dev.chieppa.model.result.work.SearchDate
import dev.chieppa.util.encode


data class FilterWorkSearchQuery(
    val sortColumn: SortColumn? = null,
    val otherIncludes: List<String>? = null,
    val otherExcludes: List<String>? = null,
    val crossover: Crossover? = null,
    val complete: CompletionStatus? = null,
    val wordsFrom: Int? = null,
    val wordsTo: Int? = null,
    val dateFrom: SearchDate? = null,
    val dateTo: SearchDate? = null,
    val query: String? = null,
    val language: Language? = null
) : SearchQuery() {

    fun filterQuery(searchQuery: StringBuilder) {
        val header = SearchDomain.WORK_SEARCH.search_param
        with(searchQuery) {
            sortColumn?.let { appendParameter(header, COLUMN, it.search_param) }
            otherIncludes?.let { appendParameter(header, OTHER_TAGS_NAMES, it.joinToString(",")) }
            otherExcludes?.let { appendParameter(header, EXCLUDED_TAGS_NAMES, it.joinToString(",")) }
            crossover?.let { appendParameter(header, CROSSOVER, it.search_param) }
            complete?.let { appendParameter(header, COMPLETE, it.search_param) }
            wordsFrom?.let { appendParameter(header, WORDS_FROM, it.toString()) }
            wordsTo?.let { appendParameter(header, WORDS_TO, it.toString()) }
            dateFrom?.let { appendParameter(header, DATE_FROM, it.toString()) }
            dateTo?.let { appendParameter(header, DATE_TO, it.toString()) }
            query?.let { appendParameter(header, QUERY, it) }
            language?.let { appendParameter(header, LANGUAGE, it.search_param) }
        }
    }

}

data class TagSearchQuery(
    val ratingIds: List<ContentRating>? = null,
    val contentWarnings: List<ContentWarning>? = null,
    val categories: List<Category>? = null,
    val fandomIds: List<Int>? = null,
    val characterIds: List<Int>? = null,
    val relationshipIds: List<Int>? = null,
    val freeFormIds: List<Int>? = null
) : SearchQuery() {

    fun tagQuery(searchQuery: StringBuilder, searchDomain: SearchDomain) {
        val header = searchDomain.search_param
        with(searchQuery) {
            ratingIds?.forEach { appendParameter(header, RATING, it.search_param.toString(), true) }
            contentWarnings?.forEach { appendParameter(header, WARNING, it.search_param.toString(), true) }
            categories?.forEach { appendParameter(header, CATEGORY, it.search_param.toString(), true) }
            fandomIds?.forEach { appendParameter(header, FANDOM_ID, it.toString(), true) }
            characterIds?.forEach { appendParameter(header, CHARACTER_ID, it.toString(), true) }
            relationshipIds?.forEach { appendParameter(header, RELATIONSHIP_ID, it.toString(), true) }
            freeFormIds?.forEach { appendParameter(header, ADDITIONAL_TAGS_IDS, it.toString(), true) }
        }
    }

}

private fun buildQuery(
    searchString: StringBuilder,
    include: TagSearchQuery,
    exclude: TagSearchQuery,
    workSearch: FilterWorkSearchQuery
) {
    workSearch.filterQuery(searchString)
    include.tagQuery(searchString, INCLUDE_WORK_SEARCH)
    exclude.tagQuery(searchString, EXCLUDE_WORK_SEARCH)
    searchString.append("&${SearchQuery.SUFFIX}")
}

fun buildTagSearchQuery(
    tagId: String,
    include: TagSearchQuery,
    exclude: TagSearchQuery,
    workSearch: FilterWorkSearchQuery
): String {
    val searchString = StringBuilder()
    buildQuery(searchString, include, exclude, workSearch)
    searchString.append("&tag_id=${tagId.encode()}")
    return searchString.toString()
}

fun buildUserSearchQuery(
    user: String,
    pseudonym: String?,
    include: TagSearchQuery,
    exclude: TagSearchQuery,
    workSearch: FilterWorkSearchQuery
): String {
    val searchString = StringBuilder()
    buildQuery(searchString, include, exclude, workSearch)
    searchString.append("&user_id=${user.encode()}")
    pseudonym?.let { searchString.append("&pseud_id=${it.encode()}") }
    return searchString.toString()
}

fun buildUserBookmarkSearchQuery(
    user: String,
    pseudonym: String?,
    bookmarkQuery: BookmarkQuery
): String {
    val searchString = StringBuilder()
    bookmarkQuery.bookmarkQuery(searchString)
    searchString.append("&user_id=${user.encode()}")
    pseudonym?.let { searchString.append("&pseud_id=${it.encode()}") }
    return searchString.toString()
}