package dev.chieppa.wrapper.model.result

import dev.chieppa.wrapper.model.result.navigation.Navigation
import dev.chieppa.wrapper.model.result.work.Work
import java.io.Serializable

@kotlinx.serialization.Serializable
data class GiftsResult(
    val navigation: Navigation,
    val works: List<Work>
) : Serializable