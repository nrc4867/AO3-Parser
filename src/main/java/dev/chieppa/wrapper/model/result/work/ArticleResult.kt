package dev.chieppa.wrapper.model.result.work

import dev.chieppa.wrapper.constants.workproperties.Language
import dev.chieppa.wrapper.constants.workproperties.TagType
import java.io.Serializable

interface ArticleResult {
    val articleID: Int
    val archiveSymbols: ArchiveSymbols
    val title: String
    val creators: List<Creator>
    val tags: Map<TagType, List<String>>
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
    override val tags: Map<TagType, List<String>>,
    override val summary: String,
    val series: List<WorkAssociatedSeries>,
    val language: Language,
    val collections: Int,
    override val stats: WorkStats<WorkSearchDateStat>
) : ArticleResult, Serializable {
    override fun hashCode(): Int {
        return articleID
    }

    override fun equals(other: Any?): Boolean {
        return (other as Work).articleID == articleID
    }
}

@kotlinx.serialization.Serializable
data class ExternalWork(
    override val articleID: Int,
    override val archiveSymbols: ArchiveSymbols,
    override val title: String,
    override val creators: List<Creator>,
    override val tags: Map<TagType, List<String>>,
    override val summary: String,
    override val stats: ExternalWorkStats
) : ArticleResult, Serializable

@kotlinx.serialization.Serializable
data class Series(
    override val articleID: Int,
    override val title: String,
    override val archiveSymbols: ArchiveSymbols,
    override val creators: List<Creator>,
    override val tags: Map<TagType, List<String>>,
    override val summary: String,
    override val stats: SeriesStats
) : ArticleResult, Serializable