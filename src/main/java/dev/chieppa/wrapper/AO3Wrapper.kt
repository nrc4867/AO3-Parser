package dev.chieppa.wrapper

import dev.chieppa.wrapper.constants.AutoCompleteField
import dev.chieppa.wrapper.constants.ao3_chapter
import dev.chieppa.wrapper.constants.ao3_host
import dev.chieppa.wrapper.constants.ao3_session_cookie
import dev.chieppa.wrapper.constants.workproperties.SortColumn
import dev.chieppa.wrapper.constants.workproperties.SortDirection
import dev.chieppa.wrapper.exception.loginexception.InvalidLoginException
import dev.chieppa.wrapper.exception.queryexception.ChapterDoesNotExistException
import dev.chieppa.wrapper.exception.queryexception.WorkDoesNotExistException
import dev.chieppa.wrapper.model.Session
import dev.chieppa.wrapper.model.result.*
import dev.chieppa.wrapper.model.result.chapter.ChapterNavigationResult
import dev.chieppa.wrapper.model.result.chapter.ChapterResult
import dev.chieppa.wrapper.model.result.chapter.FullChapterInfo
import dev.chieppa.wrapper.model.result.filterSidebar.BookmarkSortAndFilterResult
import dev.chieppa.wrapper.model.result.filterSidebar.TagSortAndFilterResult
import dev.chieppa.wrapper.model.result.work.Work
import dev.chieppa.wrapper.model.searchqueries.*
import dev.chieppa.wrapper.parser.*
import dev.chieppa.wrapper.util.encode
import dev.chieppa.wrapper.util.loginForm
import dev.chieppa.wrapper.util.setCookie
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpCookie
import java.net.URLDecoder

