package wrapper.parser

import org.jsoup.nodes.Element
import java.time.format.DateTimeFormatter

internal object ParserRegex {
    val digitsRegex: Regex by lazy { Regex("\\d+") }
    val firstWordRegex: Regex by lazy { Regex("^([\\w\\-]+)") }

    val resultsFoundRegex: Regex by lazy { digitsRegex }
    val workIDRegex: Regex by lazy { digitsRegex }
    val authorUserRegex: Regex by lazy { Regex("(?<=/users/)[a-zA-Z]+") }
    val authorPseudoRegex: Regex by lazy { Regex("(?<=pseuds[/])(.*)") }
    val chapterRegex: Regex by lazy { Regex("(?<=chapters[/])(.*)") }
    val chapterTotalRegex: Regex by lazy { Regex("(?<=\\d[/])(.*)") }
    val chapterCurrentRegex: Regex by lazy { Regex("\\d+") }
    val tagTypeRegex: Regex by lazy { Regex("[a-zA-Z]+") }

    val chapterIdRegex: Regex by lazy { Regex("(?<=/chapters/)[\\d]+") }
    val chapterNumberRegex: Regex by lazy { digitsRegex }
    val chapterTitleRegex: Regex by lazy { Regex("(?<=[.] )(.*)") }

    val startWorkRegex: Regex by lazy { Regex("\\d+(?= -)") }
    val endWorkRegex: Regex by lazy { Regex("(?<=[-] )\\d+") }
    val foundWorksRegex: Regex by lazy { Regex("\\d+(?= Works)") }

    val tagWithoutDigitsRegex: Regex by lazy { Regex(".* ") }
    val bookmarkerPseudo: Regex by lazy { Regex("(?<=pseuds[/])[a-zA-Z]+") }

    val collectionRegex: Regex by lazy { Regex("(?<=/collections/)\\w+") }

    val giftRegex: Regex by lazy { Regex("(?<=/users/)[a-zA-Z]+(?=/gifts)") }
    val inspiredTranslationRegex: Regex by lazy { Regex("(?<=Translation into )[\\Wa-zA-Z]+(?= available\\W*)") }

    val totalCommentsRegex: Regex by lazy { Regex("(?<=Hide Comments \\()[1-9]*(?=\\).*)") }
    val currentPageRegex: Regex by lazy { Regex("(?<=<span class=\\\\\"current\\\\\">)[1-9]*(?=<\\\\/span>.*)") }
    val commentTreeRegex: Regex by lazy { Regex("(?<=\\.append\\(\").*(?=\\);)") }

    val commentEditedDateRegex: Regex by lazy { Regex("(?<=Last Edited ).*") }

    val commentTreeReplacementDoubleQuote: Regex by lazy { Regex("\\\\\"") }
    val commentTreeReplacementSingleQuote: Regex by lazy { Regex("\\\\\'") }
    val commentTreeReplacementNewLine: Regex by lazy { Regex("\\\\n") }
    val commentTreeReplacementForwardSlash: Regex by lazy { Regex("\\\\/") }
}

internal object DateTimeFormats {
    val ddMMMYYYY: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern("dd MMM YYYY") }
    val YYYYMMdd: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern("(YYYY-MM-dd)") }
    val YYYYMMddEscaped: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern("YYYY-MM-dd") }
    val commentDate: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern("EEE dd LLL yyyy hh:mma z") }
}

internal fun Regex.getRegexFound(text: String): String? = this.find(text)?.value
internal fun Regex.getRegexFound(text: String?, default: Int): Int = this.find(text ?: "")?.value?.toInt() ?: default
internal fun Regex.getRegexFound(text: String?, default: String): String = this.find(text ?: default)?.value ?: default

internal fun Regex.getWithZeroDefault(text: String) = getRegexFound(text, 0)
internal fun Regex.getWithEmptyDefault(text: String) = getRegexFound(text, "")


internal fun Element.flattenedHtml() = this.outerHtml().trim().replace("\n", "")

internal fun Element.href() = this.attr("href")

internal fun Element.getFirstByTag(tagName: String) = this.getElementsByTag(tagName)[0]
internal fun Element.getFirstByClass(className: String) = this.getElementsByClass(className)[0]
internal fun Element.getFirstByAttribute(attrName: String) = this.getElementsByAttribute(attrName)[0]