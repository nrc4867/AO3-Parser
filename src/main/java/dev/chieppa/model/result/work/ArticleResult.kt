package dev.chieppa.model.result.work

import dev.chieppa.constants.workproperties.Language
import java.io.Serializable

interface ArticleResult {
    val articleID: Int
    val archiveSymbols: ArchiveSymbols
    val title: String
    val creators: List<Creator>
    val tags: List<Tag>
    val summary: String
    val stats: Stats<WorkSearchDateStat>
}

@kotlinx.serialization.Serializable
data class Work(
    override val articleID: Int,
    val restricted: Boolean,
    val latestChapter: Int,
    override val archiveSymbols: ArchiveSymbols,
    override val title: String,
    override val creators: List<Creator>,
    val createdFor: List<String>,
    override val tags: List<Tag>,
    override val summary: String,
    val series: List<WorkAssociatedSeries>,
    val language: Language,
    val collections: Int,
    override val stats: WorkStats<WorkSearchDateStat>
) : ArticleResult, Serializable

@kotlinx.serialization.Serializable
data class ExternalWork(
    override val articleID: Int,
    override val archiveSymbols: ArchiveSymbols,
    override val title: String,
    override val creators: List<Creator>,
    override val tags: List<Tag>,
    override val summary: String,
    override val stats: ExternalWorkStats
) : ArticleResult, Serializable

@kotlinx.serialization.Serializable
data class Series(
    override val articleID: Int,
    override val title: String,
    override val archiveSymbols: ArchiveSymbols,
    override val creators: List<Creator>,
    override val tags: List<Tag>,
    override val summary: String,
    override val stats: SeriesStats
) : ArticleResult, Serializable