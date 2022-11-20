package dev.chieppa.wrapper.parser

import dev.chieppa.wrapper.constants.workproperties.*
import dev.chieppa.wrapper.constants.workproperties.TagType.CATEGORY
import dev.chieppa.wrapper.constants.workproperties.TagType.Companion.tagTypeMap
import dev.chieppa.wrapper.constants.workproperties.TagType.UNKNOWN
import dev.chieppa.wrapper.exception.parserexception.ExpectedAttributeException
import dev.chieppa.wrapper.exception.parserexception.ExpectedElementException
import dev.chieppa.wrapper.exception.parserexception.SearchParserException
import dev.chieppa.wrapper.model.result.work.*
import dev.chieppa.wrapper.parser.HeaderValues.ArticleType.*
import dev.chieppa.wrapper.parser.ParserRegex.articleTypeRegex
import dev.chieppa.wrapper.parser.ParserRegex.chapterTotalRegex
import dev.chieppa.wrapper.parser.ParserRegex.digitsRegex
import dev.chieppa.wrapper.parser.ParserRegex.tagTypeRegex
import dev.chieppa.wrapper.util.commaSeparatedToInt
import mu.KotlinLogging
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

private data class WorkStatsExtractionResult(
    var language: Language? = null,
    var words: Int = 0,
    var latestChapter: Int = 0,
    var chapterTotal: Int? = 0,
    var chapterCurrent: Int = 0,
    var collections: Int = 0,
    var comments: Int = 0,
    var kudos: Int = 0,
    var bookmarks: Int = 0,
    var hits: Int = 0
)

private data class SeriesStatsExtractResult(
    var words: Int = 0,
    var works: Int = 0,
    var bookmarks: Int = 0,
    var relatedWorks: Int = 0
)


private data class HeaderValues(
    val articleType: ArticleType,
    val articleID: Int,
    val title: String,
    val creator: List<Creator>
) {
    enum class ArticleType {
        WORK, SERIES, EXTERNAL
    }
}

private val logger = KotlinLogging.logger {}

internal fun extractArticle(article: Element): ArticleResult {
    val headerValues =
        extractHeaderValues(article.getFirstByTag("h4").getElementsByTag("a"))
    return when (headerValues.articleType) {
        WORK -> extractWork(headerValues, article)
        SERIES -> extractSeries(headerValues, article)
        EXTERNAL -> extractExternalWork(headerValues, article)
    }
}

internal fun extractExternalWork(article: Element): Series {
    val headerValues =
        extractHeaderValues(article.getFirstByTag("h4").getElementsByTag("a"))
    return extractSeries(headerValues, article)
}

private fun extractExternalWork(headerValues: HeaderValues, article: Element): ExternalWork {
    val stats = extractSeriesStats(article.getFirstByClass("stats").getElementsByTag("dt"))

    return ExternalWork(
        articleID = headerValues.articleID,
        archiveSymbols = extractRequiredTags(
            article.getFirstByClass("required-tags").getElementsByTag("span")
        ),
        title = headerValues.title,
        creators = headerValues.creator,
        tags = extractTagValues(article.getElementsByClass("tag")),
        summary = article.getElementsByTag("blockquote").firstOrNull()?.outerHtml() ?: "",
        stats = ExternalWorkStats(
            dates = WorkSearchDateStat(DateTimeFormats.ddMMMYYYY.parse(article.getFirstByClass("dateTime").text())),
            bookmarks = stats.bookmarks,
        )
    )
}

internal fun extractSeries(article: Element): Series {
    val headerValues =
        extractHeaderValues(article.getFirstByTag("h4").getElementsByTag("a"))
    return extractSeries(headerValues, article)
}

