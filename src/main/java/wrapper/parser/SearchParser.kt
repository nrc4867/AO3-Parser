package wrapper.parser

import constants.work_properties.*
import exception.SearchParserException
import model.result.SearchResult
import model.work.ArchiveSymbols
import model.work.Creator
import model.work.Tag
import model.work.Work
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.time.format.DateTimeFormatter

class SearchParser : Parser<SearchResult> {

    companion object {
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM YYYY")

        val resultsFoundRegex: Regex = Regex("\\d+")
        val workIDRegex: Regex = Regex("\\d+")
        val authorUserRegex: Regex = Regex("(?<=/users/)[a-zA-Z]+")
        val authorPseudoRegex: Regex = Regex("(?<=pseuds[/])(.*)")
        val chapterIDRegex: Regex = Regex("(?<=chapters[/])(.*)")
        val chapterTotalRegex: Regex = Regex("(?<=\\d[/])(.*)")
        val chapterCurrentRegex: Regex = Regex("\\d+")
        val tagTypeRegex: Regex = Regex("[a-zA-Z]+")

        fun getRegexFound(regex: Regex, text: String): String? = regex.find(text)?.value

        fun getRegexFound(regex: Regex, text: String, default: Int): Int {
            val res = regex.find(text)?.value
            return res?.toInt() ?: default
        }

        fun getRegexFound(regex: Regex, text: String, default: String): String {
            val res = regex.find(text)?.value
            return res ?: default
        }
    }

    internal var resultsFoundParser = {mainBody: Element -> getRegexFound(resultsFoundRegex, mainBody.getElementsByTag("h3")[0].text(), 0)}

    override fun parsePage(queryResponse: String): SearchResult {
        val doc: Document = Jsoup.parse(queryResponse)
        val mainBody = doc.getElementById("main")

        val resultsFound: Int = resultsFoundParser(mainBody)

        var page = 1
        var pages = 1
        if (mainBody.getElementsByAttributeValue("role", "navigation").size > 1) {
            val navigation = mainBody.getElementsByAttributeValue("role", "navigation")[1]
            page = navigation.getElementsByClass("current")[0].text().toInt()
            val pageButtons = navigation.getElementsByTag("a")
            pages = pageButtons[pageButtons.size - 2].text().toInt()
        }

        val works = ArrayList<Work>(20)

        mainBody.getElementsByAttributeValue("role", "article").forEach { article ->
            val headerLinks: Map<String, Any> =
                extractHeaderValues(article.getElementsByTag("h4")[0].getElementsByTag("a"))

            val stats = extractStats(article.getElementsByClass("stats")[0].getElementsByTag("dd"))

            works.add(
                Work(
                    workId = getRegexFound(workIDRegex, article.attr("id"), 0),
                    latestChapter = stats.getOrDefault("latestChapter", 0) as Int,
                    archiveSymbols = extractRequiredTags(
                        article.getElementsByClass("required-tags")[0].getElementsByTag(
                            "span"
                        )
                    ),
                    title = headerLinks["title"] as String,
                    creators = headerLinks["authors"] as ArrayList<Creator>,
                    tags = extractTagValues(article.getElementsByClass("tag")),
                    summary = if (article.getElementsByTag("blockquote")
                            .isNotEmpty()
                    ) article.getElementsByTag("blockquote")[0].children().eachText()
                        .joinToString(separator = "\n") else "",
                    chapterCount = stats.getOrDefault("chapterCurrent", 0) as Int,
                    chapterTotal = if (stats["chapterTotal"]?.toString()
                            ?.equals("?") == true
                    ) null else stats["chapterTotal"]?.toString()?.toInt(),
                    word_count = stats.getOrDefault("words", 0) as Int,
                    dateUpdated = dateTimeFormatter.parse(article.getElementsByClass("dateTime")[0].text()),
                    language = stats.getOrDefault("language", Language.UNKNOWN) as Language,
                    comments = stats.getOrDefault("comments", 0) as Int,
                    kudos = stats.getOrDefault("kudos", 0) as Int,
                    bookmarks = stats.getOrDefault("bookmarks", 0) as Int,
                    hits = stats.getOrDefault("hits", 0) as Int
                )
            )
        }

        return SearchResult(resultsFound, pages, page, works)
    }

    private fun extractHeaderValues(links: Elements): Map<String, Any> {
        val HeaderValuesMap = HashMap<String, Any>()

        HeaderValuesMap["title"] = links[0].text()

        HeaderValuesMap["authors"] = ArrayList<Creator>()
        for (index in 1 until links.size) {
            (HeaderValuesMap["authors"] as ArrayList<Creator>).add(
                Creator(
                    authorUserName = getRegexFound(authorUserRegex, links[index].attr("href"), ""),
                    authorPseudoName = getRegexFound(authorPseudoRegex, links[index].attr("href"), "")
                )
            )
        }

        return HeaderValuesMap
    }

    private fun extractTagValues(links: Elements): ArrayList<Tag> {
        val tags = ArrayList<Tag>()

        for (link in links) {
            tags.add(
                Tag(
                    text = link.text(),
                    tagType = TagType.tagTypeMap.getOrDefault(
                        getRegexFound(
                            tagTypeRegex,
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
            contentRating = ContentRating.contentRatingMap.get(getFirstClass(links[0].className()))
                ?: throw SearchParserException("required_tag content_rating", getFirstClass(links[0].className())),
            category = Category.categoryMap.get(getFirstClass(links[4].className()))
                ?: throw SearchParserException("required_tag category", getFirstClass(links[4].className())),
            contentWarning = ContentWarning.contentWarningMap.get(getFirstClass(links[2].className()))
                ?: throw SearchParserException("required_tag content_warnign", getFirstClass(links[2].className())),
            completionStatus = CompletionStatus.completionStatusMap.get(getFirstClass(links[6].className()))
                ?: throw SearchParserException("required_tag completion_status", getFirstClass(links[6].className()))
        )
    }

    private fun getFirstClass(class_name: String): String {
        return class_name.split(" ", limit = 2)[0]
    }

    private fun extractStats(links: Elements): Map<String, Any?> {
        val statsMap = HashMap<String, Any?>()

        for (link in links) {
            val className = link.className().toString()
            when (className) {
                "language" -> statsMap["language"] = Language.languageMap.getOrDefault(link.text(), Language.UNKNOWN)
                "words" -> statsMap["words"] = link.text().replace(",", "").toInt()
                "chapters" -> {
                    statsMap["latestChapter"] =
                        getRegexFound(chapterIDRegex,
                            if (link.getElementsByTag("a")
                                    .isNullOrEmpty()
                            ) "" else link.getElementsByTag("a")[0].attr("href"),
                            0)
                    statsMap["chapterTotal"] = getRegexFound(chapterTotalRegex, link.text().replace(",", ""))
                    statsMap["chapterCurrent"] = getRegexFound(chapterCurrentRegex, link.text().replace(",", ""), 0)
                }
                "collections" -> statsMap["collections"] = link.text().replace(",", "").toInt()
                "comments" -> statsMap["comments"] = link.text().replace(",", "").toInt()
                "kudos" -> statsMap["kudos"] = link.text().replace(",", "").toInt()
                "bookmarks" -> statsMap["bookmarks"] = link.text().replace(",", "").toInt()
                "hits" -> statsMap["hits"] = link.text().replace(",", "").toInt()
                else -> print("$className unrecognized")
            }
        }

        return statsMap
    }

}