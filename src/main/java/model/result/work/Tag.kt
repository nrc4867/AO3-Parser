package model.result.work

import constants.workproperties.TagType
import java.io.Serializable

@kotlinx.serialization.Serializable
data class Tag(val text: String, val tagType: TagType) : Serializable