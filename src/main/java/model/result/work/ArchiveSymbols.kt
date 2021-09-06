package model.result.work

import constants.workproperties.Category
import constants.workproperties.CompletionStatus
import constants.workproperties.ContentRating
import constants.workproperties.ContentWarning
import java.io.Serializable

@kotlinx.serialization.Serializable
data class ArchiveSymbols(val contentRating: ContentRating,
                          val category: Category,
                          val contentWarning: ContentWarning,
                          val completionStatus: CompletionStatus) : Serializable