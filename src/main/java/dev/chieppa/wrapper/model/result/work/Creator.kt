package dev.chieppa.wrapper.model.result.work

import java.io.Serializable

@kotlinx.serialization.Serializable
data class Creator(val authorUserName: String, val authorPseudoName: String) : Serializable