package ajc2.ahead

import java.io.PushbackReader

/**
 * A wrapper for all head IO functions
 */
class IOWrapper {
    private val input = PushbackReader(System.`in`.reader())

    fun getChar(): Int? {
        if(!input.ready()) return null
        val i = input.read()

        return if(i == -1) null else i
    }

    fun getNumber(): Int? {
        if(!input.ready()) return null
        val acc = StringBuilder()
        var i: Int

        // read until first digit character
        do {
            i = input.read()
        } while(i != -1 && i.toChar() !in '0'..'9')

        // read digits until last digit character
        while(i != -1 && i.toChar() in '0'..'9') {
            acc.append(i)
            i = input.read()
        }

        if(i != -1) input.unread(i)
        
        // parse or return null
        try {
            return acc.toString().toInt()
        } catch(e: NumberFormatException) {
            return null
        }
    }

    fun close() {
        input.close()
    }
}
