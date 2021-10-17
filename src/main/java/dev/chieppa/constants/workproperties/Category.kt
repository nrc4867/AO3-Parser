package dev.chieppa.constants.workproperties

enum class Category(val search_param: Int, val class_name: String) {
    FEMALE_FEMALE(116, "category-femslash"),
    FEMALE_MALE(22, "category-het"),
    GEN(21, "category-gen"),
    MALE_MALE(23, "category-slash"),
    MULTI(2246, "category-multi"),
    OTHER(24, "category-other"),
    NONE(0, "category-none") // todo find the real value, this should be the no-category option4
    ;

    companion object {
        val categoryMap = HashMap<String, Category>()

        init {
            for (category in values()) {
                categoryMap[category.class_name] = category
            }
        }
    }
}