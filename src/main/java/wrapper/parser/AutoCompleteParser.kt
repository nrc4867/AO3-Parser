package wrapper.parser

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.result.AutoCompleteResult

class AutoCompleteParser : Parser<List<AutoCompleteResult>> {

    override fun parsePage(queryResponse: String): List<AutoCompleteResult> {
        return Json.decodeFromString<ArrayList<AutoCompleteResult>>(queryResponse)
    }
}