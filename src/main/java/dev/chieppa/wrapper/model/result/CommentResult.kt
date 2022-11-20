package dev.chieppa.wrapper.model.result


import dev.chieppa.wrapper.model.result.comment.Comment
import dev.chieppa.wrapper.model.result.navigation.Navigation
import java.io.Serializable


@kotlinx.serialization.Serializable
data class CommentResult(val totalComments: Int, val navigation: Navigation, val topLevelComments: List<Comment>) :
    Serializable

