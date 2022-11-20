package dev.chieppa.wrapper.model.result

import dev.chieppa.wrapper.constants.workproperties.TagType
import java.io.Serializable

@kotlinx.serialization.Serializable
data class TagSummaryResult(
    val primaryTag: String,
    val category: TagType?,
    val filterWorksOrBookmarks: Boolean,

    val merger: String?,

    val parentTags: List<String>,
    val synonymTags: List<String>,
    val subTags: List<String>,
    val metaTags: List<String>,

    val childTags: Map<TagType, List<String>>
): Serializable