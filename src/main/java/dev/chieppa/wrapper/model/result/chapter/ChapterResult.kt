package dev.chieppa.wrapper.model.result.chapter

import dev.chieppa.wrapper.constants.SummaryType
import dev.chieppa.wrapper.constants.workproperties.*
import dev.chieppa.wrapper.model.result.work.*
import java.io.Serializable

@kotlinx.serialization.Serializable
data class ChapterResult(
    val workMeta: WorkMeta,
    val restricted: Boolean,
    val commentsRestricted: Boolean,
    val chapterNavigationResult: ChapterNavigationResult<BasicChapterInfo>,
    val chapterPosition: Int?, // zero indexed
    val chapterId: Int?,
    val authorNotes: List<AuthorNote>,
    val createdFor: List<String>?,
    val inspiredBy: List<InspiredWork>?,
    val inspiredWorks: List<InspiredWork>?,
    val translations: List<TranslatedWork>?,
    val chapterText: String
): Serializable, ArticleResult {

    private lateinit var tagMap: Map<TagType, List<String>>

    override val articleID: Int
        get() = chapterNavigationResult.workID

    /**
     * Todo: See if there is a way to properly retrieve this from just the chapter information
     */
    override val archiveSymbols: ArchiveSymbols
        get() {
            return ArchiveSymbols(
                contentRating = ContentRating.NONE,
                category = Category.NONE,
                contentWarning = ContentWarning.NO_ARCHIVE_WARNINGS,
                completionStatus = if (workMeta.workStats.chapterTotal == workMeta.workStats.chapterCount)
                    CompletionStatus.COMPLETE else CompletionStatus.IN_PROGRESS
            )
        }
    override val title: String
        get() = chapterNavigationResult.workTitle
    override val creators: List<Creator>
        get() = chapterNavigationResult.creators
    override val tags: Map<TagType, List<String>>
        get() {
            if (this::tagMap.isInitialized) {
                return tagMap
            }

            val tmap = mutableMapOf<TagType, MutableList<String>>()
            TagType.values().forEach {
                tmap[it] = mutableListOf()
            }

            workMeta.tags.forEach {
                tmap[it.tagType]!!.add(it.text)
            }
            tagMap = tmap

            return tmap
        }
    override val summary: String
        get() = authorNotes.find { it.summaryType == SummaryType.SUMMARY }?.summaryText
            ?: authorNotes.find { it.summaryType == SummaryType.CHAPTER_NOTE }?.summaryText
            ?: ""
    override val stats: Stats<WorkSearchDateStat>
        get() = WorkStats(
            chapterCount = workMeta.workStats.chapterCount,
            chapterTotal = workMeta.workStats.chapterTotal,
            wordCount = workMeta.workStats.wordCount,
            dates = WorkSearchDateStat(workMeta.workStats.dates.dateUpdated),
            comments = workMeta.workStats.comments,
            kudos = workMeta.workStats.kudos,
            bookmarks = workMeta.workStats.bookmarks,
            hits = workMeta.workStats.hits
        )

}