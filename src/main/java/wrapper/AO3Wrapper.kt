package wrapper

import constants.AutoCompleteField
import constants.ao3_chapter
import constants.ao3_session_cookie
import constants.workproperties.SortColumn
import constants.workproperties.SortDirection
import exception.loginexception.InvalidLoginException
import exception.queryexception.ChapterDoesNotExistException
import exception.queryexception.WorkDoesNotExistException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import model.Session
import model.result.AutoCompleteResult
import model.result.PersonResult
import model.result.SearchResult
import model.result.bookmark.BookmarkSearchResult
import model.result.chapter.ChapterNavigationResult
import model.result.chapter.ChapterResult
import model.result.chapter.FullChapterInfo
import model.result.filterSidebar.TagSortAndFilterResult
import model.result.work.Work
import model.searchqueries.*
import mu.KotlinLogging
import util.encode
import util.loginForm
import util.setCookie
import wrapper.parser.*
import java.net.HttpCookie

class AO3Wrapper(
    private val httpClient: HttpClient = HttpClient { ao3HttpClientConfig("generic-ao3-wrapper") },
    private val locations: LinkLocations = LinkLocations()
) : Closeable {

    private val logger = KotlinLogging.logger {  }

    /**
     * parser for the search results
     */
    var searchParser = SearchParser()

    /**
     * parser for the filtered results
     */
    var sortAndFilterParser = SortAndFilterParser()

    /**
     * parser for work chapter navigator
     */
    var chapterNavigationParser = ChapterNavigationParser()

    /**
     * parser for the auto complete results
     */
    var autoCompleteParser = AutoCompleteParser()

    /**
     * parser for the login screen
     */
    var loginParser = LoginPageParser()

    /**
     * parser for the person search
     */
    var personWrapper = PersonParser()

    /**
     * parser for the bookmark search
     */
    var bookmarkParser = BookmarkParser()

    /**
     * Parser for chapter query
     */
    var chapterParser = ChapterParser()

    /**
     * Perform a search
     *
     * @param workSearchQuery: a query
     * @param session: a session that has permission to view the works in the listed query
     * @param page: the page of results to return
     */
    suspend fun search(workSearchQuery: WorkSearchQuery, session: Session? = null, page: Int = 1): SearchResult {
        val response: HttpResponse =
            httpClient.getWithSession(locations.search_loc(workSearchQuery.workSearchString(), page), session)
        return searchParser.parsePage(response.receive())
    }

    /**
     * perform a search on a tag filter
     *
     * @param tagId: the tag to filter on
     * @param include: filters to include
     * @param exclude: filters to exclude
     * @param workSearchQuery: a query
     * @param session: a session that has permission to view the works in the listed query
     * @param page: the page of results to return
     */
    suspend fun sortAndFilterTags(
        tagId: String,
        include: TagSearchQuery,
        exclude: TagSearchQuery,
        workSearchQuery: FilterWorkSearchQuery,
        session: Session? = null,
        page: Int = 1
    ): TagSortAndFilterResult {
        val response: HttpResponse =
            httpClient.getWithSession(
                locations.filter_loc(
                    buildTagSearchQuery(
                        tagId,
                        include,
                        exclude,
                        workSearchQuery
                    ), page
                ), session
            )
        return sortAndFilterParser.parsePage(response.receive())
    }

    suspend fun searchPeople(peopleQuery: PeopleQuery, session: Session? = null, page: Int = 1): List<PersonResult> {
        val response: HttpResponse = httpClient.getWithSession("", session)
        return personWrapper.parsePage(response.receive())
    }

    suspend fun searchBookmarks(
        bookmarkQuery: BookmarkQuery,
        session: Session? = null,
        page: Int = 1
    ): BookmarkSearchResult {
        val response: HttpResponse =
            httpClient.getWithSession(locations.bookmark_location(bookmarkSearch(bookmarkQuery), page), session)
        return bookmarkParser.parsePage(response.receive())
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
     * @param field: the search to base the autocomplete on
     * @param autoCompleteField: The type of autocomplete to return
     *
     * @return a list of suggested completions for field
     */
    suspend fun suggestAutoComplete(field: String, autoCompleteField: AutoCompleteField): List<AutoCompleteResult> {
        val response: HttpResponse =
            httpClient.get(locations.auto_complete(autoCompleteField.search_param, field.encode()))
        return autoCompleteParser.parsePage(response.receive())
    }

    /**
     * Get the chapters for a given work
     *
     * @param work: the work to retrieve chapters for
     * @param session: a user session that has access to this work
     *
     * @return ChapterQueryResult containing the requested information
     */
    suspend fun getChapters(work: Work, session: Session? = null): ChapterNavigationResult<FullChapterInfo> {
        return getChapters(work.workId, session)
    }

    /**
     * Get chapters by the WorkId
     * @see getChapters
     */
    suspend fun getChapters(workId: Int, session: Session? = null): ChapterNavigationResult<FullChapterInfo> {
        val response: HttpResponse = httpClient.getWithSession(locations.chapter_navigation(workId), session)
        if (response.status != HttpStatusCode.OK)
            throw WorkDoesNotExistException(workId)
        return chapterNavigationParser.parsePage(response.receive())
    }

    suspend fun getChapter(chapterId: Int, session: Session? = null): ChapterResult {
        val response: HttpResponse = httpClient.getWithSession(ao3_chapter(chapterId), session)
        if (response.status != HttpStatusCode.OK)
            throw ChapterDoesNotExistException(chapterId)
        return chapterParser.parsePage(response.receive())
    }

    /**
     * Get the first chapter from a work without needing to know knowing the chapter ID,
     * If the chapterID of the first chapter is known then you should use getChapter(Int, Session) instead
     *
     * @param workId: Work to query
     * @param session: A session which has permission to view this work
     */
    suspend fun getFirstChapter(workId: Int, session: Session? = null): ChapterResult {
        val chapterLocation: Int =
            httpClient.getWithSession(
                locations.first_chapter_location(workId),
                session
            ).headers[HttpHeaders.Location]?.substringAfterLast('/')?.toInt()
                ?: throw WorkDoesNotExistException(workId)
        return getChapter(chapterLocation, session)
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

        val authToken: String = loginParser.parsePage(response.receive())

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
                    else -> logger.warn("unused cookie ${cookie.name}, ${cookie.value}")
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
        val response: HttpResponse = httpClient.getWithSession(locations.login_loc, session)
        return response.status == HttpStatusCode.Found
    }

    private suspend fun HttpClient.getWithSession(location: String, session: Session?): HttpResponse =
        this.get(location) {
            session?.let {
                with(session) {
                    setSessionCookies()
                }
            }
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