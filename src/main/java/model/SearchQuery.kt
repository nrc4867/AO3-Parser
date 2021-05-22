package model

import constants.work_properties.*
import constants.work_properties.SearchQueryParam.*
import util.Utils.encode
import util.Utils.encodeParameter

class SearchQuery(
    query: String? = null,
    title: String? = null,
    creator: String? = null,
    revisedAt: String? = null,
    completionStatus: CompletionStatus? = null,
    crossover: Crossover? = null,
    single_chapter: Multichapter? = null,
    wordCount: String? = null,
    language: Language? = null,
    fandoms: String? = null,
    rating: ContentRating? = null,
    warning: Set<ContentWarning>? = null,
    category: Set<Category>? = null,
    characters: String? = null,
    relationships: String? = null,
    additionalTags: String? = null,
    hits: String? = null,
    kudos: String? = null,
    comments: String? = null,
    bookmarks: String? = null,
    sortColumn: SortColumn? = null,
    sortDirection: SortDirection? = null,
) {

    companion object {
        val SUFFIX: String = "utf8=${encode("✓")}"
    }

    private val searchString = { header: String ->
        val sb = StringBuilder()
        query?.let {
            sb.append(encodeSearchQuery(header, QUERY, it))
        }
        title?.let {
            sb.append(encodeSearchQuery(header, TITLE, it))
        }
        creator?.let {
            sb.append(encodeSearchQuery(header, CREATOR, it))
        }
        revisedAt?.let {
            sb.append(encodeSearchQuery(header, REVISED_AT, it))
        }
        completionStatus?.let {
            sb.append(encodeSearchQuery(header, COMPLETE, it.search_param))
        }
        crossover?.let {
            sb.append(encodeSearchQuery(header, CROSSOVER, it.search_param))
        }
        single_chapter?.let {
            sb.append(encodeSearchQuery(header, SINGLE_CHAPTER, it.search_param.toString()))
        }
        wordCount?.let {
            sb.append(encodeSearchQuery(header, WORD_COUNT, it))
        }
        language?.let {
            sb.append(encodeSearchQuery(header, LANGUAGE, it.search_param))
        }
        fandoms?.let {
            sb.append(encodeSearchQuery(header, FANDOM, it))
        }
        rating?.let {
            sb.append(encodeSearchQuery(header, RATING, it.search_param.toString()))
        }
        warning?.let {
            for (contentWarningValue in it)
                sb.append(encodeSearchQuery(header, WARNING, contentWarningValue.search_param.toString(), true))
        }
        category?.let {
            for (categoryValue in it)
                sb.append(encodeSearchQuery(header, CATEGORY, categoryValue.search_param.toString(), true))
        }
        characters?.let {
            sb.append(encodeSearchQuery(header, CHARACTER_NAME, it))
        }
        relationships?.let {
            sb.append(encodeSearchQuery(header, RELATIONSHIP_NAMES, it))
        }
        additionalTags?.let {
            sb.append(encodeSearchQuery(header, ADDITIONAL_TAGS, it))
        }
        hits?.let {
            sb.append(encodeSearchQuery(header, HITS, it))
        }
        kudos?.let {
            sb.append(encodeSearchQuery(header, KUDOS, it))
        }
        comments?.let {
            sb.append(encodeSearchQuery(header, COMMENTS, it))
        }
        bookmarks?.let {
            sb.append(encodeSearchQuery(header, BOOKMARKS, it))
        }
        sortColumn?.let {
            sb.append(encodeSearchQuery(header, COLUMN, it.search_param))
        }
        sortDirection?.let {
            sb.append(encodeSearchQuery(header, DIRECTION, it.search_param))
        }
        sb
    }

    private fun encodeSearchQuery(
        header: String,
        searchQueryParam: SearchQueryParam,
        value: String,
        array: Boolean = false
    ) = encodeParameter("$header[${searchQueryParam.raw}]", value, array) + "&"

    fun searchString(include: Boolean = true): String {
        val builder = searchString("${if (include) "" else "exclude_"}work_search")
        builder.append(SUFFIX)
        return builder.toString()
    }

}
