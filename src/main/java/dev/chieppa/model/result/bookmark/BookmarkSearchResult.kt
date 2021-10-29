package dev.chieppa.model.result.bookmark

import dev.chieppa.model.result.navigation.Navigation

data class BookmarkSearchResult(
    val bookmarkResults: List<BookmarkResult>,
    val found: Int,
    val navigation: Navigation
)
