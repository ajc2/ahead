package ajc2.ahead

import java.util.Scanner
import java.util.regex.Pattern
import java.io.InputStream
import java.io.DataInputStream
import java.io.PrintStream
import java.io.OutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.charset.Charset

/**
 * A wrapper for all head IO functions
 */
interface IOWrapper {
    /**
     * Read small value from stdin (char or byte)
     */
    fun getChar(): Int?

    /**
     * Read large value from stdin (integer)
     */
    fun getNumber(): Int?

    /**
     * Write small value to stdout
     */
    fun printChar(v: Int)

    /**
     * Write large value to stdout
     */
    fun printNumber(v: Int)

    /**
     * Close io streams
     */
    fun close()
}

/**
 * IOWrapper for text mode
 */
class TextIOWrapper(
    val inStream: InputStream = System.`in`,
    val outStream: OutputStream = System.out,
    val charset: Charset = Charset.defaultCharset()
) : IOWrapper {

    private val input = Scanner(inStream, charset.name())
    private val charPat = Pattern.compile(".", Pattern.DOTALL)
    private val numPat = Pattern.compile("""[+-]?\d+""")

    /**
     * Return the next character in the input stream,
     * or null if not available.
     */
    override fun getChar(): Int? {
        return input.findWithinHorizon(charPat, 0)?.get(0)?.toInt()
    }

    /**
     * Parse the next decimal integer out of the input.
     */
    override fun getNumber(): Int? {
        return input.findWithinHorizon(numPat, 0)?.toInt()
    }

    /**
     * Print this value as a char
     */
    override fun printChar(v: Int) {
        if(v < 0) {
            outStream.write('\uFFFD'.toString().toByteArray(charset))
        }
        else {
            outStream.write(v.toChar().toString().toByteArray(charset))
        }
        outStream.flush()
    }

    /**
     * Print this value as an integer
     */
    override fun printNumber(v: Int) {
        outStream.write(v.toString().toByteArray(charset))
        outStream.flush()
    }

    /**
     * Close the input/output streams.
     */
    override fun close() {
        input.close()
    }
}

/**
 * IOWrapper for little-endian binary mode
 */
class BinLittleIOWrapper(
    val inStream: InputStream = System.`in`,
    val outStream: OutputStream = System.out
) : IOWrapper {
    
    private val input = DataInputStream(inStream)
    private val output = DataOutputStream(outStream)
    
    override fun getChar(): Int? {
        var o: Int?
        try {
            o = input.readUnsignedByte()
        }
        catch(ex: IOException) {
            return null
        }
        return o
    }
    
    override fun getNumber(): Int?  {
        val o: Int?
        try {
            val buf = ByteArray(4)
            input.readFully(buf)
            val i = buf.map(Byte::toInt)
            o = (i[3] shl 24)or(i[2] shl 16)or(i[1] shl 8)or(i[0])
        }
        catch(ex: IOException) {
            return null
        }
        return o
    }
    
    override fun printChar(v: Int) {
        output.writeByte(v)
        output.flush()
    }
    
    override fun printNumber(v: Int) {
        repeat(4) {
            output.writeByte((v shr (it*8)) and 0xFF)
            output.flush()
        }
    }
    
    override fun close() {
        input.close()
        output.close()
    }
}

class BinBigIOWrapper(
    val inStream: InputStream = System.`in`,
    val outStream: OutputStream = System.out
) : IOWrapper {
    
    private val input = DataInputStream(inStream)
    private val output = DataOutputStream(outStream)
    
    override fun getChar(): Int? {
        var o: Int?
        try {
            o = input.readUnsignedByte()
        }
        catch(ex: IOException) {
            return null
        }
        return o
    }
    
    override fun getNumber(): Int? {
        var o: Int?
        try {
            o = input.readInt()
        }
        catch(ex: IOException) {
            return null
        }
        return o
    }
    
    override fun printChar(v: Int) {
        output.writeByte(v)
        output.flush()
    }
    
    override fun printNumber(v: Int) {
        output.writeInt(v)
        output.flush()
    }
    
    override fun close() {
        input.close()
        output.close()
    }
}

/**
 * IOWrapper that does nothing.
 * Included just in case
 */
class NullIOWrapper: IOWrapper {
    override fun getChar() = null
    override fun getNumber() = null
    override fun printChar(v: Int) {}
    override fun printNumber(v: Int) {}
    override fun close() {}
}
