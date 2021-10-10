package model.result.work

import java.io.Serializable
import java.time.temporal.TemporalAccessor

@kotlinx.serialization.Serializable
data class Stats<E : DateStatistic>(
    val chapterCount: Int,
    val chapterTotal: Int?,
    val wordCount: Int,
    val dates: E,
    val comments: Int,
    val kudos: Int,
    val bookmarks: Int,
    val hits: Int
) : Serializable


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