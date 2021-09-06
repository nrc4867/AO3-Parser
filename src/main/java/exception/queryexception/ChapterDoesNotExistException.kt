package exception.queryexception

data class ChapterDoesNotExistException(val chapterID: Int) :
    RuntimeException("The chapter with ID $chapterID either does not exist or you have insufficient permissions to view this chapter")