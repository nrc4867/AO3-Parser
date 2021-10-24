package dev.chieppa.model.result.people

import java.io.Serializable

@kotlinx.serialization.Serializable
data class Person(
    val username: String,
    val pseudo: String,
    val profileImage: String?,
    val works: Int?,
    val fandomWorks: List<FandomWork>?,
    val bookmarks: Int?,
    val description: String?
): Serializable

@kotlinx.serialization.Serializable
data class FandomWork(val total: Int, val fandom: String, val fandomID: Int): Serializable