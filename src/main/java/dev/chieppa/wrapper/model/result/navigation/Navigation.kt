package dev.chieppa.wrapper.model.result.navigation

import java.io.Serializable

@kotlinx.serialization.Serializable
data class Navigation(val page: Int, val pages: Int): Serializable
