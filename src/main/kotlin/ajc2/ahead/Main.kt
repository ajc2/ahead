package ajc2.ahead

import java.io.File
import java.nio.charset.Charset
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.types.*

const val HELP_TEXT = "Start an Ahead interpreter for given SCRIPT."
const val EPILOG_TEXT = "For info see https://github.com/ajc2/ahead"

class Ahead : CliktCommand(help = HELP_TEXT, epilog = EPILOG_TEXT) {
    val stackContents by option("-s", "--stack", help = "Comma-separated list of initial stack contents.")
        .convert {
            it.split(',').map {
                when {
                    it.startsWith("'") -> it[1].toInt()
                    else -> it.toInt()
                }
            }
        }
    val evaluate by option("-e", "--eval", help = "Treat SCRIPT as Ahead code instead of file path.")
            .flag(default = false)
    val debug by option("-d", "--debug", help = "Show verbose debugging information.")
            .flag(default = false)
    val ioMode by option(help = "Specify IO mode.").switch(
        "-b" to "binlittle", "--binlittle" to "binlittle", "--bin" to "binlittle",
        "-B" to "binbig", "--binbig" to "binbig"
    ).default("text")
    val encoding by option("-n", "--encoding", help = "Specify text encoding.")
        .convert {
            Charset.forName(it)
        }
    val script by argument()

    override fun run() {
        val board: Board
        val head: Head

        // select IO wrapper to use based on chosen mode
        val io = when(ioMode) {
            "text" -> TextIOWrapper(charset = encoding ?: Charset.defaultCharset())
            "binlittle" -> BinLittleIOWrapper()
            "binbig" -> BinBigIOWrapper()
            else -> NullIOWrapper()  // if we get here something has gone wrong :grimace:
        }

        head = Head(io)
        
        if(stackContents != null && stackContents?.size != 0) {
            head.stack.addAll(stackContents!!)
        }

        if(evaluate) {
            board = Board(script)
        } else {
            val file = File(script)
            if(file.isFile()) {
                board = Board(file)
            }
            else {
                echo("$script does not exist, or is not a file.", err = true)
                return
            }
        }

        if(debug) echo("BOARD\n-----\n${board.toString()}")
        
        try {
            while(!head.isDead()) {
                if(debug) {
                    echo(head.debugInfo())
                }
                head.step(board)
            }
        } catch(ex: Exception) {
            echo("Encountered exception.", err = true)
            echo("BOARD\n-----\n${board.toString()}", err = true)
            echo(head.debugInfo(), err = true)
            echo("JVM stacktrace below:", err = true)
            ex.printStackTrace()
        }
    }
}

fun main(args: Array<String>) = Ahead().main(args)