private fun extractSeries(headerValues: HeaderValues, article: Element): Series {
    val stats = extractSeriesStats(article.getFirstByClass("stats").getElementsByTag("dt"))

    return Series(
        articleID = headerValues.articleID,
        archiveSymbols = extractRequiredTags(
            article.getFirstByClass("required-tags").getElementsByTag("span")
        ),
        title = headerValues.title,
        creators = headerValues.creator,
        tags = extractTagValues(article.getElementsByClass("tag")),
        summary = article.getElementsByTag("blockquote").firstOrNull()?.outerHtml() ?: "",
        stats = SeriesStats(
            wordCount = stats.words,
            dates = WorkSearchDateStat(DateTimeFormats.ddMMMYYYY.parse(article.getFirstByClass("dateTime").text())),
            bookmarks = stats.bookmarks,
            works = stats.works
        )
    )
}

internal fun extractWork(article: Element): Work {
    val headerValues =
        extractHeaderValues(article.getFirstByTag("h4").getElementsByTag("a"))
    return extractWork(headerValues, article)
}

private fun extractWork(headerValues: HeaderValues, article: Element): Work {
    val stats = extractWorkStats(article.getFirstByClass("stats").getElementsByTag("dd"))

    val tags = extractTagValues(article.getElementsByClass("tag")).toMutableMap()
    if (!tags.containsKey(CATEGORY) || tags[CATEGORY]?.isEmpty() == true) {
        tags += extractWorkCategories(
            article.getFirstByClass("required-tags").getElementsByTag("span")[4].attr("title")
        )
    }

    return Work(
        articleID = headerValues.articleID,
        restricted = article.getFirstByTag("h4").getElementsByTag("img").isNotEmpty(),
        latestChapter = stats.latestChapter,
        archiveSymbols = extractRequiredTags(
            article.getFirstByClass("required-tags").getElementsByTag("span")
        ),
        title = headerValues.title,
        creators = headerValues.creator.filter { it.authorPseudoName != "" },
        createdFor = headerValues.creator.filter { it.authorPseudoName == "" }.map { it.authorUserName },
        tags = tags,
        summary = article.getElementsByTag("blockquote").firstOrNull()?.outerHtml() ?: "",
        series = article.getElementsByClass("series").firstOrNull()?.let { extractWorkSeries(it) } ?: emptyList(),
        language = stats.language ?: Language.UNKNOWN,
        collections = stats.collections,
        stats = WorkStats(
            chapterCount = stats.chapterCurrent,
            chapterTotal = stats.chapterTotal,
            wordCount = stats.words,
            dates = WorkSearchDateStat(DateTimeFormats.ddMMMYYYY.parse(article.getFirstByClass("dateTime").text())),
            comments = stats.comments,
            kudos = stats.kudos,
            bookmarks = stats.bookmarks,
            hits = stats.hits
        )
    )

}

private fun extractHeaderValues(links: Elements): HeaderValues {
    val storyLink = links.getOrNull(0) ?: throw SearchParserException("Title", "Work does not have title")
    val title = storyLink.text()

    val authors = ArrayList<Creator>()
    for (index in 1 until links.size) {
        authors.add(
            Creator(
                authorUserName = ParserRegex.authorUserRegex.getWithEmptyDefault(links[index].href()),
                authorPseudoName = ParserRegex.authorPseudoRegex.getWithEmptyDefault(links[index].href())
            )
        )
    }

    val articleType = when (val articleRef = articleTypeRegex.getWithEmptyDefault(storyLink.href())) {
        "series" -> SERIES
        "external_works" -> EXTERNAL
        "works" -> WORK
        else -> throw SearchParserException(articleRef, "Unknown articleType")
    }

    return HeaderValues(articleType, digitsRegex.getWithZeroDefault(storyLink.href()), title, authors)
}

private fun extractTagValues(links: Elements): Map<TagType, List<String>> {
    val tagMap = HashMap<TagType, MutableList<String>>()
    for (value in TagType.values()) {
        tagMap[value] = mutableListOf()
    }

    links.forEach {
        val tagTypeStr = tagTypeRegex.getRegexFound(getTagType(it.parent() ?: throw ExpectedElementException("parent", "could not get parent element")))
        val tagType = tagTypeMap.getOrDefault(tagTypeStr, UNKNOWN)
        tagMap[tagType]!!.add(it.text())
    }

    return tagMap
}


