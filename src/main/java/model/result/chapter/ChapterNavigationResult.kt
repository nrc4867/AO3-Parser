package model.result.chapter

import model.result.work.Creator
import java.io.Serializable

@kotlinx.serialization.Serializable
data class ChapterNavigationResult<E: ChapterInfo>(
    val workID: Int,
    val workTitle: String,
    val creators: List<Creator>,
    val chapters: List<E>
): Serializable