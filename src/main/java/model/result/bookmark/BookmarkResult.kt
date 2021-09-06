package model.result.bookmark

import constants.workproperties.BookmarkType
import model.result.work.Work

data class BookmarkResult(
    val work: Work?,
    val bookmarkType: BookmarkType?,
    val bookmarkUserSection: BookmarkUserSection
)