private fun getTagType(tag: Element): String {
    return when (tag.tagName()) {
        "strong" -> tag.parent()?.attr("class") ?: throw ExpectedAttributeException("class")
        else -> tag.attr("class")
    }
}

private fun extractRequiredTags(links: Elements): ArchiveSymbols {
    return ArchiveSymbols(
        contentRating = ContentRating.contentRatingMap[getFirstClass(links[0].className())]
            ?: throw SearchParserException("required_tag content_rating", getFirstClass(links[0].className())),
        category = Category.categoryMap[getFirstClass(links[4].className())]
            ?: throw SearchParserException("required_tag category", getFirstClass(links[4].className())),
        contentWarning = ContentWarning.contentWarningMap[getFirstClass(links[2].className())]
            ?: throw SearchParserException("required_tag content_warning", getFirstClass(links[2].className())),
        completionStatus = CompletionStatus.completionStatusMap[getFirstClass(links[6].className())]
            ?: throw SearchParserException("required_tag completion_status", getFirstClass(links[6].className()))
    )
}

private fun extractWorkCategories(title: String): Map<TagType, List<String>> {
    val categories = title.split(", ")
    return mapOf(Pair(CATEGORY, categories))
}

private fun extractWorkSeries(seriesList: Element): MutableList<WorkAssociatedSeries> {
    val series = mutableListOf<WorkAssociatedSeries>()

    for (workSeries in seriesList.getElementsByTag("li")) {
        val link = workSeries.getFirstByTag("a")
        series.add(
            WorkAssociatedSeries(
                digitsRegex.getWithZeroDefault(link.href()),
                link.text(),
                digitsRegex.getWithZeroDefault(workSeries.getFirstByTag("strong").text())
            )
        )
    }

    return series
}

private fun getFirstClass(className: String): String {
    return className.split(" ", limit = 2)[0]
}

private fun extractWorkStats(descriptions: Elements): WorkStatsExtractionResult {

    val workStats = WorkStatsExtractionResult()

    for (descriptor in descriptions) {
        when (val statName = descriptor.className().toString()) {
            "language" -> workStats.language = Language.languageMap.getOrDefault(descriptor.text(), Language.UNKNOWN)
            "words" -> workStats.words = descriptor.text().commaSeparatedToInt()
            "chapters" -> {
                workStats.latestChapter = extractLatestChapterChapter(descriptor.getElementsByTag("a"))
                workStats.chapterTotal = chapterTotalRegex.getRegexFound(descriptor.text().removeCommas())
                    ?.let { if (it == "?") return@let null else it.commaSeparatedToInt() }
                workStats.chapterCurrent =
                    ParserRegex.chapterCurrentRegex.getWithZeroDefault(descriptor.text().removeCommas())
            }
            "collections" -> workStats.collections = descriptor.text().commaSeparatedToInt()
            "comments" -> workStats.comments = descriptor.text().commaSeparatedToInt()
            "kudos" -> workStats.kudos = descriptor.text().commaSeparatedToInt()
            "bookmarks" -> workStats.bookmarks = descriptor.text().commaSeparatedToInt()
            "hits" -> workStats.hits = descriptor.text().commaSeparatedToInt()
            else -> logger.warn("$statName unrecognized, ${descriptor.flattenedHtml()}")
        }
    }

    return workStats
}

private fun extractSeriesStats(stats: Elements): SeriesStatsExtractResult {
    val seriesStats = SeriesStatsExtractResult()
    for (dt in stats) {
        val count = dt.nextElementSibling()?.text()?.commaSeparatedToInt() ?: 0
        when (val statName = dt.text()) {
            "Words:" -> seriesStats.words = count
            "Works:" -> seriesStats.works = count
            "Bookmarks:" -> seriesStats.bookmarks = count
            else -> logger.warn("$statName unrecognized, ${dt.flattenedHtml()}: $count")
        }
    }

    return seriesStats
}

private fun String.removeCommas() = this.replace(",", "")

private fun extractLatestChapterChapter(links: Elements): Int {
    if (links.isEmpty())
        return 0
    return ParserRegex.chapterRegex.getWithZeroDefault(links[0].href())
}