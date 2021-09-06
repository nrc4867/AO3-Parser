package wrapper.parser

import constants.workproperties.*
import exception.parserexception.SearchParserException
import model.result.work.ArchiveSymbols
import model.result.work.Creator
import model.result.work.Tag
import model.result.work.Work
import mu.KotlinLogging
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import util.commaSeparatedToInt

private val logger = KotlinLogging.logger {}

internal fun extractWork(article: Element): Work {
    val headerLinks: Map<String, Any> =
        extractHeaderValues(article.getElementsByTag("h4")[0].getElementsByTag("a"))

    val stats = extractStats(article.getElementsByClass("stats")[0].getElementsByTag("dd"))

    return Work(
        workId = ParserRegex.workIDRegex.getRegexFound(article.attr("id"), 0),
        latestChapter = stats.getOrDefault("latestChapter", 0) as Int,
        archiveSymbols = extractRequiredTags(
            article.getElementsByClass("required-tags")[0].getElementsByTag("span")
        ),
        title = headerLinks["title"] as String,
        creators = headerLinks["authors"] as ArrayList<Creator>,
        tags = extractTagValues(article.getElementsByClass("tag")).filter { it.tagType != TagType.UNKNOWN }
            .toMutableList(),
        summary = if (article.getElementsByTag("blockquote")
                .isNotEmpty()
        ) article.getElementsByTag("blockquote")[0].children().eachText()
            .joinToString(separator = "\n") else "",
        chapterCount = stats.getOrDefault("chapterCurrent", 0) as Int,
        chapterTotal = if (stats["chapterTotal"]?.toString()
                ?.equals("?") == true
        ) null else stats["chapterTotal"]?.toString()?.toInt(),
        word_count = stats.getOrDefault("words", 0) as Int,
        dateUpdated = DateTimeFormats.ddMMMYYYY.parse(article.getElementsByClass("dateTime")[0].text()),
        language = stats.getOrDefault("language", Language.UNKNOWN) as Language,
        comments = stats.getOrDefault("comments", 0) as Int,
        kudos = stats.getOrDefault("kudos", 0) as Int,
        bookmarks = stats.getOrDefault("bookmarks", 0) as Int,
        hits = stats.getOrDefault("hits", 0) as Int
    )

}

private fun extractHeaderValues(links: Elements): Map<String, Any> {
    val headerValuesMap = HashMap<String, Any>()

    headerValuesMap["title"] = links[0].text()

    headerValuesMap["authors"] = ArrayList<Creator>()
    for (index in 1 until links.size) {
        (headerValuesMap["authors"] as ArrayList<Creator>).add(
            Creator(
                authorUserName = ParserRegex.authorUserRegex.getRegexFound(links[index].attr("href"), ""),
                authorPseudoName = ParserRegex.authorPseudoRegex.getRegexFound(links[index].attr("href"), "")
            )
        )
    }

    return headerValuesMap
}

private fun extractTagValues(links: Elements): ArrayList<Tag> {
    val tags = ArrayList<Tag>()

    for (link in links) {
        tags.add(
            Tag(
                text = link.text(),
                tagType = TagType.tagTypeMap.getOrDefault(
                    ParserRegex.tagTypeRegex.getRegexFound(
                        if (!link.parent().tagName().equals("strong")) link.parent().attr("class")
                        else link.parent().parent().attr("class"),
                        ""
                    ),
                    TagType.UNKNOWN
                )
            )
        )
    }

    return tags
}

private fun extractRequiredTags(links: Elements): ArchiveSymbols {
    return ArchiveSymbols(
        contentRating = ContentRating.contentRatingMap[getFirstClass(links[0].className())]
            ?: throw SearchParserException("required_tag content_rating", getFirstClass(links[0].className())),
        category = Category.categoryMap[getFirstClass(links[4].className())]
            ?: throw SearchParserException("required_tag category", getFirstClass(links[4].className())),
        contentWarning = ContentWarning.contentWarningMap[getFirstClass(links[2].className())]
            ?: throw SearchParserException("required_tag content_warnign", getFirstClass(links[2].className())),
        completionStatus = CompletionStatus.completionStatusMap[getFirstClass(links[6].className())]
            ?: throw SearchParserException("required_tag completion_status", getFirstClass(links[6].className()))
    )
}


private fun getFirstClass(class_name: String): String {
    return class_name.split(" ", limit = 2)[0]
}

private fun extractStats(links: Elements): Map<String, Any?> {
    val statsMap = HashMap<String, Any?>()

    for (link in links) {
        when (val className = link.className().toString()) {
            "language" -> statsMap["language"] = Language.languageMap.getOrDefault(link.text(), Language.UNKNOWN)
            "words" -> statsMap["words"] = link.text().commaSeparatedToInt()
            "chapters" -> {
                statsMap["latestChapter"] =
                    ParserRegex.chapterRegex.getRegexFound(
                        if (link.getElementsByTag("a").isNullOrEmpty()) ""
                        else link.getElementsByTag("a")[0].attr("href"), 0
                    )
                statsMap["chapterTotal"] = ParserRegex.chapterTotalRegex.getRegexFound(link.text().replace(",", ""))
                statsMap["chapterCurrent"] =
                    ParserRegex.chapterCurrentRegex.getRegexFound(link.text().replace(",", ""), 0)
            }
            "collections" -> statsMap["collections"] = link.text().commaSeparatedToInt()
            "comments" -> statsMap["comments"] = link.text().commaSeparatedToInt()
            "kudos" -> statsMap["kudos"] = link.text().commaSeparatedToInt()
            "bookmarks" -> statsMap["bookmarks"] = link.text().commaSeparatedToInt()
            "hits" -> statsMap["hits"] = link.text().commaSeparatedToInt()
            else -> logger.warn("$className unrecognized, ${link.flattenedHtml()}")
        }
    }

    return statsMap
}