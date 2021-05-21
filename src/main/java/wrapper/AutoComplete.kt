package wrapper

import constants.AO3Constant
import constants.AutoCompleteField
import model.result.AutoCompleteResult
import wrapper.parser.AutoCompleteParser
import wrapper.parser.Parser
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class AutoComplete(
    private val base_loc: String = AO3Constant.ao3_url,
    private val search_loc: (String, String) -> String = AO3Constant.ao3_autocomplete,
    private val parser: Parser<List<AutoCompleteResult>> = AutoCompleteParser()) {

    fun suggestAutoComplete(autoCompleteField: AutoCompleteField, userTerm: String): List<AutoCompleteResult> {
        val location = URL(base_loc + search_loc(
            autoCompleteField.search_param,
            URLEncoder.encode(userTerm, Charsets.UTF_8)
        ))
        val conn: HttpURLConnection = location.openConnection() as HttpURLConnection
        val autoCompleteResult = ArrayList<AutoCompleteResult>()

         conn.inputStream.bufferedReader().use {
            parser.parsePage(it.readText())
        }

        return autoCompleteResult
    }

}

fun main() {
    AutoComplete().suggestAutoComplete(AutoCompleteField.CHARACTER, "a")
}