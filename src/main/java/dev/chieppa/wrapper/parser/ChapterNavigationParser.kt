package dev.chieppa.wrapper.parser

import dev.chieppa.wrapper.model.result.chapter.ChapterNavigationResult
import dev.chieppa.wrapper.model.result.chapter.FullChapterInfo
import dev.chieppa.wrapper.model.result.work.Creator
import dev.chieppa.wrapper.parser.DateTimeFormats.YYYYMMdd
import dev.chieppa.wrapper.parser.ParserRegex.authorPseudoRegex
import dev.chieppa.wrapper.parser.ParserRegex.authorUserRegex
import dev.chieppa.wrapper.parser.ParserRegex.chapterIdRegex
import dev.chieppa.wrapper.parser.ParserRegex.chapterNumberRegex
import dev.chieppa.wrapper.parser.ParserRegex.chapterTitleRegex
import dev.chieppa.wrapper.parser.ParserRegex.workIDRegex
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ChapterNavigationParser: Parser<ChapterNavigationResult<FullChapterInfo>> {

    override fun parsePage(queryResponse: String): ChapterNavigationResult<FullChapterInfo> {
        val doc: Document = Jsoup.parse(queryResponse)
        val mainBody = doc.byIDOrThrow("main")

        val heading = readHeading(mainBody)
        val chapters = readChapters(mainBody)

        return ChapterNavigationResult(heading.first, heading.second, heading.third, chapters)
    }

    /**
     * Get all the attributes from the heading,
     *  WorkId, WorkTitle, Creators
     */
    private fun readHeading(mainBody: Element): Triple<Int, String, List<Creator>> {
        val heading = mainBody.getElementsByTag("h2")[0]
        val links = heading.getElementsByTag("a")
        val workID: Int = workIDRegex.getRegexFound(links[0].attr("href"), 0)
        val workTitle: String = links[0].text()

        val creators = mutableListOf<Creator>()
        for (index in 1 until links.size) {
            val author = authorUserRegex.getRegexFound(links[index].attr("href"))
            val pseudo = authorPseudoRegex.getRegexFound(links[index].attr("href"))
            creators.add(Creator(author.orEmpty(), pseudo.orEmpty()))
        }

        return Triple(workID, workTitle, creators)
    }

    private fun readChapters(mainBody: Element): List<FullChapterInfo> {
        val chapters = mutableListOf<FullChapterInfo>()
        mainBody.getElementsByTag("li").forEach {
            chapters.add(readChapterProperty(it))
        }
        return chapters
    }

    private fun readChapterProperty(chapter: Element): FullChapterInfo {
        val chapterTitle = chapterTitleRegex.getRegexFound(chapter.allElements[1].text(), "")
        val chapterNumber = chapterNumberRegex.getRegexFound(chapter.allElements[1].text(), 0)
        val chapterId: Int = chapterIdRegex.getRegexFound(chapter.allElements[1].attr("href"), 0)

        val date = YYYYMMdd.parse(chapter.allElements[2].text())

        return FullChapterInfo(chapterNumber, chapterTitle, chapterId, date)
    }
}