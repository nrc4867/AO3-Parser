package constants.work_properties

enum class SearchQueryParam(val raw: String) {

    QUERY("query"),
    TITLE("title"),
    CREATOR("creators"),
    REVISED_AT("revised_at"),
    COMPLETE("complete"),
    CROSSOVER("crossover"),
    SINGLE_CHAPTER("single_chapter"),
    WORD_COUNT("word_count"),
    LANGUAGE("language_id"),
    FANDOM("fandom_names"),
    RATING("rating_ids"),
    WARNING("archive_warning_ids"),
    CATEGORY("category_ids"),
    CHARACTER_NAME("character_names"),
    RELATIONSHIP_NAMES("relationship_names"),
    ADDITIONAL_TAGS("freeform_names"),
    HITS("hits"),
    KUDOS("kudos_count"),
    COMMENTS("comments_count"),
    BOOKMARKS("bookmarks_count"),
    COLUMN("sort_column"),
    DIRECTION("sort_direction")
}