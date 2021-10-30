package dev.chieppa.model.result.work

import java.io.Serializable

interface AssociatedSeries {
    val seriesID: Int
    val seriesName: String
    val part: Int
}

@kotlinx.serialization.Serializable
data class WorkMetaAssociatedSeries(
    override val seriesID: Int,
    override val seriesName: String,
    override val part: Int,
    val previousWorkID: Int?,
    val nextWorkID: Int?
) : AssociatedSeries, Serializable


@kotlinx.serialization.Serializable
data class WorkAssociatedSeries(
    override val seriesID: Int,
    override val seriesName: String,
    override val part: Int
) : AssociatedSeries, Serializable