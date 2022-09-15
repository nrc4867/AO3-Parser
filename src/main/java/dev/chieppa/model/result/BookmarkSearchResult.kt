package dev.chieppa.model.result

import dev.chieppa.model.result.bookmark.BookmarkResult
import dev.chieppa.model.result.navigation.Navigation
import java.io.Serializable

@kotlinx.serialization.Serializable
data class BookmarkSearchResult(
    val bookmarkResults: List<BookmarkResult>,
    val found: Int,
    val navigation: Navigation
): Serializable
