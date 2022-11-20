package dev.chieppa.wrapper.model.searchqueries

import dev.chieppa.wrapper.constants.workproperties.SearchQueryParam
import dev.chieppa.wrapper.util.encode
import dev.chieppa.wrapper.util.encodeParameter

abstract class SearchQuery {
    companion object {
        val SUFFIX: String = "utf8=${"✓".encode()}"
    }

    fun StringBuilder.appendParameter(
        header: String,
        searchQueryParam: SearchQueryParam,
        value: String,
        array: Boolean = false
    ) {
        value.let {
            this.append(encodeSearchQuery(header, searchQueryParam, value, array))
        }
    }

    private fun encodeSearchQuery(
        header: String,
        searchQueryParam: SearchQueryParam,
        value: String,
        array: Boolean = false
    ) = encodeParameter("$header[${searchQueryParam.raw}]", value, array) + "&"
}