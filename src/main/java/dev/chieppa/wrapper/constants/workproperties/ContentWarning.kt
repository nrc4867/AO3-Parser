package dev.chieppa.wrapper.constants.workproperties

enum class ContentWarning(val search_param: Int, val class_name: String) {
    ARCHIVE_WARNING_APPLIES(14, "warning-choosenotto"),
    GRAPHIC_DEPICTIONS_OF_VIOLENCE(17, ""),
    MAJOR_CHARACTER_DEATH(18, ""),
    NO_ARCHIVE_WARNINGS(16, "warning-no"),
    NON_CON(19, ""),
    UNDERAGE(20, ""),
    EXTERNAL_WORK(0, "external-work"),

    /**
     * There aren't specific archive symbols for all the warnings, I use this as a catch-all
     * I believe that if this tag is used by ao3 you should be able to find a corresponding tag
     * in the additional tags section.
     * I am not implementing anything additional to specifically catch the archive warnings above.
     * A future user could extend the Archive Symbols class if they need more control over these options.
    */
    GROUP_WARNING(0, "warning-yes") // the search id for this warning is invalid
    ;

    companion object {
        val contentWarningMap = HashMap<String, ContentWarning>()
        init {
            for (contentWarning in values()) {
                contentWarningMap[contentWarning.class_name] = contentWarning
            }
            contentWarningMap.remove("")
        }
    }


}