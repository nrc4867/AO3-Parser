package dev.chieppa.model.result

import dev.chieppa.model.result.work.Creator
import java.io.Serializable
import java.time.temporal.TemporalAccessor

@kotlinx.serialization.Serializable
data class UserProfileResult(
    val profileImage: String,
    val title: String?,
    val pseuds: List<Creator>,
    val joinDate: TemporalAccessor,
    val userID: Int,
    val livesIn: String?,
    val birthday: TemporalAccessor?,
    val bio: String?
): Serializable
