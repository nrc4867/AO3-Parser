package model.result.chapter

import constants.SummaryType
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class AuthorNote(val summaryType: SummaryType, @Contextual val summaryText: String)