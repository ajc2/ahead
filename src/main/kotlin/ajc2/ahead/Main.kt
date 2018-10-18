package ajc2.ahead

import java.io.File

fun main(args: Array<String>) {
    val board: Board
    val head: Head
    val runner: Runner

    board = Board(File(args[0]))
    head = Head()
    runner = Runner(board, head)
    runner.go()
}
