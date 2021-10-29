package dev.chieppa.wrapper.parser

import dev.chieppa.constants.workproperties.*
import dev.chieppa.constants.workproperties.TagType.Companion.tagTypeMap
import dev.chieppa.constants.workproperties.TagType.UNKNOWN
import dev.chieppa.exception.parserexception.ExpectedAttributeException
import dev.chieppa.exception.parserexception.ExpectedElementException
import dev.chieppa.exception.parserexception.SearchParserException
import dev.chieppa.model.result.work.*
import dev.chieppa.util.commaSeparatedToInt
import dev.chieppa.wrapper.parser.ParserRegex.chapterTotalRegex
import dev.chieppa.wrapper.parser.ParserRegex.digitsRegex
import dev.chieppa.wrapper.parser.ParserRegex.tagTypeRegex
import dev.chieppa.wrapper.parser.ParserRegex.workIDRegex
import dev.chieppa.wrapper.parser.Stat.*
import mu.KotlinLogging
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

private enum class Stat {
    LANGUAGE,
    WORDS,
    LATEST_CHAPTER,
    CHAPTER_TOTAL,
    CHAPTER_CURRENT,
    COLLECTIONS,
    COMMENTS,
    KUDOS,
    BOOKMARKS,
    HITS
}


private val logger = KotlinLogging.logger {}

internal fun extractWork(article: Element): Work {
    val headerLinks =
        extractHeaderValues(article.getFirstByTag("h4").getElementsByTag("a"))

    val stats = extractStats(article.getFirstByClass("stats").getElementsByTag("dd"))

    return Work(
        workId = workIDRegex.getWithZeroDefault(article.attr("id")),
        restricted = article.getFirstByTag("h4").getElementsByTag("img").isNotEmpty(),
        latestChapter = stats.getOrZero(LATEST_CHAPTER),
        archiveSymbols = extractRequiredTags(
            article.getFirstByClass("required-tags").getElementsByTag("span")
        ),
        title = headerLinks.first,
        creators = headerLinks.second.filter { it.authorPseudoName != "" },
        createdFor = headerLinks.second.filter { it.authorPseudoName == "" }.map { it.authorUserName },
        tags = extractTagValues(article.getElementsByClass("tag")),
        summary = article.getElementsByTag("blockquote").firstOrNull()?.outerHtml() ?: "",
        series = article.getElementsByClass("series").firstOrNull()?.let { extractSeries(it) } ?: emptyList(),
        language = stats.getOrDefault(LANGUAGE, Language.UNKNOWN) as Language,
        stats = Stats(
            chapterCount = stats.getOrZero(CHAPTER_CURRENT),
            chapterTotal = if (stats.getOrDefault(CHAPTER_TOTAL, "?") == "?") null else stats[CHAPTER_TOTAL].toString().toInt(),
            wordCount = stats.getOrZero(WORDS),
            dates = WorkSearchDateStat(DateTimeFormats.ddMMMYYYY.parse(article.getFirstByClass("dateTime").text())),
            comments = stats.getOrZero(COMMENTS),
            kudos = stats.getOrZero(KUDOS),
            bookmarks = stats.getOrZero(BOOKMARKS),
            hits = stats.getOrZero(HITS)
        )
    )

}

private fun Map<Stat, Any?>.getOrZero(stat: Stat) = getOrDefault(stat, 0) as Int

private fun extractHeaderValues(links: Elements): Pair<String, List<Creator>> {
    val title = links[0].text()

    val authors = ArrayList<Creator>()
    for (index in 1 until links.size) {
        authors.add(
            Creator(
                authorUserName = ParserRegex.authorUserRegex.getWithEmptyDefault(links[index].attr("href")),
                authorPseudoName = ParserRegex.authorPseudoRegex.getWithEmptyDefault(links[index].attr("href"))
            )
        )
    }

    return Pair(title, authors)
}

private fun extractTagValues(links: Elements): List<Tag> =
    links.map {
        Tag(
            it.text(),
            tagTypeMap.getOrDefault(
                tagTypeRegex.getRegexFound(
                    getTagType(
                        it.parent() ?: throw ExpectedElementException("parent", "could not get parent element")
                    )
                ), UNKNOWN
            )
        )
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

private fun extractSeries(seriesList: Element): MutableList<WorkSeries> {
    val series = mutableListOf<WorkSeries>()

    for (workSeries in seriesList.getElementsByTag("li")) {
        val link = workSeries.getFirstByTag("a")
        series.add(
            WorkSeries(
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

private fun extractStats(links: Elements): Map<Stat, Any?> {
    val statsMap = HashMap<Stat, Any?>()

    for (link in links) {
        when (val statName = link.className().toString()) {
            "language" -> statsMap[LANGUAGE] = Language.languageMap.getOrDefault(link.text(), Language.UNKNOWN)
            "words" -> statsMap[WORDS] = link.text().commaSeparatedToInt()
            "chapters" -> {
                statsMap[LATEST_CHAPTER] = extractLatestChapterChapter(link.getElementsByTag("a"))
                statsMap[CHAPTER_TOTAL] = chapterTotalRegex.getRegexFound(link.text().removeCommas())
                statsMap[CHAPTER_CURRENT] =
                    ParserRegex.chapterCurrentRegex.getWithZeroDefault(link.text().removeCommas())
            }
            "collections" -> statsMap[COLLECTIONS] = link.text().commaSeparatedToInt()
            "comments" -> statsMap[COMMENTS] = link.text().commaSeparatedToInt()
            "kudos" -> statsMap[KUDOS] = link.text().commaSeparatedToInt()
            "bookmarks" -> statsMap[BOOKMARKS] = link.text().commaSeparatedToInt()
            "hits" -> statsMap[HITS] = link.text().commaSeparatedToInt()
            else -> logger.warn("$statName unrecognized, ${link.flattenedHtml()}")
        }
    }

    return statsMap
}

private fun String.removeCommas() = this.replace(",", "")

private fun extractLatestChapterChapter(links: Elements): Int {
    if (links.isEmpty())
        return 0
    return ParserRegex.chapterRegex.getWithZeroDefault(links[0].href())
}