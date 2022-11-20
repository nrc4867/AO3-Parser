package dev.chieppa.wrapper.model.result

import dev.chieppa.wrapper.model.result.navigation.Navigation
import dev.chieppa.wrapper.model.result.people.Person
import java.io.Serializable

@kotlinx.serialization.Serializable
data class PeopleResult(val found: Int, val navigation: Navigation, val people: List<Person>): Serializable
