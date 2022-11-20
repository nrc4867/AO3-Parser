package dev.chieppa.wrapper.model.result.bookmark

import dev.chieppa.wrapper.constants.workproperties.BookmarkType
import dev.chieppa.wrapper.model.result.work.ArticleResult
import java.io.Serializable

@kotlinx.serialization.Serializable
data class BookmarkResult(
    val article: ArticleResult?,
    val bookmarkType: BookmarkType?,
    val bookmarkUserSection: BookmarkUserSection
): Serializable
