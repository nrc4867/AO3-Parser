package model.result.chapter

import model.result.work.Creator
import java.io.Serializable

@kotlinx.serialization.Serializable
data class InspiredWork(val name: String, val workId: Int?, val authors: List<Creator>): Serializable