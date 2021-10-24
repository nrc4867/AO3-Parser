package dev.chieppa.model.result

import dev.chieppa.model.result.people.Person
import java.io.Serializable

@kotlinx.serialization.Serializable
data class PeopleResult(val found: Int, val pages: Int, val page: Int, val people: List<Person>): Serializable
