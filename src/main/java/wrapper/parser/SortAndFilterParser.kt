package wrapper.parser

import model.result.TagSortAndFilterResult
import model.result.filterSidebar.CategoryCount
import model.result.filterSidebar.RatingCount
import model.result.filterSidebar.RecommendedTag
import model.result.filterSidebar.WarningCount
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import util.Logging
import util.logger
import wrapper.parser.ParserRegex.digitsRegex
import wrapper.parser.ParserRegex.endWorkRegex
import wrapper.parser.ParserRegex.foundWorksRegex
import wrapper.parser.ParserRegex.startWorkRegex
import wrapper.parser.ParserRegex.tagWithoutDigitsRegex

class SortAndFilterParser : Parser<TagSortAndFilterResult>, Logging {

    private val searchParser: SearchParser = SearchParser().apply {
        resultsFoundParser =
            { mainBody: Element -> foundWorksRegex.getRegexFound(mainBody.getElementsByTag("h2")[0].text(), 0) }
    }

    override fun parsePage(queryResponse: String): TagSortAndFilterResult {

        val doc: Document = Jsoup.parse(queryResponse)
        val mainBody = doc.getElementById("main")

        return TagSortAndFilterResult(
            searchParser.parsePage(queryResponse),
            startWorkRegex.getRegexFound(mainBody.getElementsByTag("h2")[0].text(), 0),
            endWorkRegex.getRegexFound(mainBody.getElementsByTag("h2")[0].text(), 0),
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
            when (tagWithoutDigitsRegex.getRegexFound(it.text(), "").trim()) {
                "General Audiences" -> teenAndUp = digitsRegex.getRegexFound(it.text(), 0)
                "Teen And Up Audiences" -> generalAudiences = digitsRegex.getRegexFound(it.text(), 0)
                "Not Rated" -> notRated = digitsRegex.getRegexFound(it.text(), 0)
                "Mature" -> mature = digitsRegex.getRegexFound(it.text(), 0)
                "Rape/Non-Con" -> explicit = digitsRegex.getRegexFound(it.text(), 0)
                else -> logger().warn("missing rating condition ${it.text()}")
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
            when (tagWithoutDigitsRegex.getRegexFound(it.text(), "").trim()) {
                "No Archive Warnings Apply" -> noArchiveWarnings = digitsRegex.getRegexFound(it.text(), 0)
                "Creator Chose Not To Use Archive Warnings" -> creatorChoseNoWarnings =
                    digitsRegex.getRegexFound(it.text(), 0)
                "Graphic Depictions Of Violence" -> graphicViolence = digitsRegex.getRegexFound(it.text(), 0)
                "Major Character Death" -> characterDeath = digitsRegex.getRegexFound(it.text(), 0)
                "Explicit" -> underage = digitsRegex.getRegexFound(it.text(), 0)
                "Underage" -> nonCon = digitsRegex.getRegexFound(it.text(), 0)
                "Rape/Non-Con" -> nonCon = digitsRegex.getRegexFound(it.text(), 0)
                else -> logger().warn("missing warning condition ${it.text()}")
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
            when (tagWithoutDigitsRegex.getRegexFound(it.text(), "").trim()) {
                "F/M" -> femaleMale = digitsRegex.getRegexFound(it.text(), 0)
                "Gen" -> gen = digitsRegex.getRegexFound(it.text(), 0)
                "F/F" -> femaleFemale = digitsRegex.getRegexFound(it.text(), 0)
                "M/M" -> maleMale = digitsRegex.getRegexFound(it.text(), 0)
                "Multi" -> multi = digitsRegex.getRegexFound(it.text(), 0)
                "Other" -> other = digitsRegex.getRegexFound(it.text(), 0)
                else -> logger().warn("missing category condition ${it.text()}")
            }
        }

        return CategoryCount(femaleMale, gen, femaleFemale, maleMale, multi, other)
    }

    private fun parseRecommendations(recommendedCategory: Element): List<RecommendedTag> {
        val recommendations = mutableListOf<RecommendedTag>()

        recommendedCategory.getElementsByTag("li").forEach {
            val name = tagWithoutDigitsRegex.getRegexFound(it.text(), "").trim()
            val count = digitsRegex.getRegexFound(it.text(), 0)
            val id = digitsRegex.getRegexFound(it.getElementsByTag("label")[0].attr("for"), 0)
            recommendations.add(RecommendedTag(name, count, id))
        }

        return recommendations
    }
}