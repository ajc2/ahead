package ajc2.ahead

import java.io.File
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.types.*

const val HELP_TEXT = "Start an Ahead interpreter for given SCRIPT."
const val EPILOG_TEXT = "For info see https://github.com/ajc2/ahead"

class Ahead : CliktCommand(help = HELP_TEXT, epilog = EPILOG_TEXT) {
    val stackContents by option("-s", "--stack", help = "Comma-separated list of initial stack contents.").convert {
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
    val script by argument()

    override fun run() {
        val board: Board
        val head: Head

        head = Head()
        
        if(stackContents != null && stackContents?.size != 0) {
            head.stack.addAll(stackContents!!)
        }

        if(evaluate) {
            board = Board(script)
        } else {
            board = Board(File(script))
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
