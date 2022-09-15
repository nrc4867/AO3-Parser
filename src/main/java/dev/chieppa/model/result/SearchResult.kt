package dev.chieppa.model.result

import dev.chieppa.model.result.navigation.Navigation
import dev.chieppa.model.result.work.ArticleResult
import java.io.Serializable


@kotlinx.serialization.Serializable
data class SearchResult<E : ArticleResult>(
    val found: Int,
    val navigation: Navigation,
    val articles: List<E>
): Serializable