package wrapper.parser

import model.result.CommentResult
import model.result.comment.Comment
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import wrapper.parser.ParserRegex.commentTreeRegex
import wrapper.parser.ParserRegex.commentTreeReplacementDoubleQuote
import wrapper.parser.ParserRegex.commentTreeReplacementForwardSlash
import wrapper.parser.ParserRegex.commentTreeReplacementNewLine
import wrapper.parser.ParserRegex.commentTreeReplacementSingleQuote
import wrapper.parser.ParserRegex.currentPageRegex
import wrapper.parser.ParserRegex.totalCommentsRegex

class CommentsParser : Parser<CommentResult> {
    override fun parsePage(queryResponse: String): CommentResult {

        var totalComments: Int = 0
        var currentPage: Int = 1
        lateinit var commentTree: List<Comment>


        for ((line, text) in queryResponse.lines().withIndex()) {
            when (line) {
                4 -> totalComments = parseCommentTotal(text)
                6 -> currentPage = parseCurrentPage(text)
                7 -> commentTree = parseCommentTree(text)
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

        val comments = Jsoup.parse(commentRawHTML).getElementsByIndexEquals(0).first()
        return parseCommentLevel(comments)
    }

    private fun parseCommentLevel(commentLevel: Element): List<Comment> {
        val comments = mutableListOf<Comment>()
        for (listItem in commentLevel.children()) {
            if (listItem.hasAttr("role")) {

                val subComments: List<Comment> = listItem.nextElementSibling()?.run {
                    return when {
                        this.hasAttr("role") -> emptyList()
                        else -> parseCommentLevel(this.children().first())
                    }
                } ?: run { emptyList() }

                comments.add(
                    Comment(
                        commentID = TODO(),
                        parentThreadID = TODO(),
                        displayName = TODO(),
                        creator = TODO(),
                        imageLink = TODO(),
                        chapter = TODO(),
                        datePosted = TODO(),
                        contents = TODO(),
                        subComments = subComments
                    )
                )

            }
        }
        return comments
    }

}