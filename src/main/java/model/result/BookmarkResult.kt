package model.result

import constants.work_properties.BookmarkType
import model.work.Work

data class BookmarkResult(
    val work: Work?,
    val bookmarkType: BookmarkType?,
    val bookmarkUserSection: BookmarkUserSection
)
