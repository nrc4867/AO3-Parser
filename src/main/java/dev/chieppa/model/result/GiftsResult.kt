package dev.chieppa.model.result

import dev.chieppa.model.result.navigation.Navigation
import dev.chieppa.model.result.work.Work
import java.io.Serializable

@kotlinx.serialization.Serializable
data class GiftsResult(
    val navigation: Navigation,
    val works: List<Work>
) : Serializable