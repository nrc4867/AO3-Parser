package dev.chieppa.model.result.chapter

import dev.chieppa.constants.workproperties.Language
import dev.chieppa.model.result.work.Creator
import java.io.Serializable

@kotlinx.serialization.Serializable
data class InspiredWork(val name: String, val workId: Int?, val authors: List<Creator>): Serializable

@kotlinx.serialization.Serializable
data class TranslatedWork(val language: Language, val inspiredWork: InspiredWork): Serializable