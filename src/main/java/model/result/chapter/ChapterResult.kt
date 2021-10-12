package model.result.chapter

import model.result.work.Creator
import java.io.Serializable

@kotlinx.serialization.Serializable
data class ChapterResult(
    val workMeta: WorkMeta,
    val restricted: Boolean,
    val chapterNavigationResult: ChapterNavigationResult<BasicChapterInfo>,
    val chapterPosition: Int?, // zero indexed
    val chapterId: Int?,
    val authorNotes: List<AuthorNote>,
    val createdFor: List<Creator>?,
    val inspiredBy: List<InspiredWork>?,
    val inspiredWorks: List<InspiredWork>?,
    val translations: List<TranslatedWork>?,
    val chapterText: String
): Serializable