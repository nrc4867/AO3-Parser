package model.result.chapter

import java.io.Serializable

@kotlinx.serialization.Serializable
data class ChapterResult(
    val workMeta: WorkMeta,
//    val restricted: Boolean,
    val chapterNavigationResult: ChapterNavigationResult<BasicChapterInfo>?,
    val chapterPosition: Int?, // zero indexed
    val chapterId: Int?,
    val authorNotes: List<AuthorNote>,
    val inspiredWorks: List<InspiredWork>?,
    val chapterText: String
): Serializable