package model

import constants.work_properties.*
import constants.work_properties.SearchQueryParam.*
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Consumer

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
        fun encode(str: String, charset: Charset = StandardCharsets.UTF_8): String {
            return URLEncoder.encode(str, charset)
        }

        val OPEN_BRACE: String = encode("[")
        val CLOSED_BRACE: String = encode("]")
        val SUFFIX: String = "utf8=${encode("✓")}" //&commit=Search"
    }

    private val queryParamMap: EnumMap<SearchQueryParam, String> = EnumMap(SearchQueryParam::class.java)
    private val categoricalQueryParamMap: EnumMap<SearchQueryParam, Set<String>> = EnumMap(SearchQueryParam::class.java)

    init {
        queryParamMap[QUERY] = query
        queryParamMap[TITLE] = title
        queryParamMap[CREATOR] = creator
        queryParamMap[REVISED_AT] = revisedAt
        queryParamMap[COMPLETE] = completionStatus?.search_param
        queryParamMap[CROSSOVER] = crossover?.search_param
        queryParamMap[SINGLE_CHAPTER] = single_chapter?.search_param?.toString()
        queryParamMap[WORD_COUNT] = wordCount
        queryParamMap[LANGUAGE] = language?.search_param
        queryParamMap[FANDOM] = fandoms
        queryParamMap[RATING] = rating?.search_param?.toString()
        categoricalQueryParamMap[WARNING] = warning?.map { it.search_param.toString() }?.toSet()
        categoricalQueryParamMap[CATEGORY] = category?.map { it.search_param.toString() }?.toSet()
        queryParamMap[CHARACTER_NAME] = characters
        queryParamMap[RELATIONSHIP_NAMES] = relationships
        queryParamMap[ADDITIONAL_TAGS] = additionalTags
        queryParamMap[HITS] = hits
        queryParamMap[KUDOS] = kudos
        queryParamMap[COMMENTS] = comments
        queryParamMap[BOOKMARKS] = bookmarks
        queryParamMap[COLUMN] = sortColumn?.search_param
        queryParamMap[DIRECTION] = sortDirection?.search_param
    }

    fun searchString(include: Boolean = true): String {
        val builder = StringBuilder()
        queryParamMap.forEach { (parameter, value) ->
            if (value != null)
                builder.append("${if (include) "" else "exclude_"}work_search")
                    .append(OPEN_BRACE)
                    .append(parameter.raw)
                    .append("$CLOSED_BRACE=")
                    .append(encode(value))
                    .append("&")
        }
        categoricalQueryParamMap.forEach { (parameter, value) ->
            value?.forEach(Consumer {
                builder.append("${if (include) "" else "exclude_"}work_search")
                    .append(OPEN_BRACE)
                    .append(parameter.raw)
                    .append("$CLOSED_BRACE$OPEN_BRACE$CLOSED_BRACE=")
                    .append(encode(it))
                    .append("&")
            })
        }
        builder.append(SUFFIX)
        return builder.toString()
    }


}
