package model.work

import constants.work_properties.TagType
import java.io.Serializable

@kotlinx.serialization.Serializable
data class Tag(val text: String, val tagType: TagType) : Serializable