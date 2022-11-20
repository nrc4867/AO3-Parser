package dev.chieppa.wrapper.model.result.filterSidebar

import kotlinx.serialization.Serializable

@Serializable
data class WarningCount(
    val noArchiveWarningsApply: Int,
    val choseNotToUseWarnings: Int,
    val graphicViolence: Int,
    val majorCharacterDeath: Int,
    val underage: Int,
    val nonCon: Int
)
