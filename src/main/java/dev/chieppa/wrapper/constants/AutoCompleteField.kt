package dev.chieppa.wrapper.constants

enum class AutoCompleteField(val search_param: String) {
    FANDOM("fandom"),
    RELATIONSHIP("relationship"),
    CHARACTER("character"),
    FREEFORM("freeform"),
    TAG("tag"),
    PSEUD("pseud")
}