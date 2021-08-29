package model.work

import constants.work_properties.Language
import java.io.Serializable
import java.time.temporal.TemporalAccessor

data class Work(
    val workId: Int,
    val latestChapter: Int,
    val archiveSymbols: ArchiveSymbols,
    val title: String,
    val creators: List<Creator>,
    val tags: MutableList<Tag>,
    val summary: String,
    val chapterCount: Int,
    val chapterTotal: Int?,
    var word_count: Int,
    val dateUpdated: TemporalAccessor,
    val language: Language,
    val comments: Int,
    val kudos: Int,
    val bookmarks: Int,
    val hits: Int
) : Serializable