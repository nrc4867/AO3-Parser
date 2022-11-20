package dev.chieppa.wrapper.constants.workproperties

enum class ContentRating(val search_param: Int, val class_name: String) {
    GENERAL(10, "rating-general-audience"),
    TEEN(11, "rating-teen"),
    MATURE(12, "rating-mature"),
    EXPLICIT(13, "rating-explicit"),
    NONE(9, "rating-notrated")
    ;

    companion object {
        val contentRatingMap = HashMap<String, ContentRating>()

        init {
            for (contentRating in values()) {
                contentRatingMap[contentRating.class_name] = contentRating
            }
        }
    }
}