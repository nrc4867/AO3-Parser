package model.work

import constants.work_properties.Category
import constants.work_properties.CompletionStatus
import constants.work_properties.ContentRating
import constants.work_properties.ContentWarning
import java.io.Serializable

@kotlinx.serialization.Serializable
data class ArchiveSymbols(val contentRating: ContentRating,
                          val category: Category,
                          val contentWarning: ContentWarning,
                          val completionStatus: CompletionStatus) : Serializable