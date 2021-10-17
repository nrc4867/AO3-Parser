package dev.chieppa.model.result.work

import dev.chieppa.constants.workproperties.TagType
import java.io.Serializable

@kotlinx.serialization.Serializable
data class Tag(val text: String, val tagType: TagType) : Serializable