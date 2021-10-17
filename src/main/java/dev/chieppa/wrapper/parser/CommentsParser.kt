package dev.chieppa.wrapper.parser

import dev.chieppa.model.result.CommentResult
import dev.chieppa.model.result.comment.Comment
import dev.chieppa.model.result.work.Creator
import dev.chieppa.wrapper.parser.DateTimeFormats.commentDate
import dev.chieppa.wrapper.parser.ParserRegex.authorUserRegex
import dev.chieppa.wrapper.parser.ParserRegex.chapterCurrentRegex
import dev.chieppa.wrapper.parser.ParserRegex.commentEditedDateRegex
import dev.chieppa.wrapper.parser.ParserRegex.commentTreeRegex
import dev.chieppa.wrapper.parser.ParserRegex.commentTreeReplacementDoubleQuote
import dev.chieppa.wrapper.parser.ParserRegex.commentTreeReplacementForwardSlash
import dev.chieppa.wrapper.parser.ParserRegex.commentTreeReplacementNewLine
import dev.chieppa.wrapper.parser.ParserRegex.commentTreeReplacementSingleQuote
import dev.chieppa.wrapper.parser.ParserRegex.currentPageRegex
import dev.chieppa.wrapper.parser.ParserRegex.digitsRegex
import dev.chieppa.wrapper.parser.ParserRegex.firstWordRegex
import dev.chieppa.wrapper.parser.ParserRegex.totalCommentsRegex
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class CommentsParser : Parser<CommentResult> {

    private val defaultImage = "/images/skins/iconsets/default/icon_user.png"

    override fun parsePage(queryResponse: String): CommentResult {

        var totalComments = 0
        var currentPage = 1
        lateinit var commentTree: List<Comment>


        for ((line, text) in queryResponse.lines().withIndex()) {
            when (line) {
                3 -> totalComments = parseCommentTotal(text)
                5 -> currentPage = parseCurrentPage(text)
                6 -> commentTree = parseCommentTree(text)
            }
        }

        return CommentResult(totalComments, currentPage, commentTree)

    }

    private fun parseCommentTotal(text: String): Int = totalCommentsRegex.getWithZeroDefault(text)

    private fun parseCurrentPage(text: String): Int = currentPageRegex.getWithZeroDefault(text)

    private fun parseCommentTree(text: String): List<Comment> {
        val commentRawHTML = commentTreeRegex
            .getWithEmptyDefault(text)
            .replace(commentTreeReplacementDoubleQuote, "\"")
            .replace(commentTreeReplacementSingleQuote, "\'")
            .replace(commentTreeReplacementNewLine, "")
            .replace(commentTreeReplacementForwardSlash, "/")
            .plus("</ol>")

        val comments = Jsoup.parse(commentRawHTML).getFirstByTag("body").getFirstByTag("ol")
        return parseCommentLevel(comments)
    }

    private fun parseCommentLevel(commentLevel: Element): List<Comment> {
        val comments = mutableListOf<Comment>()
        for (listItem in commentLevel.children()) {
            if (listItem.hasAttr("role")) {

                val subComments: List<Comment> = listItem.nextElementSibling()?.run {
                    when {
                        this.hasAttr("role") -> emptyList()
                        else -> parseCommentLevel(this.children()[0])
                    }
                } ?: emptyList()

                val byline = parseByline(listItem.getFirstByClass("byline"))
                val threadInfo = parseThreadInfo(listItem.getFirstByClass("actions"))

                comments.add(
                    Comment(
                        commentID = threadInfo.first,
                        parentThreadID = threadInfo.second,
                        displayName = byline.first,
                        creator = byline.second,
                        imageLink = listItem.getElementsByTag("img").firstOrNull()?.let { parseImage(it) },
                        chapter = byline.third,
                        datePosted = commentDate.parse(listItem.getFirstByClass("posted datetime").text()),
                        dateEdited = listItem.getElementsByClass("edited datetime").firstOrNull()
                            ?.let { commentDate.parse(commentEditedDateRegex.getWithEmptyDefault(it.text())) },
                        contents = listItem.getFirstByTag("blockquote").outerHtml(),
                        subComments = subComments
                    )
                )
            }
        }
        return comments
    }

    private fun parseThreadInfo(action: Element): Pair<Int, Int?> {
        var commentID = 0
        var parentCommentID: Int? = null

        for (link in action.getElementsByTag("a")) {
            when (link.text()) {
                "Reply" -> {
                }
                "Thread" -> commentID = digitsRegex.getWithZeroDefault(link.href())
                "Parent Thread" -> parentCommentID = digitsRegex.getWithZeroDefault(link.href())
            }
        }

        return Pair(commentID, parentCommentID)
    }

    private fun parseByline(byline: Element): Triple<String, Creator?, Int> {
        return Triple(
            firstWordRegex.getWithEmptyDefault(byline.text()),
            byline.getElementsByTag("a").firstOrNull()?.run {
                Creator(
                    authorUserRegex.getWithEmptyDefault(byline.href()),
                    authorUserRegex.getWithEmptyDefault(byline.href())
                )
            },
            byline.getElementsByClass("parent").firstOrNull()?.let { chapterCurrentRegex.getWithZeroDefault(it.text()) }
                ?: 1
        )
    }

    private fun parseImage(img: Element): String? {
        with(img.attr("src")) {
            return if (this == defaultImage) null
            else this
        }
    }

}