package model.work

import constants.work_properties.*
import java.io.Serializable
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

data class Work(val workId: Int,
//                val firstChapter: Int,
                val lastestChapter: Int,

                val archiveSymbols: ArchiveSymbols,
                val title: String,
                val creators: List<Creator>,
//                val fandom: List<String>,
                val tags: MutableList<Tag>,
                val summary: String,

                val chapterCount: Int,
                val chapterTotal: Int?,
                var word_count: Int,
//                val updatedAt: Int,
                val dateUpdated: TemporalAccessor,
                val language: Language,
                val comments: Int,
                val kudos: Int,
                val bookmarks: Int,
                val hits: Int) : Serializable {

    companion object {
        fun emptyWork() : Work {
            return Work(workId = 0,
//                firstChapter = 0,
                lastestChapter = 0,
                archiveSymbols = ArchiveSymbols(ContentRating.NONE,
                    Category.OTHER,
                    ContentWarning.NO_ARCHIVE_WARNINGS,
                    CompletionStatus.UNKNOWN),
                title = "",
                creators = emptyList(),
//                fandom = emptyList(),
                tags = ArrayList(),
                summary = "",
                chapterCount = 0,
                chapterTotal = null,
                word_count = 0,
//                updatedAt = 0,
                dateUpdated = DateTimeFormatter.ofPattern("dd MMM YYYY").parse("01 Sep 2020"),
                language = Language.EN,
                comments = 0,
                kudos = 0,
                bookmarks = 0,
                hits = 0
            )
        }
    }

    fun removeFreeforms() {
        tags.removeAll { tag: Tag -> tag.tagType == TagType.FREEFORM }
    }
}