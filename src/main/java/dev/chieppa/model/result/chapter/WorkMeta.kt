package dev.chieppa.model.result.chapter

import dev.chieppa.constants.workproperties.Language
import dev.chieppa.model.result.work.Tag
import dev.chieppa.model.result.work.WorkMetaAssociatedSeries
import dev.chieppa.model.result.work.WorkMetaDateStat
import dev.chieppa.model.result.work.WorkStats
import java.io.Serializable

@kotlinx.serialization.Serializable
data class WorkMeta(
    val tags: List<Tag>,
    val language: Language,
    val collection: List<WorkCollection>,
    val series: List<WorkMetaAssociatedSeries>,
    val workStats: WorkStats<WorkMetaDateStat>
): Serializable
