package dev.chieppa.wrapper.exception.parserexception

open class ExpectedElementException(selection: String, override val message: String) :
    RuntimeException("$message: '$selection'")

class ExpectedElementByIDException(
    selection: String,
    message: String = "Could not select element by id"
) : ExpectedElementException(selection, message)

class ExpectedAttributeException(
    selection: String,
    message: String = "Could find elements attribute"
) : ExpectedElementException(selection, message)
