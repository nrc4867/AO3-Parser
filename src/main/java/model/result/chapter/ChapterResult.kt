package model.result.chapter

import model.result.work.Work
import java.io.Serializable

@kotlinx.serialization.Serializable
data class ChapterResult(
    val work: Work,
    val workCollection: List<WorkCollection>,
    val chapterNavigationResult: ChapterNavigationResult<BasicChapterInfo>,
    val chapterPosition: Int,
    val chapterId: Int,
    val authorNotes: List<AuthorNote>,
    val chapterText: String
): Serializable