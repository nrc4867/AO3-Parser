package model.result.bookmark

import model.result.work.Creator
import java.time.temporal.TemporalAccessor

data class BookmarkUserSection(
    val creator: Creator,
    val notes: String,
    val bookmarkerTags: List<String>,
    val bookmarkDate: TemporalAccessor
)
