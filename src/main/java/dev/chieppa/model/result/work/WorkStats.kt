package dev.chieppa.model.result.work

import dev.chieppa.util.TemporalAccessorSerializer
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
    @kotlinx.serialization.Serializable(with = TemporalAccessorSerializer::class)
    val dateUpdated: TemporalAccessor
}

@kotlinx.serialization.Serializable
data class WorkSearchDateStat(
    @kotlinx.serialization.Serializable(with = TemporalAccessorSerializer::class)
    override val dateUpdated: TemporalAccessor
) : DateStatistic, Serializable

@kotlinx.serialization.Serializable
data class WorkMetaDateStat(
    @kotlinx.serialization.Serializable(with = TemporalAccessorSerializer::class)
    override val dateUpdated: TemporalAccessor,
    @kotlinx.serialization.Serializable(with = TemporalAccessorSerializer::class)
    val datePublished: TemporalAccessor,
    val complete: Boolean
) : DateStatistic, Serializable