package dev.chieppa.wrapper.exception.parserexception

data class SearchParserException(
    val field: String,
    val value: String,
) : RuntimeException("The search parser failed interpret $field from $value")