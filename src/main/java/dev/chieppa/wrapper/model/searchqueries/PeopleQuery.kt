package dev.chieppa.wrapper.model.searchqueries

import dev.chieppa.wrapper.constants.SearchDomain
import dev.chieppa.wrapper.constants.workproperties.SearchQueryParam.*

data class PeopleQuery(val query: String? = null, val names: List<String>? = null, val fandoms: List<String>?= null) : SearchQuery() {

    fun peopleQuery(stringBuilder: StringBuilder) {
        val header = SearchDomain.PEOPLE_SEARCH.search_param
        with(stringBuilder) {
            query?.let { appendParameter(header, QUERY, it) }
            names?.let { appendParameter(header, NAME, it.joinToString(",")) }
            fandoms?.let { appendParameter(header, FANDOM, it.joinToString(",")) }
        }
    }
}

fun peopleSearch(query: PeopleQuery): String {
    val stringBuilder = StringBuilder()
    query.peopleQuery(stringBuilder)
    return stringBuilder.toString()
}
