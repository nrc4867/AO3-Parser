package dev.chieppa.model.result.chapter

import java.io.Serializable

@kotlinx.serialization.Serializable
data class ChapterResult(
    val workMeta: WorkMeta,
    val restricted: Boolean,
    val commentsRestricted: Boolean,
    val chapterNavigationResult: ChapterNavigationResult<BasicChapterInfo>,
    val chapterPosition: Int?, // zero indexed
    val chapterId: Int?,
    val authorNotes: List<AuthorNote>,
    val createdFor: List<String>?,
    val inspiredBy: List<InspiredWork>?,
    val inspiredWorks: List<InspiredWork>?,
    val translations: List<TranslatedWork>?,
    val chapterText: String
): Serializable