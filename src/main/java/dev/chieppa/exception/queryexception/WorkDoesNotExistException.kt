package dev.chieppa.exception.queryexception

data class WorkDoesNotExistException(val workId: Int) :
    RuntimeException("The work with ID $workId either does not exist or you have insufficient permission to view this work")