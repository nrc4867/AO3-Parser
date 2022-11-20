package dev.chieppa.wrapper.model.result.filterSidebar

import kotlinx.serialization.Serializable

@Serializable
data class RatingCount(
    val teenAndUp: Int,
    val generalAudiences: Int,
    val notRated: Int,
    val mature: Int,
    val explicit: Int
)
