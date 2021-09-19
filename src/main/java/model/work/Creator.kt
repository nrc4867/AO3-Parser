package model.work

import java.io.Serializable

@kotlinx.serialization.Serializable
data class Creator(val authorUserName: String, val authorPseudoName: String) : Serializable