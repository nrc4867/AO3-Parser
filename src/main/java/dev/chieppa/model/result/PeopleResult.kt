package dev.chieppa.model.result

import dev.chieppa.model.result.navigation.Navigation
import dev.chieppa.model.result.people.Person
import java.io.Serializable

@kotlinx.serialization.Serializable
data class PeopleResult(val found: Int, val navigation: Navigation, val people: List<Person>): Serializable
