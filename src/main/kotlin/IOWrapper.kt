package ajc2.ahead

import java.util.Scanner
import java.util.regex.Pattern

/**
 * A wrapper for all head IO functions
 */
class IOWrapper {
    private val input = Scanner(System.`in`, "utf8")
    private val charPat = Pattern.compile(".", Pattern.DOTALL)
    private val numPat = Pattern.compile("""[+-]?\d+""")

    /**
     * Return the next character in the input stream,
     * or null if not available.
     */
    fun getChar(): Int? {
        return input.findWithinHorizon(charPat, 0)?.get(0)?.toInt()
    }

    /**
     * Parse the next decimal integer out of the input.
     */
    fun getNumber(): Int? {
        return input.findWithinHorizon(numPat, 0)?.toInt()
    }

    /**
     * Close the input/output streams.
     */
    fun close() {
        input.close()
    }
}
