package model.result.chapter

import constants.workproperties.Language
import model.result.work.Stats
import model.result.work.Tag
import model.result.work.WorkMetaDateStat
import model.result.work.WorkMetaSeries
import java.io.Serializable

@kotlinx.serialization.Serializable
data class WorkMeta(
    val tags: List<Tag>,
    val language: Language,
    val collection: List<WorkCollection>,
    val series: List<WorkMetaSeries>,
    val stats: Stats<WorkMetaDateStat>
): Serializable
