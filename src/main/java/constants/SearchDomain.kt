package constants

enum class SearchDomain(val search_param: String) {
    WORK_SEARCH("work_search"),
    EXCLUDE_WORK_SEARCH("exclude_work_search")
}