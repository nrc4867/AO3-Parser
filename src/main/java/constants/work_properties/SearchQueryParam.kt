package constants.work_properties

enum class SearchQueryParam(val raw: String) {

    QUERY("query"),
    BOOKMARKABLE_QUERY("bookmarkable_query"),
    BOOKMARK_QUERY("bookmark_query"),
    BOOKMARK_NOTES("bookmark_notes"),
    REC("rec"),
    WITH_NOTES("with_notes"),
    TITLE("title"),
    CREATOR("creators"),
    BOOKMARKER("bookmarker"),
    REVISED_AT("revised_at"),
    DATE_FROM("date_from"),
    DATE_TO("date_to"),
    BOOKMARKABLE_DATE("bookmarkable_date"),
    DATE("date"),
    COMPLETE("complete"),
    CROSSOVER("crossover"),
    SINGLE_CHAPTER("single_chapter"),
    WORD_COUNT("word_count"),
    LANGUAGE("language_id"),
    FANDOM("fandom_names"),
    FANDOM_ID("fandom_ids"),
    RATING("rating_ids"),
    WARNING("archive_warning_ids"),
    CATEGORY("category_ids"),
    CHARACTER_NAME("character_names"),
    CHARACTER_ID("character_ids"),
    RELATIONSHIP_NAMES("relationship_names"),
    RELATIONSHIP_ID("relationship_ids"),
    ADDITIONAL_TAGS("freeform_names"),
    ADDITIONAL_TAGS_IDS("freeform_ids"),
    OTHER_TAGS_NAMES("other_tag_names"),
    OTHER_BOOKMARK_TAG_NAMES("other_bookmark_tag_names"),
    EXCLUDED_TAGS_NAMES("excluded_tag_names"),
    WORDS_FROM("words_from"),
    WORDS_TO("words_to"),
    HITS("hits"),
    KUDOS("kudos_count"),
    COMMENTS("comments_count"),
    BOOKMARKS("bookmarks_count"),
    COLUMN("sort_column"),
    DIRECTION("sort_direction")
}