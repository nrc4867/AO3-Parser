package constants.workproperties

enum class CompletionStatus(val search_param: String, val class_name : String) {
    IN_PROGRESS("F", "complete-no"),
    COMPLETE("T", "complete-yes"),
    UNKNOWN("", "category-none"),
    EXTERNAL_WORK("", "external-work")
    ;

    companion object {
        val completionStatusMap = HashMap<String, CompletionStatus>()

        init {
            for (completionStatus in values()) {
                completionStatusMap[completionStatus.class_name] = completionStatus
            }
        }
    }
}