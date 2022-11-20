package dev.chieppa.wrapper.parser

import dev.chieppa.wrapper.constants.workproperties.TagType
import dev.chieppa.wrapper.constants.workproperties.TagType.*
import dev.chieppa.wrapper.model.result.TagSummaryResult
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class TagSummaryParser : Parser<TagSummaryResult> {
    override fun parsePage(queryResponse: String): TagSummaryResult {
        val document = Jsoup.parse(queryResponse)
        val mainBody = document.byIDOrThrow("main")

        val tagBody = mainBody.getFirstByClass("tag")

        return TagSummaryResult(
            primaryTag = mainBody.getFirstByClass("heading").text(),
            category = parseCategory(tagBody),
            filterWorksOrBookmarks = canFilter(tagBody),

            merger = getMerger(tagBody),
            parentTags = getSubtags(tagBody, "parent"),
            synonymTags = getSubtags(tagBody, "synonym"),
            subTags = getSubtags(tagBody, "sub"),
            metaTags = getSubtags(tagBody, "meta"),

            childTags = getChildTags(tagBody),
        )
    }

    private fun parseCategory(tagBody: Element): TagType {
        val category = tagBody
            .getFirstByTag("p")
            .text()
            .lowercase()

        return with(category) {
            when {
                contains("fandom") -> FANDOMS
                contains("warn") -> WARNING
                contains("relation") -> RELATIONSHIP
                contains("character") -> CHARACTER
                contains("additional") -> FREEFORM
                contains("rating") -> RATING
                else -> UNKNOWN
            }
        }
    }

    private fun canFilter(tagBody: Element): Boolean {
        return tagBody.getFirstByTag("p").getElementsByTag("a").isNotEmpty()
    }

    private fun getMerger(tagBody: Element): String? {
        return tagBody.getElementsByClass("merger")
            .first()
            ?.getFirstByTag("a")
            ?.text()
    }

    private fun getChildTags(tagBody: Element): Map<TagType, List<String>> {
        val childSection = tagBody.getElementsByClass("child").first() ?: return emptyMap()

        return mapOf(
            Pair(CHARACTER, getSubtags(childSection, "characters")),
            Pair(RELATIONSHIP, getSubtags(childSection, "relationships")),
            Pair(FREEFORM, getSubtags(childSection, "freeforms")),
        )
    }

    private fun getSubtags(tagBody: Element, group: String): List<String> {
        return tagBody.getElementsByClass(group)
            .first()
            ?.getElementsByTag("a")
            ?.map { it.text() }
            ?.toList()
            ?: emptyList()
    }
}