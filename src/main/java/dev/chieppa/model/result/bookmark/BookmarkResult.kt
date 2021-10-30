package dev.chieppa.model.result.bookmark

import dev.chieppa.constants.workproperties.BookmarkType
import dev.chieppa.model.result.work.ArticleResult

data class BookmarkResult(
    val article: ArticleResult?,
    val bookmarkType: BookmarkType?,
    val bookmarkUserSection: BookmarkUserSection
)
