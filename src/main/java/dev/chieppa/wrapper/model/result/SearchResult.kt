package dev.chieppa.wrapper.model.result

import dev.chieppa.wrapper.model.result.navigation.Navigation
import dev.chieppa.wrapper.model.result.work.ArticleResult
import java.io.Serializable


@kotlinx.serialization.Serializable
data class SearchResult<E : ArticleResult>(
    val found: Int,
    val navigation: Navigation,
    val articles: List<E>
): Serializable