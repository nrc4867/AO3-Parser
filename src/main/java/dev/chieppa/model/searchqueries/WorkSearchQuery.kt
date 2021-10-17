package dev.chieppa.model.searchqueries

import dev.chieppa.constants.SearchDomain
import dev.chieppa.constants.workproperties.*
import dev.chieppa.constants.workproperties.SearchQueryParam.*

data class WorkSearchQuery(
    val query: String? = null,
    val title: String? = null,
    val creator: String? = null,
    val revisedAt: String? = null,
    val completionStatus: CompletionStatus? = null,
    val crossover: Crossover? = null,
    val single_chapter: Multichapter? = null,
    val wordCount: String? = null,
    val language: Language? = null,
    val fandoms: List<String>? = null,
    val rating: ContentRating? = null,
    val warning: Set<ContentWarning>? = null,
    val category: Set<Category>? = null,
    val characters: List<String>? = null,
    val relationships: List<String>? = null,
    val additionalTags: List<String>? = null,
    val hits: String? = null,
    val kudos: String? = null,
    val comments: String? = null,
    val bookmarks: String? = null,
    val sortColumn: SortColumn? = null,
    val sortDirection: SortDirection? = null,
): SearchQuery() {

    fun workSearchString(): String = workSearchString(SearchDomain.WORK_SEARCH.search_param).append(SUFFIX).toString()

    private fun workSearchString(header: String): StringBuilder {
        val searchQuery = StringBuilder()
        with(searchQuery) {
            query?.let { appendParameter(header, QUERY, it) }
            title?.let { appendParameter(header, TITLE, it) }
            creator?.let { appendParameter(header, CREATOR, it) }
            revisedAt?.let { appendParameter(header, REVISED_AT, it) }
            completionStatus?.let { appendParameter(header, COMPLETE, it.search_param) }
            crossover?.let { appendParameter(header, CROSSOVER, it.search_param) }
            single_chapter?.let { appendParameter(header, SINGLE_CHAPTER, it.search_param.toString()) }
            wordCount?.let { appendParameter(header, WORD_COUNT, it) }
            language?.let { appendParameter(header, LANGUAGE, it.search_param) }
            fandoms?.let { appendParameter(header, FANDOM, it.joinToString(",")) }
            rating?.let { appendParameter(header, RATING, it.search_param.toString()) }
            characters?.let { appendParameter(header, CHARACTER_NAME, it.joinToString(",")) }
            relationships?.let { appendParameter(header, RELATIONSHIP_NAMES, it.joinToString(",")) }
            additionalTags?.let { appendParameter(header, ADDITIONAL_TAGS, it.joinToString(",")) }
            hits?.let { appendParameter(header, HITS, it) }
            kudos?.let { appendParameter(header, KUDOS, it) }
            comments?.let { appendParameter(header, COMMENTS, it) }
            bookmarks?.let { appendParameter(header, BOOKMARKS, it) }
            sortColumn?.let { appendParameter(header, COLUMN, it.search_param) }
            sortDirection?.let { appendParameter(header, DIRECTION, it.search_param) }
            warning?.stream()?.forEach {
                appendParameter(header, WARNING, it.search_param.toString(), true)
            }
            category?.stream()?.forEach {
                appendParameter(header, CATEGORY, it.search_param.toString(), true)
            }
        }
        return searchQuery
    }



}