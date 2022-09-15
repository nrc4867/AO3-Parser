package dev.chieppa.model.result.bookmark

import dev.chieppa.constants.workproperties.BookmarkType
import dev.chieppa.model.result.work.ArticleResult
import java.io.Serializable

@kotlinx.serialization.Serializable
data class BookmarkResult(
    val article: ArticleResult?,
    val bookmarkType: BookmarkType?,
    val bookmarkUserSection: BookmarkUserSection
): Serializable
