package dev.chieppa.wrapper.parser

import dev.chieppa.wrapper.model.result.AutoCompleteResult
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AutoCompleteParser : Parser<List<AutoCompleteResult>> {

    override fun parsePage(queryResponse: String): List<AutoCompleteResult> {
        return Json.decodeFromString<ArrayList<AutoCompleteResult>>(queryResponse)
    }
}