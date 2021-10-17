package dev.chieppa.model.searchqueries

data class PeopleQuery(val query: String, val names: List<String>, val fandoms: List<String>) : SearchQuery()
