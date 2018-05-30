package ajc2.ahead

import java.util.Random

class Head {
    val rand = Random()
    val stack = Stack()
    
    var register = 0
    var posX = 0
    var posY = 0
    var dirX = 1
    var dirY = 0
    var mode = HeadMode.NORMAL

    /**
     * Do one step of execution on the board.
     */
    fun step(board: Board) {
        // interpret current cell
        val cell = board[posY][posX]
        doCell(cell)

        // move
    }

    private fun doCell(cell: Char) {
        var a: Int = 0
        var b: Int = 0
        
        when(cell) {
            '^' -> {
                dirX = 0
                dirY = -1
            }
            'v' -> {
                dirX = 0
                dirY = 1
            }
            '<' -> {
                dirX = -1
                dirY = 0
            }
            '>' -> {
                dirX = 1
                dirY = 0
            }
            '&' -> register = stack.pop()
            't' -> stack.push(register)
            '+' -> stack.push(stack.pop() + stack.pop())
            '-' -> {
                b = stack.pop()
                a = stack.pop()
                stack.push(a - b)
            }
            '*' -> stack.push(stack.pop() * stack.pop())
            
        }
    }

    // Reflect the direction of the head.
    private fun reflect() {
        dirX = -dirX
        dirY = -dirY
    }
}

enum class HeadMode {
    NORMAL,
    NUMBER,
    STRING,
    FLOATING,
    DEAD
}

