package model.result.work

import constants.workproperties.Language
import java.io.Serializable

@kotlinx.serialization.Serializable
data class Work(
    val workId: Int,
    val restricted: Boolean,
    val latestChapter: Int,
    val archiveSymbols: ArchiveSymbols,
    val title: String,
    val creators: List<Creator>,
    val tags: MutableList<Tag>,
    val summary: String,
    val language: Language,
    val stats: Stats<WorkSearchDateStat>
) : Serializable

