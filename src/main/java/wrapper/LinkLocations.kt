package wrapper

import constants.*

data class LinkLocations(
    val login_loc: String = ao3_login,
    val search_loc: (query: String, page: Int) -> String = ao3_search,
    val filter_loc: (filter: String, page: Int) -> String = ao3_sort_and_filter,
    val auto_complete: (action: String, term: String) -> String = ao3_autocomplete,
    val chapter_navigation: (work_id: Int) -> String = ao3_chapter_navigation,
    val first_chapter_location: (work_id: Int) -> String = ao3_firstChapter,
    val chapter_location: (work_id: Int) -> String = ao3_chapter,
    val bookmark_location: (query: String, page: Int) -> String = ao3_bookmark_search,
    val chapter_comment_location: (chapter_id: Int, page: Int) -> String = ao3_chapter_comment_location,
    val work_comment_location: (work_id: Int, page: Int) -> String = ao3_work_comment_location,
)
