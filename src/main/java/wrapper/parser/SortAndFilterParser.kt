package wrapper.parser

import model.result.filterSidebar.*
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import wrapper.parser.ParserRegex.digitsRegex
import wrapper.parser.ParserRegex.endWorkRegex
import wrapper.parser.ParserRegex.foundWorksRegex
import wrapper.parser.ParserRegex.startWorkRegex
import wrapper.parser.ParserRegex.tagWithoutDigitsRegex

class SortAndFilterParser : Parser<TagSortAndFilterResult> {

    private val logger = KotlinLogging.logger {  }

    private val searchParser: SearchParser = SearchParser().apply {
        resultsFoundParser =
            { mainBody: Element -> foundWorksRegex.getWithZeroDefault(mainBody.getFirstByTag("h2").text()) }
    }

    override fun parsePage(queryResponse: String): TagSortAndFilterResult {

        val doc: Document = Jsoup.parse(queryResponse)
        val mainBody = doc.getElementById("main")

        val searchResult = searchParser.parsePage(queryResponse)
        var startWorks = startWorkRegex.getWithZeroDefault(mainBody.getFirstByTag("h2").text())
        val endWorks: Int

        if (searchResult.found > 0 && startWorks == 0) {
            startWorks = 1
            endWorks = searchResult.found
        } else {
            endWorks = endWorkRegex.getWithZeroDefault(mainBody.getFirstByTag("h2").text())
        }

        return TagSortAndFilterResult(
            searchResult,
            startWorks,
            endWorks,
            parseRatingCount(mainBody.getElementById("include_rating_tags")),
            parseWarningCount(mainBody.getElementById("include_archive_warning_tags")),
            parseCategoryCount(mainBody.getElementById("include_category_tags")),
            parseRecommendations(mainBody.getElementById("include_fandom_tags")),
            parseRecommendations(mainBody.getElementById("include_character_tags")),
            parseRecommendations(mainBody.getElementById("include_relationship_tags")),
            parseRecommendations(mainBody.getElementById("include_freeform_tags"))
        )
    }

    private fun parseRatingCount(ratings: Element): RatingCount {
        var teenAndUp = 0
        var generalAudiences = 0
        var notRated = 0
        var mature = 0
        var explicit = 0

        ratings.getElementsByTag("li").forEach {
            when (tagWithoutDigitsRegex.getWithEmptyDefault(it.text()).trim()) {
                "General Audiences" -> teenAndUp = digitsRegex.getWithZeroDefault(it.text())
                "Teen And Up Audiences" -> generalAudiences = digitsRegex.getWithZeroDefault(it.text())
                "Not Rated" -> notRated = digitsRegex.getWithZeroDefault(it.text())
                "Mature" -> mature = digitsRegex.getWithZeroDefault(it.text())
                "Explicit" -> explicit = digitsRegex.getWithZeroDefault(it.text())
                else -> logger.warn("missing rating condition ${it.text()}")
            }
        }

        return RatingCount(teenAndUp, generalAudiences, notRated, mature, explicit)
    }

    private fun parseWarningCount(warnings: Element): WarningCount {
        var noArchiveWarnings = 0
        var creatorChoseNoWarnings = 0
        var graphicViolence = 0
        var characterDeath = 0
        var underage = 0
        var nonCon = 0

        warnings.getElementsByTag("li").forEach {
            when (tagWithoutDigitsRegex.getWithEmptyDefault(it.text()).trim()) {
                "No Archive Warnings Apply" -> noArchiveWarnings = digitsRegex.getWithZeroDefault(it.text())
                "Creator Chose Not To Use Archive Warnings" -> creatorChoseNoWarnings =
                    digitsRegex.getWithZeroDefault(it.text())
                "Graphic Depictions Of Violence" -> graphicViolence = digitsRegex.getWithZeroDefault(it.text())
                "Major Character Death" -> characterDeath = digitsRegex.getWithZeroDefault(it.text())
                "Underage" -> underage = digitsRegex.getWithZeroDefault(it.text())
                "Rape/Non-Con" -> nonCon = digitsRegex.getWithZeroDefault(it.text())
                else -> logger.warn("missing warning condition ${it.text()}")
            }
        }

        return WarningCount(
            noArchiveWarnings,
            creatorChoseNoWarnings,
            graphicViolence,
            characterDeath,
            underage,
            nonCon
        )
    }

    private fun parseCategoryCount(categories: Element): CategoryCount {
        var femaleMale = 0
        var gen = 0
        var femaleFemale = 0
        var maleMale = 0
        var multi = 0
        var other = 0

        categories.getElementsByTag("li").forEach {
            when (tagWithoutDigitsRegex.getWithEmptyDefault(it.text()).trim()) {
                "F/M" -> femaleMale = digitsRegex.getWithZeroDefault(it.text())
                "Gen" -> gen = digitsRegex.getWithZeroDefault(it.text())
                "F/F" -> femaleFemale = digitsRegex.getWithZeroDefault(it.text())
                "M/M" -> maleMale = digitsRegex.getWithZeroDefault(it.text())
                "Multi" -> multi = digitsRegex.getWithZeroDefault(it.text())
                "Other" -> other = digitsRegex.getWithZeroDefault(it.text())
                else -> logger.warn("missing category condition ${it.text()}")
            }
        }

        return CategoryCount(femaleMale, gen, femaleFemale, maleMale, multi, other)
    }

    private fun parseRecommendations(recommendedCategory: Element): List<RecommendedTag> {
        val recommendations = mutableListOf<RecommendedTag>()

        recommendedCategory.getElementsByTag("li").forEach {
            val name = tagWithoutDigitsRegex.getWithEmptyDefault(it.text()).trim()
            val count = digitsRegex.getWithZeroDefault(it.text())
            val id = digitsRegex.getWithZeroDefault(it.getFirstByTag("label").attr("for"))
            recommendations.add(RecommendedTag(name, count, id))
        }

        return recommendations
    }
}