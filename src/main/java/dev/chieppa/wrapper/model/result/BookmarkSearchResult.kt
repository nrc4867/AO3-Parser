package dev.chieppa.wrapper.model.result

import dev.chieppa.wrapper.model.result.bookmark.BookmarkResult
import dev.chieppa.wrapper.model.result.navigation.Navigation
import java.io.Serializable

@kotlinx.serialization.Serializable
data class BookmarkSearchResult(
    val bookmarkResults: List<BookmarkResult>,
    val found: Int,
    val navigation: Navigation
): Serializable
