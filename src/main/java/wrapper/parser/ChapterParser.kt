package wrapper.parser

import constants.SummaryType
import constants.workproperties.Language
import constants.workproperties.Language.Companion.languageMap
import constants.workproperties.TagType
import constants.workproperties.TagType.*
import model.result.chapter.*
import model.result.work.*
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import util.commaSeparatedToInt
import wrapper.parser.DateTimeFormats.YYYYMMddEscaped
import wrapper.parser.ParserRegex.authorPseudoRegex
import wrapper.parser.ParserRegex.authorUserRegex
import wrapper.parser.ParserRegex.chapterCurrentRegex
import wrapper.parser.ParserRegex.chapterTotalRegex
import wrapper.parser.ParserRegex.collectionRegex
import wrapper.parser.ParserRegex.digitsRegex
import java.time.temporal.TemporalAccessor

private val logger = KotlinLogging.logger {}

class ChapterParser : Parser<ChapterResult> {
    override fun parsePage(queryResponse: String): ChapterResult {
        val document = Jsoup.parse(queryResponse)
        val mainBody = document.getElementById("main")

        val workMeta = mainBody.getElementsByClass("meta").first()
        val preface = mainBody.getElementsByClass("preface")

        return ChapterResult(
            workMeta = parseWorkMeta(workMeta),
            chapterNavigationResult = parseNavigation(
                preface[0],
                mainBody.getFirstByClass("download").getFirstByTag("ul").getFirstByTag("a"),
                mainBody.getElementById("selected_id").children()
            ),
            chapterPosition = parsePosition(
                mainBody.getElementById("selected_id").getElementsByAttribute("selected").getOrNull(0)
            ),
            chapterId = parseChapterID(
                mainBody.getElementById("selected_id").getElementsByAttribute("selected").getOrNull(0)
            ),
            authorNotes = parseAuthorNotes(mainBody),
            inspiredWorks = parseInspiredWorks(mainBody.getElementById("children")?.getFirstByTag("ul")?.children()),
            chapterText = mainBody.getElementsByAttributeValue("role", "article").first().outerHtml()
        )
    }

    private fun parseWorkMeta(workMeta: Element): WorkMeta {
        val tags = mutableListOf<Tag>()
        lateinit var language: Language
        var collection: List<WorkCollection> = listOf()
        var series: List<WorkMetaSeries> = listOf()
        lateinit var stats: Stats<WorkMetaDateStat>

        val definiteDescriptions = workMeta.getElementsByTag("dd")
        for (dd in definiteDescriptions) {
            when (dd.className()) {
                "rating tags" -> tags.addAll(dd.extractTags(RATING))
                "warning tags" -> tags.addAll(dd.extractTags(WARNING))
                "category tags" -> tags.addAll(dd.extractTags(RELATIONSHIP))
                "fandom tags" -> tags.addAll(dd.extractTags(FANDOMS))
                "freeform tags" -> tags.addAll(dd.extractTags(FREEFORM))
                "language" -> language = languageMap.getOrDefault(dd.text(), Language.UNKNOWN)
                "collections" -> collection = parseCollections(dd.getElementsByTag("a"))
                "series" -> series = extractSeries(dd.children())
                "stats" -> stats = extractStats(dd.getElementsByTag("dl").first())
                else -> logger.warn("unknown work meta option ${dd.className()}")
            }
        }

        return WorkMeta(
            tags = tags.toList(),
            language = language,
            collection = collection,
            series = series,
            stats = stats
        )

    }

    private fun Element.extractTags(tagType: TagType) = extractTags(this.getElementsByTag("a"), tagType)

    private fun extractTags(tags: Elements, tagType: TagType): List<Tag> {
        val tagsParsed = mutableListOf<Tag>()
        for (tag in tags) {
            tagsParsed.add(Tag(tag.text(), tagType))
        }
        return tagsParsed
    }

    private fun parseCollections(collections: Elements): List<WorkCollection> {
        val workCollections = mutableListOf<WorkCollection>()
        for (link: Element in collections) {
            workCollections.add(WorkCollection(collectionRegex.getWithEmptyDefault(link.href()), link.text()))
        }
        return workCollections
    }

    private fun extractStats(stats: Element): Stats<WorkMetaDateStat> {
        var chapterCount = 0
        var chapterTotal: Int? = null
        var wordCount = 0
        var comments = 0
        var kudos = 0
        var bookmarks = 0
        var hits = 0

        lateinit var updated: TemporalAccessor
        lateinit var datePublished: TemporalAccessor

        for (stat in stats.getElementsByTag("dd")) {
            when (stat.className()) {
                "published" -> datePublished = YYYYMMddEscaped.parse(stat.text())
                "status" -> updated = YYYYMMddEscaped.parse(stat.text())
                "words" -> wordCount = stat.text().commaSeparatedToInt()
                "chapters" -> {
                    chapterCount = chapterCurrentRegex.getWithZeroDefault(stat.text())
                    chapterTotalRegex.getWithEmptyDefault(stat.text()).toIntOrNull()?.let {
                        chapterTotal = it
                    }
                }
                "comments" -> comments = stat.text().commaSeparatedToInt()
                "kudos" -> kudos = stat.text().commaSeparatedToInt()
                "bookmarks" -> bookmarks = stat.text().commaSeparatedToInt()
                "hits" -> hits = stat.text().commaSeparatedToInt()
            }
        }

        val complete: Boolean = stats.getElementsByClass("status")[0].text().startsWith("Completed")

        return Stats(
            chapterCount = chapterCount,
            chapterTotal = chapterTotal,
            wordCount = wordCount,
            comments = comments,
            kudos = kudos,
            bookmarks = bookmarks,
            hits = hits,
            dates = WorkMetaDateStat(datePublished, updated, complete)
        )
    }

