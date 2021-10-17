package dev.chieppa.model.result.bookmark

import dev.chieppa.constants.workproperties.BookmarkType
import dev.chieppa.model.result.work.Work

data class BookmarkResult(
    val work: Work?,
    val bookmarkType: BookmarkType?,
    val bookmarkUserSection: BookmarkUserSection
)
