package dev.chieppa.model.result.work

import java.io.Serializable

interface Series {
    val seriesID: Int
    val seriesName: String
    val part: Int
}

@kotlinx.serialization.Serializable
data class WorkMetaSeries(
    override val seriesID: Int,
    override val seriesName: String,
    override val part: Int,
    val previousWorkID: Int?,
    val nextWorkID: Int?
) : Series, Serializable


@kotlinx.serialization.Serializable
data class WorkSeries(
    override val seriesID: Int,
    override val seriesName: String,
    override val part: Int
) : Series, Serializable