class AO3Wrapper(
    private var requestApplicator: suspend (HttpRequestBuilder) -> HttpResponse,
    private val locations: LinkLocations = LinkLocations()
) {

    constructor(userAgent: String) : this(requestApplicator = createRequestApplicator(userAgent))

    companion object {
        private fun createRequestApplicator(userAgent: String): suspend (HttpRequestBuilder) -> HttpResponse {
            val httpClient = HttpClient { ao3HttpClientConfig(userAgent) }
            return httpClient::request
        }
    }

    /**
     * parser for the search results
     */
    var searchParser = SearchParser<Work>()

    /**
     * parser for the filtered results
     */
    var sortAndFilterParser = SortAndFilterParser<Work>()

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
     * Parser for comments
     */
    var commentsParser = CommentsParser()

    /**
     * Parser for user works
     */
    var userWorksParser = UserQueryParser(sortAndFilterParser)

    /**
     * Parser for user bookmarks page
     */
    var userBookmarkParser = UserQueryParser(UserBookmarkParser(bookmarkParser))

    /**
     * Parser for user gifts
     */
    var userGiftParser = UserQueryParser(GiftsParser())

    /**
     * Parser for user profile
     */
    var userProfileParser = UserQueryParser(ProfileParser())

    /**
     * Parser for Tag summary
     */
    var tagSummaryParser = TagSummaryParser()


    /**
     * Perform a search
     *
     * @param workSearchQuery: a query
     * @param session: a session that has permission to view the works in the listed query
     * @param page: the page of results to return
     */
    suspend fun search(workSearchQuery: WorkSearchQuery, session: Session? = null, page: Int = 1): SearchResult<Work> {
        val response: HttpResponse = getWithSession(locations.search_loc(workSearchQuery.workSearchString(), page), session)
        return searchParser.parsePage(response.body())
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
    ): TagSortAndFilterResult<Work> {
        val response: HttpResponse = getWithSession(
            locations.filter_loc(
                buildTagSearchQuery(
                    tagId,
                    include,
                    exclude,
                    workSearchQuery
                ), page
            ), session
        )
        return sortAndFilterParser.parsePage(response.body())
    }

    suspend fun sortAndFilterUserWorks(
        user: String,
        pseudonym: String? = null,
        include: TagSearchQuery,
        exclude: TagSearchQuery,
        workSearchQuery: FilterWorkSearchQuery,
        session: Session? = null,
        page: Int = 1
    ): UserQueryResult<TagSortAndFilterResult<Work>> {
        val response: HttpResponse = getWithSession(
            locations.filter_loc(
                buildUserSearchQuery(
                    user, pseudonym,
                    include, exclude,
                    workSearchQuery
                ), page
            ), session
        )
        return userWorksParser.parsePage(response.body())
    }

    suspend fun searchPeople(peopleQuery: PeopleQuery, session: Session? = null, page: Int = 1): PeopleResult {
        val response: HttpResponse = getWithSession(locations.people_location(peopleSearch(peopleQuery), page), session)
        return personWrapper.parsePage(response.body())
    }

    suspend fun searchBookmarks(
        bookmarkQuery: BookmarkQuery,
        session: Session? = null,
        page: Int = 1
    ): BookmarkSearchResult {
        val response: HttpResponse = getWithSession(locations.bookmark_location(bookmarkSearch(bookmarkQuery), page), session)
        return bookmarkParser.parsePage(response.body())
    }

    suspend fun sortAndFilterBookmarks(
        user: String,
        pseudonym: String? = null,
        bookmarkQuery: BookmarkQuery,
        session: Session? = null,
        page: Int = 1
    ): UserQueryResult<BookmarkSortAndFilterResult> {
        val response: HttpResponse = getWithSession(
            locations.filter_loc_bookmarks(buildUserBookmarkSearchQuery(user, pseudonym, bookmarkQuery), page),
            session
        )
        return userBookmarkParser.parsePage(response.body())
    }

//    suspend fun sortAndFilterCollection() {
//    }
//
//    suspend fun getUserSeries() {
//    }
//
//    suspend fun getUserCollections() {
//    }

    /**
     * Get the gifts given to this user.
     * @param user: The username of the account. pseudonym is not available for this query
     * @param page: the page of the query
     * @param session: A session that has the permissions to view the works given to this user.
     */
    suspend fun getUserGifts(user: String, page: Int = 1, session: Session? = null): UserQueryResult<GiftsResult> {
        val response: HttpResponse = getWithSession(locations.user_gift_location(user, page), session)
        return userGiftParser.parsePage(response.body())
    }

    suspend fun getUserProfile(user: String, session: Session? = null): UserQueryResult<UserProfileResult> {
        val response: HttpResponse = getWithSession(locations.user_profile_location(user), session)
        return userProfileParser.parsePage(response.body())
    }

//    suspend fun getUserDashboard() {
//    }

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
        workIds: Collection<Int>,
        session: Session? = null,
        sortColumn: SortColumn = SortColumn.DATE_UPDATED,
        sortDirection: SortDirection = SortDirection.DESCENDING,
        betweenPages: suspend (SearchResult<Work>) -> Unit = { },
    ): Set<Work> {
        val works: MutableSet<Work> = mutableSetOf()
        val searchQuery = createIDSearchString(workIds)
        var currentPage = 1
        var endPage: Int
        do {
            val result = search(
                WorkSearchQuery(query = searchQuery, sortColumn = sortColumn, sortDirection = sortDirection),
                session, currentPage
            )
            betweenPages(result)
            works.addAll(result.articles)
            endPage = result.navigation.pages
            currentPage++
        } while (currentPage < endPage)
        return works
    }

    /**
     * Get a suggested autocomplete from an ao3 field
     *
     * @param field: the search to base the autocomplete on
     * @param autoCompleteField: The type of autocomplete to return
     *
     * @return a list of suggested completions for field
     */
    suspend fun suggestAutoComplete(field: String, autoCompleteField: AutoCompleteField): List<AutoCompleteResult> {
        val response: HttpResponse = requestApplicator(HttpRequestBuilder().apply {
                method = HttpMethod.Get
                url(locations.auto_complete(autoCompleteField.search_param, field.encode()))
            })
        return autoCompleteParser.parsePage(response.body())
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
        return getChapters(work.articleID, session)
    }

    /**
     * Get chapters by the WorkId
     * @see getChapters
     */
    suspend fun getChapters(workId: Int, session: Session? = null): ChapterNavigationResult<FullChapterInfo> {
        val response: HttpResponse = getWithSession(locations.chapter_navigation(workId), session)
        if (response.status != HttpStatusCode.OK)
            throw WorkDoesNotExistException(workId)
        return chapterNavigationParser.parsePage(response.body())
    }

    /**
     * Request a chapter from a work
     *
     * @param chapterId the id of the chapter
     * @param session a session which has the permission to view this work
     */
    suspend fun getChapter(chapterId: Int, session: Session? = null): ChapterResult {
        val response: HttpResponse = getWithSession(ao3_chapter(chapterId), session)
        if (response.status != HttpStatusCode.OK)
            throw ChapterDoesNotExistException(chapterId)
        return chapterParser.parsePage(response.body())
    }

    /**
     * Get the first chapter from a work without needing to know knowing the chapter ID,
     * If the chapterID of the first chapter is known then you should use getChapter(Int, Session) instead
     *
     * @param workId: Work to query
     * @param session: A session which has permission to view this work
     */
    suspend fun getFirstChapter(workId: Int, session: Session? = null): ChapterResult {
        val response: HttpResponse = getWithSession(locations.first_chapter_location(workId), session)
        if (response.status == HttpStatusCode.Found) { // multi-chapter work
            val chapterLocation: Int =
                response.headers[HttpHeaders.Location]?.substringAfterLast('/')?.toInt()
                    ?: throw WorkDoesNotExistException(workId)
            return getChapter(chapterLocation, session)
        }
        return chapterParser.parsePage(response.body())
    }

    /**
     * get comments from the entire work
     *
     * @param workId: The id of the work to get the comments from
     * @param page: The page of the comments
     * @param session: A session which has permission to view this work
     */
    suspend fun getCommentsFromWork(workId: Int, page: Int = 1, session: Session? = null): CommentResult =
        getComments(locations.work_comment_location(workId, page), session)

    /**
     * get comments from a single chapter
     *
     * @param chapterId: The id of the chapter to get the comments from
     * @param page: The page of the comments
     * @param session: A session which has permission to view this work
     */
    suspend fun getCommentsFromChapter(chapterId: Int, page: Int = 1, session: Session? = null): CommentResult =
        getComments(locations.chapter_comment_location(chapterId, page), session)

    /**
     * Get Summary Information about a tag
     * @param tag: tag to get summary info about
     */
    suspend fun getTagSummary(tag: String): TagSummaryResult? {
        val response = getWithSession(
            URLBuilder(
                protocol = URLProtocol.HTTPS,
                host = ao3_host,
                pathSegments = listOf("tags", tag)
            ).buildString(),
            session = null
        )

        return when (response.status) {
            HttpStatusCode.OK -> tagSummaryParser.parsePage(response.body())
            else -> null
        }
    }

    /**
     * Get the tag the provided tag was merged with
     * @param tag: tag to query on
     * @return: the merged tag (including provided tag), null if this tag is not canonical
     */
    suspend fun getMergerTag(tag: String): String? {
        val response = requestApplicator(HttpRequestBuilder().apply {
            method = HttpMethod.Head
            URLBuilder(
                protocol = URLProtocol.HTTPS,
                host = ao3_host,
                pathSegments = listOf("tags", tag, "works"),
                parameters = Parameters.build {
                    append("page", "100000000000")
                }
            ).buildString()
        })
        if (response.status == HttpStatusCode.OK) {
            return tag
        }

        val location = response.headers[HttpHeaders.Location] ?: return null

        val merger = ParserRegex.tagMergerRegex.getRegexFound(location) ?: return null

        return withContext(Dispatchers.IO) {
            URLDecoder.decode(merger, Charsets.UTF_8.toString())
        }.replace(ParserRegex.tagURLHelperRegex) {
            return@replace when (it.value) {
                "*s*" -> "/"
                "*a*" -> "&"
                "*d*" -> "."
                "*q*" -> "?"
                "*h*" -> "#"
                else -> it.value
            }
        }
    }

    private suspend fun getComments(location: String, session: Session?): CommentResult {
        return commentsParser.parsePage(getCommentRequest(location, session).body())
    }

    private suspend fun getCommentRequest(location: String, session: Session?): HttpResponse =
        requestApplicator(HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(location)
            setSession(session)
            // asks ao3 to return the jquery use to display the comments rather than the entire article
            header("X-Requested-With", "XMLHttpRequest")
            header(
                "Accept",
                "*/*;q=0.5, text/javascript, application/javascript, application/ecmascript, application/x-ecmascript"
            )
        })

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

        /*
         * I have to get a real login page to acquire a rails token
         */
        val response: HttpResponse = requestApplicator(HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(locations.login_loc)
        })

        val authToken: String = loginParser.parsePage(response.body())

        val cookies: MutableMap<String, HttpCookie> = mutableMapOf()
        response.headers.getAll(HttpHeaders.SetCookie)?.forEach {
            HttpCookie.parse(it)[0].also { cookie ->
                cookies[cookie.name] = cookie
            }
        }

        val loginResponse: HttpResponse = requestApplicator(HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(locations.login_loc)
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(loginForm(username, password, authToken, rememberMe))
            cookies[ao3_session_cookie]?.let { setCookie(ao3_session_cookie, it.value) }
        })

        if (loginResponse.status.value != HttpStatusCode.Found.value) {
            throw InvalidLoginException()
        }

        return extractSession(loginResponse.headers)
    }

    /**
     * Check to see if this session is still signed in
     *
     * @param session user session
     * @return true if this user is being recognized as logged in
     */
    suspend fun validateSession(session: Session): Boolean {
        val response: HttpResponse = getWithSession(locations.login_loc, session)
        return response.status == HttpStatusCode.Found
    }

    private suspend fun getWithSession(location: String, session: Session?): HttpResponse {
        return requestApplicator(HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(location)
            setSession(session)
            setCookie("view_adult", "true")
        })
    }

    private fun HttpRequestBuilder.setSession(session: Session?) =
        session?.let {
            with(session) {
                setSessionCookies()
            }
        }

    private fun createSearchStringForWorks(works: List<Work>): String {
        return createIDSearchString(works.map { work -> work.articleID })
    }

    private fun createIDSearchString(workIds: Collection<Int>): String {
        return "id:(" + workIds.joinToString(" OR ") { "$it" } + ")"
    }
}

fun HttpClientConfig<*>.ao3HttpClientConfig(userAgent: String) {
    expectSuccess = false
    followRedirects = false

    install(UserAgent) {
        agent = userAgent
    }
}