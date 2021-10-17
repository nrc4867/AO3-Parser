package dev.chieppa.model.result

import java.io.Serializable

@kotlinx.serialization.Serializable
data class AutoCompleteResult(val id : String, val name : String) : Serializable