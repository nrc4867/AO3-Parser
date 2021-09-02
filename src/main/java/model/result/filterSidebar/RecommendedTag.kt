package model.result.filterSidebar

import kotlinx.serialization.Serializable

@Serializable
data class RecommendedTag(val name: String, val count: Int, val id: Int)
