package wrapper

import constants.AutoCompleteField
import constants.ao3_session_cookie
import constants.work_properties.SortColumn
import constants.work_properties.SortDirection
import exception.InvalidLoginException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import model.result.AutoCompleteResult
import model.result.ChapterQueryResult
import model.result.SearchResult
import model.result.TagSortAndFilterResult
import model.searchQuries.FilterWorkSearchQuery
import model.searchQuries.TagSearchQuery
import model.searchQuries.WorkSearchQuery
import model.searchQuries.buildTagSearchQuery
import model.user.Session
import model.work.Work
import util.*
import wrapper.parser.*
import java.net.HttpCookie

class AO3Wrapper(
    private val httpClient: HttpClient = HttpClient { ao3HttpClientConfig("generic-ao3-wrapper") },
    private val locations: LinkLocations = LinkLocations()
) : Logging, Closeable {

    /**
     * parser for the search results
     */
    var searchWrapper = Wrapper(SearchParser())

    /**
     *
     */
    var sortAndFilterWrapper = Wrapper(SortAndFilterParser())


    /**
     * parser for work chapter navigator
     */
    var chapterNavigationWrapper = Wrapper(ChapterParser())

    /**
     * parser for the auto complete results
     */
    var autoCompleteWrapper = Wrapper(AutoCompleteParser())

    /**
     * parser for the login screen
     */
    var loginWrapper = Wrapper(LoginPageParser())

    /**
     * Perform a search
     *
     * @param workSearchQuery: a query
     * @param session: a session that has permission to view the works in the listed query
     * @param page: the page of results to return
     */
    suspend fun search(workSearchQuery: WorkSearchQuery, session: Session? = null, page: Int = 1): SearchResult {
        val response: HttpResponse =
            httpClient.request(locations.search_loc(workSearchQuery.workSearchString(), page)) {
                method = HttpMethod.Get
                session?.let {
                    with(it) {
                        setSessionCookies()
                    }
                }
            }

        return searchWrapper.read(response.receive())
    }

    suspend fun bookmarkSearch() {
    }

    suspend fun tagSearch() {
    }

    suspend fun peopleSearch() {
    }

    suspend fun browseTag() {
    }

    suspend fun sortAndFilterTags(
        tagId: String,
        include: TagSearchQuery,
        exclude: TagSearchQuery,
        workSearchQuery: FilterWorkSearchQuery,
        session: Session? = null,
        page: Int = 1
    ): TagSortAndFilterResult {
        val response: HttpResponse =
            httpClient.get(locations.filter_loc(buildTagSearchQuery(tagId, include, exclude, workSearchQuery), page)) {
                session?.let {
                    with(session) {
                        setSessionCookies()
                    }
                }
            }

        return sortAndFilterWrapper.parser.parsePage(response.receive())
    }

    suspend fun sortAndFilterBookmarks() {
    }

    suspend fun sortAndFilterCollection() {
    }

    suspend fun sortAndFilterUserWorks() {
    }

    suspend fun getUserSeries() {
    }

    suspend fun getUserCollections() {
    }

    suspend fun getUserGifts() {
    }

    suspend fun getUserProfile() {
    }

    suspend fun getUserDashboard() {
    }


    /**
     * Get a list of works by supplied ID
     *
     * @param workIds: a list of workIds to query for
     * @param session: a user session that has access to these works
     * @param sortColumn: the column to sort the results on
     * @param sortDirection: the direction to sort the results
     *
     * @return a list of works, this function does not guarantee that a work exists for each id
     */
    suspend fun getWorksByID(
        workIds: List<Int>,
        session: Session? = null,
        sortColumn: SortColumn = SortColumn.DATE_UPDATED,
        sortDirection: SortDirection = SortDirection.DESCENDING
    ): List<Work> {
        val works: MutableList<Work> = mutableListOf()
        val searchQuery = createIDSearchString(workIds)
        var currentPage = 1
        var endPage: Int
        do {
            val result = search(
                WorkSearchQuery(query = searchQuery, sortColumn = sortColumn, sortDirection = sortDirection),
                session, currentPage
            )
            works.addAll(result.works)
            endPage = result.pages
            currentPage++
        } while (currentPage < endPage)
        return works
    }

    /**
     * Get a suggested autocomplete from an ao3 feild
     *
     * @param autoCompleteField: The type of autocomplete to return
     * @param field: the search to base the autocomplete on
     *
     * @return a list of suggested completions for field
     */
    suspend fun suggestAutoComplete(autoCompleteField: AutoCompleteField, field: String): List<AutoCompleteResult> {
        val response: HttpResponse =
            httpClient.get(locations.auto_complete(autoCompleteField.search_param, field.encode()))
        return autoCompleteWrapper.read(response.receive())
    }

    /**
     * Get the chapters for a given work
     *
     * @param work: the work to retrieve chapters for
     * @param session: a user session that has access to this work
     *
     * @return ChapterQueryResult containing the requested information
     */
    suspend fun getChapters(work: Work, session: Session? = null): ChapterQueryResult {
        return getChapters(work.workId, session)
    }

    /**
     * Get chapters by the WorkId
     * @see getChapters
     */
    suspend fun getChapters(workId: Int, session: Session? = null): ChapterQueryResult {
        val response: HttpResponse = httpClient.get(locations.chapter_navigation(workId)) {
            session?.let {
                with(it) {
                    setSessionCookies()
                }
            }
        }
        return chapterNavigationWrapper.read(response.receive())
    }

    /**
     * Login - Create a Session for AO3
     *
     * @param username plaintext
     * @param password plaintext
     * @param rememberMe if true the session token should work across sessions
     * @return a logged in user session
     * @throws InvalidLoginException username or password is incorrect, or ao3 did not return a login indicator
     */
    @Throws(InvalidLoginException::class)
    suspend fun login(username: String, password: String, rememberMe: Boolean = false): Session {

        /**
         * I have to get a real login page to acquire a rails token
         */
        val response: HttpResponse = httpClient.get(locations.login_loc)

        val authToken: String = loginWrapper.read(response.receive())

        val cookies: MutableMap<String, HttpCookie> = mutableMapOf()
        response.headers.getAll(HttpHeaders.SetCookie)?.forEach {
            HttpCookie.parse(it)[0].also { cookie ->
                cookies[cookie.name] = cookie
            }
        }

        val loginResponse: HttpResponse = httpClient.post(locations.login_loc) {
            contentType(ContentType.Application.FormUrlEncoded)
            body = loginForm(username, password, authToken, rememberMe)
            cookies[ao3_session_cookie]?.let { setCookie(ao3_session_cookie, it.value) }
        }

        if (loginResponse.status.value != HttpStatusCode.Found.value) {
            throw InvalidLoginException()
        }

        val session = Session()
        loginResponse.headers.getAll(HttpHeaders.SetCookie)?.forEach {
            HttpCookie.parse(it)[0].also { cookie ->
                when (cookie.name) {
                    "_otwarchive_session" -> session.session_id = cookie.value
                    "remember_user_token" -> session.remember_user_token = cookie.value
                    "user_credentials" -> session.userCredentials = cookie.value
                    "flash_is_set" -> {
                    }
                    else -> logger().warn("unused cookie ${cookie.name}, ${cookie.value}")
                }
            }
        }

        return session
    }

    /**
     * Check to see if this session is still signed in
     *
     * @param session user session
     * @return true if this user is being recognized as logged in
     */
    suspend fun validateSession(session: Session): Boolean {
        val response: HttpResponse = httpClient.get(locations.login_loc) {
            with(session) {
                setSessionCookies()
            }
        }

        return response.status == HttpStatusCode.Found
    }

    override fun close() {
        httpClient.close()
    }

    private fun createSearchStringForWorks(works: List<Work>): String {
        return createIDSearchString(works.map { work -> work.workId })
    }

    private fun createIDSearchString(workIds: List<Int>): String {
        return workIds.joinToString(" OR ") { "id:${it}" }
    }
}

fun HttpClientConfig<*>.ao3HttpClientConfig(userAgent: String) {
    expectSuccess = false
    followRedirects = false

    install(UserAgent) {
        agent = userAgent
    }
}