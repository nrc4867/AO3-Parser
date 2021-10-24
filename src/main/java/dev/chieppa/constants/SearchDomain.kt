package dev.chieppa.constants

enum class SearchDomain(val search_param: String) {
    WORK_SEARCH("work_search"),
    EXCLUDE_WORK_SEARCH("exclude_work_search"),
    INCLUDE_WORK_SEARCH("include_work_search"),
    BOOKMARK_SEARCH("bookmark_search"),
    PEOPLE_SEARCH("people_search")
}