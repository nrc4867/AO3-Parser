package dev.chieppa.model.result

import dev.chieppa.model.result.comment.Comment
import dev.chieppa.model.result.navigation.Navigation
import java.io.Serializable


@kotlinx.serialization.Serializable
data class CommentResult(val totalComments: Int, val navigation: Navigation, val topLevelComments: List<Comment>) :
    Serializable

