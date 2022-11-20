package dev.chieppa.wrapper.model.result.chapter

import dev.chieppa.wrapper.constants.SummaryType
import java.io.Serializable

@kotlinx.serialization.Serializable
data class AuthorNote(val summaryType: SummaryType, val summaryText: String): Serializable