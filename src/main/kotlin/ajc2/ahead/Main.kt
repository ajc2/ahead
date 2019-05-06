package ajc2.ahead

import java.io.File
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.types.*

const val HELP_TEXT = "Start an Ahead interpreter for given SCRIPT."

class Ahead : CliktCommand(help = HELP_TEXT) {
    val stackContents by option("-s", "--stack", help = "Comma-separated list of initial stack contents.").convert {
        it.split(',').map {
            when {
                it.startsWith("'") -> it[1].toInt()
                else -> it.toInt()
            }
        }
    }
    val evaluate by option("-e", "--eval",
    help = "Treat SCRIPT as Ahead code instead of file path.")
            .flag(default = false)
    val script by argument()

    override fun run() {
        val board: Board
        val head: Head
        val runner: Runner

        head = Head()
        
        if(stackContents?.size != 0) {
            head.stack.addAll(stackContents!!)
        }

        if(evaluate) {
            board = Board(script)
        } else {
            board = Board(File(script))
        }
        runner = Runner(board, head)
        runner.go()
    }
}

fun main(args: Array<String>) = Ahead().main(args)
