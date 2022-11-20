package dev.chieppa.wrapper.model.result.chapter

import java.io.Serializable
import java.time.temporal.TemporalAccessor


interface ChapterInfo {
    val chapterTitle: String
    val chapterNumber: Int
    val chapterID: Int
}

@kotlinx.serialization.Serializable
data class BasicChapterInfo(
    override val chapterNumber: Int,
    override val chapterTitle: String,
    override val chapterID: Int,
): ChapterInfo, Serializable

@kotlinx.serialization.Serializable
data class FullChapterInfo(
    override val chapterNumber: Int,
    override val chapterTitle: String,
    override val chapterID: Int,
    val postDate: TemporalAccessor
) : ChapterInfo, Serializable