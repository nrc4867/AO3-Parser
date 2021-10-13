package model.result.comment

import model.result.work.Creator
import java.io.Serializable
import java.time.temporal.TemporalAccessor

@kotlinx.serialization.Serializable
data class Comment(
    val commenter: Creator,
    val guestComment: Boolean,
    val imageLink: String,
    val chapter: Int,
    val datePosted: TemporalAccessor,
    val subComments: List<Comment>
) : Serializable