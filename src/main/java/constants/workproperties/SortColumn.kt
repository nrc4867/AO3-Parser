package constants.workproperties

enum class SortColumn(val search_param: String) {
    BEST_MATCH("_score"),
    AUTHOR("authors_to_sort_on"),
    TITLE("title_to_sort_on"),
    DATE_POSTED("created_at"),
    DATE_UPDATED("revised_at"),
    WORD_COUNT("word_count"),
    HITS("hits"),
    KUDOS("kudos"),
    COMMENTS("comments_count"),
    BOOKMARKS("bookmarks_count")
}