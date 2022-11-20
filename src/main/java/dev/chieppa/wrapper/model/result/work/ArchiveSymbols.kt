package dev.chieppa.wrapper.model.result.work

import dev.chieppa.wrapper.constants.workproperties.Category
import dev.chieppa.wrapper.constants.workproperties.CompletionStatus
import dev.chieppa.wrapper.constants.workproperties.ContentRating
import dev.chieppa.wrapper.constants.workproperties.ContentWarning
import java.io.Serializable

@kotlinx.serialization.Serializable
data class ArchiveSymbols(val contentRating: ContentRating,
                          val category: Category,
                          val contentWarning: ContentWarning,
                          val completionStatus: CompletionStatus) : Serializable