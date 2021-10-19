package dev.chieppa.wrapper.parser

import org.jsoup.nodes.Element

/**
 * @return Pair<Page, Pages>
 */
fun extractPage(navigation: Element?): Pair<Int, Int> {
    var page = 1
    var pages = 1

    navigation?.let {
        page = it.getElementsByClass("current").getOrNull(0)?.text()?.toInt() ?: Int.MAX_VALUE
        val pageButtons = it.getFirstByClass("next")
        pages = pageButtons.previousElementSibling()?.text()?.toInt() ?: 0
    }

    return Pair(page, pages)
}