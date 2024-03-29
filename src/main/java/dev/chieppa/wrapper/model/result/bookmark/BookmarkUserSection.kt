package dev.chieppa.wrapper.model.result.bookmark

import dev.chieppa.wrapper.model.result.work.Creator
import java.io.Serializable
import java.time.temporal.TemporalAccessor

@kotlinx.serialization.Serializable
data class BookmarkUserSection(
    val creator: Creator,
    val notes: String,
    val bookmarkerTags: List<String>,
    val bookmarkDate: TemporalAccessor
): Serializable
