package model

import constants.SearchDomain
import constants.work_properties.*
import constants.work_properties.SearchQueryParam.*
import util.encode

data class SearchQuery(
    val query: String? = null,
    val title: String? = null,
    val creator: String? = null,
    val revisedAt: String? = null,
    val completionStatus: CompletionStatus? = null,
    val crossover: Crossover? = null,
    val single_chapter: Multichapter? = null,
    val wordCount: String? = null,
    val language: Language? = null,
    val fandoms: String? = null,
    val rating: ContentRating? = null,
    val warning: Set<ContentWarning>? = null,
    val category: Set<Category>? = null,
    val characters: String? = null,
    val relationships: String? = null,
    val additionalTags: String? = null,
    val hits: String? = null,
    val kudos: String? = null,
    val comments: String? = null,
    val bookmarks: String? = null,
    val sortColumn: SortColumn? = null,
    val sortDirection: SortDirection? = null,
) {

    companion object {
        val SUFFIX: String = "utf8=${"✓".encode()}"
    }

    fun searchString(domain: SearchDomain = SearchDomain.WORK_SEARCH): String =
        searchString(domain.search_param).append(SUFFIX).toString()

    private var searchString = { header: String ->
        val searchQuery = StringBuilder()
        with(searchQuery) {
            appendParameter(header, QUERY, query)
            appendParameter(header, TITLE, title)
            appendParameter(header, CREATOR, creator)
            appendParameter(header, REVISED_AT, revisedAt)
            appendParameter(header, COMPLETE, completionStatus?.search_param)
            appendParameter(header, CROSSOVER, crossover?.search_param)
            appendParameter(header, SINGLE_CHAPTER, single_chapter?.search_param?.toString())
            appendParameter(header, WORD_COUNT, wordCount)
            appendParameter(header, LANGUAGE, language?.search_param)
            appendParameter(header, FANDOM, fandoms)
            appendParameter(header, RATING, rating?.search_param?.toString())
            appendParameter(header, CHARACTER_NAME, characters)
            appendParameter(header, RELATIONSHIP_NAMES, relationships)
            appendParameter(header, ADDITIONAL_TAGS, additionalTags)
            appendParameter(header, HITS, hits)
            appendParameter(header, KUDOS, kudos)
            appendParameter(header, COMMENTS, comments)
            appendParameter(header, BOOKMARKS, bookmarks)
            appendParameter(header, COLUMN, sortColumn?.search_param)
            appendParameter(header, DIRECTION, sortDirection?.search_param)
            warning?.stream()?.forEach {
                appendParameter(header, WARNING, it.search_param.toString(), true)
            }
            category?.stream()?.forEach {
                appendParameter(header, CATEGORY, it.search_param.toString(), true)
            }
        }
        searchQuery
    }

    private fun StringBuilder.appendParameter(
        header: String,
        searchQueryParam: SearchQueryParam,
        value: String?,
        array: Boolean = false
    ) {
        value?.let {
            this.append(encodeSearchQuery(header, searchQueryParam, value, array))
        }
    }

    private fun encodeSearchQuery(
        header: String,
        searchQueryParam: SearchQueryParam,
        value: String,
        array: Boolean = false
    ) = util.encodeParameter("$header[${searchQueryParam.raw}]", value, array) + "&"

}