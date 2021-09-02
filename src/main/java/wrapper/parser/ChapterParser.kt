package wrapper.parser

import model.result.ChapterQueryResult
import model.work.ChapterProperty
import model.work.Creator
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.time.format.DateTimeFormatter

class ChapterParser: Parser<ChapterQueryResult> {

    companion object {
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("(YYYY-MM-dd)")

        val chapterIdRegex: Regex = Regex("(?<=/chapters/)[\\d]+")
        val chapterNumberRegex: Regex = Regex("\\d+")
        val chapterTitleRegex: Regex = Regex("(?<=[.] )(.*)")

    }

    override fun parsePage(queryResponse: String): ChapterQueryResult {
        val doc: Document = Jsoup.parse(queryResponse)
        val mainBody = doc.getElementById("main")

        val heading = readHeading(mainBody)
        val chapters = readChapters(mainBody)

        return ChapterQueryResult(heading.first, heading.second, heading.third, chapters)
    }

    /**
     * Get all the attributes from the heading,
     *  WorkId, WorkTitle, Creators
     */
    private fun readHeading(mainBody: Element): Triple<Int, String, List<Creator>> {
        val heading = mainBody.getElementsByTag("h2")[0]
        val links = heading.getElementsByTag("a")
        val workID: Int = SearchParser.getRegexFound(SearchParser.workIDRegex, links[0].attr("href"), 0)
        val workTitle: String = links[0].text()

        val creators = mutableListOf<Creator>()
        for (index in 1 until links.size) {
            val author = SearchParser.getRegexFound(SearchParser.authorUserRegex, links[index].attr("href"))
            val pseudo = SearchParser.getRegexFound(SearchParser.authorPseudoRegex, links[index].attr("href"))
            creators.add(Creator(author.orEmpty(), pseudo.orEmpty()))
        }

        return Triple(workID, workTitle, creators)
    }

    private fun readChapters(mainBody: Element): List<ChapterProperty> {
        val chapters = mutableListOf<ChapterProperty>()
        mainBody.getElementsByTag("li").forEach {
            chapters.add(readChapterProperty(it))
        }
        return chapters
    }

    private fun readChapterProperty(chapter: Element): ChapterProperty {
        val chapterTitle = SearchParser.getRegexFound(chapterTitleRegex, chapter.allElements[1].text(), "")
        val chapterNumber = SearchParser.getRegexFound(chapterNumberRegex, chapter.allElements[1].text(), 0)
        val chapterId: Int = SearchParser.getRegexFound(chapterIdRegex, chapter.allElements[1].attr("href"), 0)

        val date = dateTimeFormatter.parse(chapter.allElements[2].text())

        return ChapterProperty(chapterNumber, chapterTitle, chapterId, date)
    }
}