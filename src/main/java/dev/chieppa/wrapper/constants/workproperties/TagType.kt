package dev.chieppa.wrapper.constants.workproperties

enum class TagType(val tagType: String) {
    FANDOMS("fandoms"),
    WARNING("warnings"),
    CATEGORY("category"),
    RELATIONSHIP("relationships"),
    CHARACTER("characters"),
    FREEFORM("freeforms"),
    RATING("rating"),
    UNKNOWN("unknown")
    ;

    companion object {
        val tagTypeMap: Map<String, TagType>

        init {
            tagTypeMap = HashMap()
            for (tagType in values()) {
                tagTypeMap[tagType.tagType] = tagType
            }
        }
    }
}