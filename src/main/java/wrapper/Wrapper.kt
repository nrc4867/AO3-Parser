package wrapper

import wrapper.parser.Parser
import java.io.InputStream

class  Wrapper<E>(val parser: Parser<E>) {

    fun read(inputStream: InputStream): E {
        return inputStream.bufferedReader().use {
            parser.parsePage(it.readText())
        }
    }

}