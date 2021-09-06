package model.result.filterSidebar

import model.result.SearchResult

data class TagSortAndFilterResult(
    val searchResult: SearchResult,
    val startWork: Int,
    val endWork: Int,
    val ratingCounts: RatingCount,
    val warningCount: WarningCount,
    val categoryCount: CategoryCount,
    val fandomRecommendation: List<RecommendedTag>,
    val characterRecommendation: List<RecommendedTag>,
    val relationshipRecommendation: List<RecommendedTag>,
    val additionalTagRecommendations: List<RecommendedTag>
)
