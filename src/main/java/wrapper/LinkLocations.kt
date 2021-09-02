package wrapper

import constants.*

data class LinkLocations(
    val login_loc: String = ao3_login,
    val search_loc: (String, Int) -> String = ao3_search,
    val filter_loc: (String, Int) -> String = ao3_sort_and_filter,
    val auto_complete: (String, String) -> String = ao3_autocomplete,
    val chapter_navigation: (work_id: Int) -> String = ao3_chapter_navigation,
    val work_location: (work_id: Int, chapter_id: Int) -> String = ao3_chapter
)
