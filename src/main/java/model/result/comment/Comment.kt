package model.result.comment

import model.result.work.Creator
import java.io.Serializable
import java.time.temporal.TemporalAccessor

@kotlinx.serialization.Serializable
data class Comment(
    val commentID: Int,
    val parentThreadID: Int?,
    val displayName: String,
    val creator: Creator?,
    val imageLink: String?,
    val chapter: Int,
    val datePosted: TemporalAccessor,
    val dateEdited: TemporalAccessor?,
    val contents: String,
    val subComments: List<Comment>
) : Serializable