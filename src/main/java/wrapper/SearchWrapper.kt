package wrapper

import constants.AO3Constant
import constants.work_properties.Category
import constants.work_properties.SortColumn
import constants.work_properties.SortDirection
import model.SearchQuery
import model.result.SearchResult
import model.user.Session
import model.work.Work
import wrapper.parser.Parser
import wrapper.parser.SearchParser
import java.io.InputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class SearchWrapper(
    private val base_loc: String = AO3Constant.ao3_url,
    private val search_loc: String = AO3Constant.ao3_search,
    private val parser: Parser<SearchResult> = SearchParser(),
) {

    fun search(
        searchQuery: SearchQuery,
        session: Session? = null,
        page: Int = 1,
    ): SearchResult {
        val location = URL(base_loc + search_loc + "page=$page&" + searchQuery.searchString())
        val conn: HttpsURLConnection = location.openConnection() as HttpsURLConnection
        conn.setRequestProperty("Cookie", session?.getCookie() ?: "")

        return search(conn.inputStream)
    }

    fun search(inputStream: InputStream) : SearchResult {
        lateinit var searchResult: SearchResult
        inputStream.bufferedReader().use {
            searchResult = parser.parsePage(it.readText())
        }
        return searchResult
    }

    fun checkUpdate(work: List<Work>): List<Work> {
        return emptyList()
    }

    fun forceUpdate(work: Work): Work {
        return Work.emptyWork()
    }
}

fun main(args: Array<String>) {
    println(SearchWrapper().search(SearchQuery(category = setOf(Category.OTHER),
        sortColumn = SortColumn.AUTHOR,
        sortDirection = SortDirection.ASCENDING), page = 3))
}