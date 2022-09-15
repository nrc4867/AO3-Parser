package dev.chieppa.model.result.filterSidebar

import dev.chieppa.model.result.BookmarkSearchResult
import dev.chieppa.model.result.SearchResult
import dev.chieppa.model.result.work.ArticleResult
import java.io.Serializable

enum class Recommendation(val element_id: String) {
    FANDOM("include_fandom_tags"),
    CHARACTER("include_character_tags"),
    RELATIONSHIP("include_relationship_tags"),
    FREEFORM("include_freeform_tags"),
    BOOKMARKER("include_tag_tags")
}

@kotlinx.serialization.Serializable
class SortAndFilterResult(
    val startWork: Int,
    val endWork: Int,
    val ratingCounts: RatingCount,
    val warningCount: WarningCount,
    val categoryCount: CategoryCount,
    val recommendations: Map<Recommendation, List<RecommendedTag>>
) : Serializable

interface SortAndFilter {
    val sortAndFilter: SortAndFilterResult
}

data class TagSortAndFilterResult<E : ArticleResult>(
    val searchResult: SearchResult<E>,
    override val sortAndFilter: SortAndFilterResult
) : Serializable, SortAndFilter

data class BookmarkSortAndFilterResult(
    val searchResult: BookmarkSearchResult,
    override val sortAndFilter: SortAndFilterResult
) : Serializable, SortAndFilter
