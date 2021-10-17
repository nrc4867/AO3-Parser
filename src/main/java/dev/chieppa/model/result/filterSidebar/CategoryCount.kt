package dev.chieppa.model.result.filterSidebar

import kotlinx.serialization.Serializable

@Serializable
data class CategoryCount(
    val femaleMale: Int,
    val gen: Int,
    val femaleFemale: Int,
    val maleMale: Int,
    val multi: Int,
    val other: Int
)