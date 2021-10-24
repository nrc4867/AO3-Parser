package dev.chieppa.wrapper.parser

import dev.chieppa.model.result.GiftsResult
import org.jsoup.Jsoup

class GiftsParser: Parser<GiftsResult> {

    override fun parsePage(queryResponse: String): GiftsResult {
        val document = Jsoup.parse(queryResponse)
        val mainBody = document.byIDOrThrow("main")

        val navigation = extractPage(mainBody.getElementsByAttributeValue("role", "navigation").getOrNull(1))

        return GiftsResult(navigation, mainBody.getElementsByAttributeValue("role", "article").map { extractWork(it) })
    }

}