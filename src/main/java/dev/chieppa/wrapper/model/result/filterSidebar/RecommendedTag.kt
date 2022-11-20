package dev.chieppa.wrapper.model.result.filterSidebar

import kotlinx.serialization.Serializable

@Serializable
data class RecommendedTag(val name: String, val count: Int, val id: Int)
