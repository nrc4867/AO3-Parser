package dev.chieppa.wrapper

import dev.chieppa.wrapper.constants.*

data class LinkLocations(
    val login_loc: String = ao3_login,
    val search_loc: (query: String, page: Int) -> String = ao3_search,
    val filter_loc: (filter: String, page: Int) -> String = ao3_sort_and_filter,
    val auto_complete: (action: String, term: String) -> String = ao3_autocomplete,
    val chapter_navigation: (work_id: Int) -> String = ao3_chapter_navigation,
    val first_chapter_location: (work_id: Int) -> String = ao3_firstChapter,
    val chapter_location: (work_id: Int) -> String = ao3_chapter,
    val bookmark_location: (query: String, page: Int) -> String = ao3_bookmark_search,
    val people_location: (query: String, page: Int) -> String = ao3_people_search,
    val chapter_comment_location: (chapter_id: Int, page: Int) -> String = ao3_chapter_comment_location,
    val work_comment_location: (work_id: Int, page: Int) -> String = ao3_work_comment_location,
    val user_location: (username: String) -> String = ao3_user_location,
    val filter_loc_bookmarks: (filter: String, page: Int) -> String = ao3_sort_and_filter_bookmarks,
    val user_profile_location: (username: String) -> String = ao3_user_profile_location,
    val user_gift_location: (username: String, page: Int) -> String = ao3_user_gift_location,
    val tags_location: (tag: String) -> String = ao3_tag_location
)