    private fun extractSeries(series: Elements): List<WorkMetaSeries> {
        val seriesCollection = mutableListOf<WorkMetaSeries>()
        for (volume in series) {
            seriesCollection.add(
                WorkMetaSeries(
                    digitsRegex.getWithZeroDefault(volume.getFirstByTag("a").href()),
                    volume.getFirstByTag("a").text(),
                    digitsRegex.getRegexFound(volume.getFirstByClass("position").text(), 1),
                    volume.getElementsByClass("previous")
                        .firstNotNullOfOrNull { digitsRegex.getWithZeroDefault(it.href()) },
                    volume.getElementsByClass("next")
                        .firstNotNullOfOrNull { digitsRegex.getWithZeroDefault(it.href()) }
                )
            )
        }
        return seriesCollection
    }

    private fun parseNavigation(
        preface: Element,
        linkWithWorkID: Element,
        chapterOptions: Elements?
    ): ChapterNavigationResult<BasicChapterInfo>? {
        if (chapterOptions == null)
            return null

        val chapters = mutableListOf<BasicChapterInfo>()
        for ((position, option) in chapterOptions.withIndex()) {
            chapters.add(BasicChapterInfo(position, option.text(), option.attr("value").toInt()))
        }

        return ChapterNavigationResult(
            digitsRegex.getRegexFound(linkWithWorkID.href(), 0),
            preface.getFirstByClass("title").text(),
            extractAuthors(preface.getFirstByClass("byline")),
            chapters
        )
    }

    private fun extractAuthors(byline: Element): List<Creator> {
        val creators = mutableListOf<Creator>()
        for (link in byline.getElementsByTag("a")) {
            creators.add(
                Creator(
                    authorUserRegex.getWithEmptyDefault(link.href()),
                    authorPseudoRegex.getWithEmptyDefault(link.href())
                )
            )
        }
        return creators
    }

    private fun parsePosition(chapterSelected: Element?): Int? {
        chapterSelected?.let {
            return digitsRegex.getRegexFound(chapterSelected.text(), 1) - 1
        }
        return null
    }

    private fun parseChapterID(chapterSelected: Element?): Int? {
        chapterSelected?.let {
            return digitsRegex.getRegexFound(chapterSelected.attr("value"), 0)
        }
        return null
    }

    private fun parseAuthorNotes(work: Element): List<AuthorNote> {

        fun Element.getBlockquote() = this.getElementsByTag("blockquote")

        val authorNotes = mutableListOf<AuthorNote>()

        for (summary in work.getElementsByClass("summary")) {
            authorNotes.add(AuthorNote(SummaryType.SUMMARY, summary.getBlockquote().outerHtml()))
        }

        for (note in work.getElementsByClass("notes")) {
            val blockquote = note.getBlockquote().outerHtml()
            if (note.attributes().get("id") == "work_endnotes") {
                authorNotes.add(AuthorNote(SummaryType.WORK_END_NOTE, blockquote))
            } else if (note.hasClass("end")) {
                authorNotes.add(AuthorNote(SummaryType.CHAPTER_END_NOTE, blockquote))
            } else {
                authorNotes.add(AuthorNote(SummaryType.CHAPTER_NOTE, blockquote))
            }
        }

        return authorNotes
    }

    private fun parseInspiredWorks(inspiredList: Elements?): List<InspiredWork>? {
        if (inspiredList == null)
            return null

        val inspiredWorks = mutableListOf<InspiredWork>()
        for (work in inspiredList) {

            val authors = mutableListOf<Creator>()

            for (author in work.getElementsByTag("a").filter { it.hasAttr("rel") }) {
                authors.add(
                    Creator(
                        authorUserRegex.getWithEmptyDefault(author.href()),
                        authorPseudoRegex.getWithEmptyDefault(author.href())
                    )
                )
            }

            work.getFirstByTag("a").let {
                if (it.hasAttr("rel")) {
                    inspiredWorks.add(InspiredWork("Restricted Work", null, authors))
                } else {
                    inspiredWorks.add(
                        InspiredWork(
                            it.text(),
                            digitsRegex.getWithZeroDefault(it.href()),
                            authors
                        )
                    )
                }
            }
        }
        return inspiredWorks
    }
}