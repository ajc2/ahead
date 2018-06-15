package ajc2.ahead

import java.util.Scanner
import java.util.regex.Pattern

/**
 * A wrapper for all head IO functions
 */
class IOWrapper {
    private val input = Scanner(System.`in`, "utf8")
    private val charPat = Pattern.compile(".", Pattern.DOTALL)
    private val numDelim = Pattern.compile("""\D+""")

    /**
     * Return the next character in the input stream,
     * or null if not available.
     */
    fun getChar(): Int? {
        input.useDelimiter("")
        return if(input.hasNext(charPat)) {
            input.next(charPat)[0].toInt()
        } else {
            null
        }
    }

    /**
     * Parse the next decimal integer out of the input.
     */
    fun getNumber(): Int? {
        input.useDelimiter(numDelim)
        return if(input.hasNextInt()) input.nextInt() else null
    }

    /**
     * Close the input/output streams.
     */
    fun close() {
        input.close()
    }
}
