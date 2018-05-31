package ajc2.ahead

class Runner(val board: Board, val head: Head) {
    fun go() {
        while(!head.isDead()) {
            head.step(board)
        }
    }
}
