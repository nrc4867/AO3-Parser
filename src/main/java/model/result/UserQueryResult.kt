package model.result

import model.result.work.Creator
import java.io.Serializable

@kotlinx.serialization.Serializable
data class UserPseuds(
    val pseuds: List<Creator>,
    val totalPseuds: Int
): Serializable

@kotlinx.serialization.Serializable
data class UserQueryResult<E>(
    val userPseuds: UserPseuds,
    val works: Int,
    val series: Int,
    val bookmarks: Int,
    val collections: Int,
    val gifts: Int,
    val drafts: Int?,
    val inbox: Int?,
    val signups: Int?,
    val assignments: Int?,
    val claims: Int?,
    val relatedWorks: Int?,
    val queryResult: E
): Serializable
