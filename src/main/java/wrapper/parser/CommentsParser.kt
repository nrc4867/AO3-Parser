package wrapper.parser

import model.result.comment.Comment
import model.result.comment.CommentResult

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

    private fun parseCommentTotal(text: String): Int {
        TODO()
    }

    private fun parseCurrentPage(text: String): Int {
        TODO()
    }

    private fun parseCommentTree(text: String): List<Comment> {
        TODO()
    }

}