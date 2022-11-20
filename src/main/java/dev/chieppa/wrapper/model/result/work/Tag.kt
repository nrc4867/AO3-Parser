package dev.chieppa.wrapper.model.result.work

import dev.chieppa.wrapper.constants.workproperties.TagType
import java.io.Serializable

@kotlinx.serialization.Serializable
data class Tag(val text: String, val tagType: TagType) : Serializable