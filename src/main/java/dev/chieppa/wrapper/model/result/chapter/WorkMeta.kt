package dev.chieppa.wrapper.model.result.chapter

import dev.chieppa.wrapper.constants.workproperties.Language
import dev.chieppa.wrapper.model.result.work.Tag
import dev.chieppa.wrapper.model.result.work.WorkMetaAssociatedSeries
import dev.chieppa.wrapper.model.result.work.WorkMetaDateStat
import dev.chieppa.wrapper.model.result.work.WorkStats
import java.io.Serializable

@kotlinx.serialization.Serializable
data class WorkMeta(
    val tags: List<Tag>,
    val language: Language,
    val collection: List<WorkCollection>,
    val series: List<WorkMetaAssociatedSeries>,
    val workStats: WorkStats<WorkMetaDateStat>
): Serializable
