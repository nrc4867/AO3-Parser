package wrapper.parser

interface Parser<E> {

    fun parsePage(queryResponse: String) : E

}