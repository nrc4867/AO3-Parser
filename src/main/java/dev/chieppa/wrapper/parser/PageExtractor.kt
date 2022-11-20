package dev.chieppa.wrapper.parser

import dev.chieppa.model.result.navigation.Navigation
import org.jsoup.nodes.Element

/**
 * @return Pair<Page, Pages>
 */
fun extractPage(navigation: Element?): Navigation {
    var page = 1
    var pages = 1

    navigation?.let {
        page = it.getElementsByClass("current").getOrNull(0)?.text()?.toInt() ?: Int.MAX_VALUE
        val pageButtons = it.getElementsByClass("next").first()
        pages = pageButtons?.previousElementSibling()?.text()?.toInt() ?: 0
    }

    return Navigation(page, pages)
}