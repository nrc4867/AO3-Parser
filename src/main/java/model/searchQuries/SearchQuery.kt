package model.searchQuries

import constants.work_properties.SearchQueryParam
import util.encode

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
    ) = util.encodeParameter("$header[${searchQueryParam.raw}]", value, array) + "&"
}