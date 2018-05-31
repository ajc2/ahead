package ajc2.ahead

fun main(args: Array<String>) {
    val board = Board(args[0])
    val head = Head()
    val runner = Runner(board, head)
    
    runner.go()
}
