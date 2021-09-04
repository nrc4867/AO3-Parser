package model.result

data class BookmarkSearchResult(
    val bookmarkResults: List<BookmarkResult>,
    val found: Int,
    val page: Int,
    val pages: Int
)
