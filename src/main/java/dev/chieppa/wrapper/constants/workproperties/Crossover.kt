package dev.chieppa.wrapper.constants.workproperties

enum class Crossover(val search_param: String) {
    INCLUDE_CROSSOVERS(""),
    EXCLUDE_CROSSOVERS("F"),
    ONLY_CROSSOVERS("T")
}