package dev.chieppa.wrapper.model.result.chapter

import dev.chieppa.wrapper.constants.workproperties.Language
import dev.chieppa.wrapper.model.result.work.Creator
import java.io.Serializable

@kotlinx.serialization.Serializable
data class InspiredWork(val name: String, val workId: Int?, val authors: List<Creator>): Serializable

@kotlinx.serialization.Serializable
data class TranslatedWork(val language: Language, val inspiredWork: InspiredWork): Serializable