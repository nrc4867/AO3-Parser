package dev.chieppa.model.result

import dev.chieppa.model.result.work.Work
import java.io.Serializable

@kotlinx.serialization.Serializable
data class GiftsResult(
    val pages: Int,
    val page: Int,
    val works: List<Work>
) : Serializable