package dev.chieppa.model.result.work

import dev.chieppa.constants.workproperties.Language
import java.io.Serializable

@kotlinx.serialization.Serializable
data class Work(
    val workId: Int,
    val restricted: Boolean,
    val latestChapter: Int,
    val archiveSymbols: ArchiveSymbols,
    val title: String,
    val creators: List<Creator>,
    val createdFor: List<String>,
    val tags: List<Tag>,
    val summary: String,
    val series: List<WorkSeries>,
    val language: Language,
    val stats: Stats<WorkSearchDateStat>
) : Serializable

