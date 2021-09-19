package wrapper.parser

import constants.work_properties.*
import exception.SearchParserException
import model.work.ArchiveSymbols
import model.work.Creator
import model.work.Tag
import model.work.Work
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import wrapper.parser.Stat.*

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

internal fun extractWork(article: Element): Work {
    val headerLinks =
        extractHeaderValues(article.getElementsByTag("h4")[0].getElementsByTag("a"))

    val stats = extractStats(article.getElementsByClass("stats")[0].getElementsByTag("dd"))

    return Work(
        workId = ParserRegex.workIDRegex.getRegexFound(article.attr("id"), 0),
        restricted = article.getElementsByTag("h4")[0].getElementsByTag("img").isNotEmpty(),
        latestChapter = stats.getOrDefault(LATEST_CHAPTER, 0) as Int,
        archiveSymbols = extractRequiredTags(
            article.getElementsByClass("required-tags")[0].getElementsByTag("span")
        ),
        title = headerLinks.first,
        creators = headerLinks.second,
        tags = extractTagValues(article.getElementsByClass("tag")).filter { it.tagType != TagType.UNKNOWN }
            .toMutableList(),
        summary = if (article.getElementsByTag("blockquote")
                .isNotEmpty()
        ) article.getElementsByTag("blockquote")[0].children().eachText()
            .joinToString(separator = "\n") else "",
        chapterCount = stats.getOrDefault(CHAPTER_CURRENT, 0) as Int,
        chapterTotal = if (stats[CHAPTER_TOTAL]?.toString()
                ?.equals("?") == true
        ) null else stats[CHAPTER_TOTAL]?.toString()?.toInt(),
        wordCount = stats.getOrDefault(WORDS, 0) as Int,
        dateUpdated = DateTimeFormats.ddMMMYYYY.parse(article.getElementsByClass("dateTime")[0].text()),
        language = stats.getOrDefault(LANGUAGE, Language.UNKNOWN) as Language,
        comments = stats.getOrDefault(COMMENTS, 0) as Int,
        kudos = stats.getOrDefault(KUDOS, 0) as Int,
        bookmarks = stats.getOrDefault(BOOKMARKS, 0) as Int,
        hits = stats.getOrDefault(HITS, 0) as Int
    )

}

private fun extractHeaderValues(links: Elements): Pair<String, List<Creator>> {
    val title = links[0].text()

    val authors = ArrayList<Creator>()
    for (index in 1 until links.size) {
        authors.add(
            Creator(
                authorUserName = ParserRegex.authorUserRegex.getRegexFound(links[index].attr("href"), ""),
                authorPseudoName = ParserRegex.authorPseudoRegex.getRegexFound(links[index].attr("href"), "")
            )
        )
    }

    return Pair(title, authors)
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
            ?: throw SearchParserException("required_tag content_warning", getFirstClass(links[2].className())),
        completionStatus = CompletionStatus.completionStatusMap[getFirstClass(links[6].className())]
            ?: throw SearchParserException("required_tag completion_status", getFirstClass(links[6].className()))
    )
}


private fun getFirstClass(class_name: String): String {
    return class_name.split(" ", limit = 2)[0]
}

private fun extractStats(links: Elements): Map<Stat, Any?> {
    val statsMap = HashMap<Stat, Any?>()

    for (link in links) {
        when (val statName = link.className().toString()) {
            "language" -> statsMap[LANGUAGE] = Language.languageMap.getOrDefault(link.text(), Language.UNKNOWN)
            "words" -> statsMap[WORDS] = link.text().replace(",", "").toInt()
            "chapters" -> {
                statsMap[LATEST_CHAPTER] =
                    ParserRegex.chapterRegex.getRegexFound(
                        if (link.getElementsByTag("a")
                                .isNullOrEmpty()
                        ) "" else link.getElementsByTag("a")[0].attr("href"),
                        0
                    )
                statsMap[CHAPTER_TOTAL] = ParserRegex.chapterTotalRegex.getRegexFound(link.text().replace(",", ""))
                statsMap[CHAPTER_CURRENT] =
                    ParserRegex.chapterCurrentRegex.getRegexFound(link.text().replace(",", ""), 0)
            }
            "collections" -> statsMap[COLLECTIONS] = link.text().replace(",", "").toInt()
            "comments" -> statsMap[COMMENTS] = link.text().replace(",", "").toInt()
            "kudos" -> statsMap[KUDOS] = link.text().replace(",", "").toInt()
            "bookmarks" -> statsMap[BOOKMARKS] = link.text().replace(",", "").toInt()
            "hits" -> statsMap[HITS] = link.text().replace(",", "").toInt()
            else -> print("$statName unrecognized")
        }
    }

    return statsMap
}