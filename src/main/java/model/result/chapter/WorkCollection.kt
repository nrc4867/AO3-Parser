package model.result.chapter

import java.io.Serializable

@kotlinx.serialization.Serializable
data class WorkCollection(val name: String, val pseudoName: String): Serializable