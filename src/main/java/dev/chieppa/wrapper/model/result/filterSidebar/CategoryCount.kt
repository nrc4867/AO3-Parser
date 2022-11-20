package dev.chieppa.wrapper.model.result.filterSidebar

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