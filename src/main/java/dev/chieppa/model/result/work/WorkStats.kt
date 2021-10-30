package dev.chieppa.model.result.work

import java.io.Serializable
import java.time.temporal.TemporalAccessor

interface Stats<E : DateStatistic> {
    val bookmarks: Int
    val dates: E
}

@kotlinx.serialization.Serializable
data class WorkStats<E : DateStatistic>(
    val chapterCount: Int,
    val chapterTotal: Int?,
    val wordCount: Int,
    override val dates: E,
    val comments: Int,
    val kudos: Int,
    override val bookmarks: Int,
    val hits: Int
) : Stats<E>, Serializable

@kotlinx.serialization.Serializable
data class ExternalWorkStats(
    override val bookmarks: Int,
    override val dates: WorkSearchDateStat
) : Stats<WorkSearchDateStat>, Serializable

@kotlinx.serialization.Serializable
data class SeriesStats(
    val wordCount: Int,
    override val dates: WorkSearchDateStat,
    override val bookmarks: Int,
    val works: Int
): Stats<WorkSearchDateStat>, Serializable

interface DateStatistic {
    val dateUpdated: TemporalAccessor
}

@kotlinx.serialization.Serializable
data class WorkSearchDateStat(override val dateUpdated: TemporalAccessor) : DateStatistic, Serializable

@kotlinx.serialization.Serializable
data class WorkMetaDateStat(
    override val dateUpdated: TemporalAccessor,
    val datePublished: TemporalAccessor,
    val complete: Boolean
) : DateStatistic, Serializable