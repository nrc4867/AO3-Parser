package constants.work_properties

enum class CompletionStatus(val search_param: String, val class_name : String) {
    IN_PROGRESS("F", "complete-no"),
    COMPLETE("T", "complete-yes"),
    UNKNOWN("", "category-none")
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