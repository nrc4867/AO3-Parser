package model.result

import model.work.ChapterProperty
import model.work.Creator

data class ChapterQueryResult(
    val workID: Int,
    val title: String,
    val creators: List<Creator>,
    val chapters: List<ChapterProperty>
)