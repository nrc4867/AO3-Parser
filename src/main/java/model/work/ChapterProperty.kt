package model.work

import java.io.Serializable
import java.time.temporal.TemporalAccessor

data class ChapterProperty(
    val chapterNumber: Int,
    val chapterTitle: String,
    val chapterID: Int,
    val postDate: TemporalAccessor
) : Serializable