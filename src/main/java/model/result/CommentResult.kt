package model.result

import model.result.comment.Comment
import java.io.Serializable


@kotlinx.serialization.Serializable
data class CommentResult(val totalComments: Int, val currentPage: Int, val topLevelComments: List<Comment>) :
    Serializable

