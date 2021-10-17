package dev.chieppa.model.result.work

import dev.chieppa.constants.workproperties.Category
import dev.chieppa.constants.workproperties.CompletionStatus
import dev.chieppa.constants.workproperties.ContentRating
import dev.chieppa.constants.workproperties.ContentWarning
import java.io.Serializable

@kotlinx.serialization.Serializable
data class ArchiveSymbols(val contentRating: ContentRating,
                          val category: Category,
                          val contentWarning: ContentWarning,
                          val completionStatus: CompletionStatus) : Serializable