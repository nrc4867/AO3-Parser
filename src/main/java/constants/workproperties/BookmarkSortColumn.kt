package constants.workproperties

enum class BookmarkSortColumn(val search_param: String) {
    BEST_MATCH(""),
    DATE_BOOKMARKED("created_at"),
    DATE_UPDATED("bookmarkable_date")
}