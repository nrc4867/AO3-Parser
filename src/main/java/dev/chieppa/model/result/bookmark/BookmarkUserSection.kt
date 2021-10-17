package dev.chieppa.model.result.bookmark

import dev.chieppa.model.result.work.Creator
import java.time.temporal.TemporalAccessor

data class BookmarkUserSection(
    val creator: Creator,
    val notes: String,
    val bookmarkerTags: List<String>,
    val bookmarkDate: TemporalAccessor
)
