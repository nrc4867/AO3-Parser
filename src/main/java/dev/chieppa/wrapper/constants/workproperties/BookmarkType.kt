package dev.chieppa.wrapper.constants.workproperties

import mu.KotlinLogging.logger

enum class BookmarkType(val search_param: String) {
    PUBLIC("public"),
    PRIVATE("private"),
    REC("rec"),
    ADMIN("admin"),
    UNKNOWN("unknown");
}

fun parseBookmarkType(value: String): BookmarkType {
    return try {
        BookmarkType.valueOf(value.uppercase())
    } catch (exception: Exception) {
        logger("Failed to parse bookmark value: $value")
        logger("Exception: ${exception.localizedMessage}")
        BookmarkType.UNKNOWN
    }
}