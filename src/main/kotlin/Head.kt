package ajc2.ahead

import java.util.Random

class Head {
    private val rand = Random()
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
        when(mode) {
            HeadMode.NORMAL -> {
                when(cell) {
                    in '0'..'9' -> {
                        stack.push(cell.toDigit())
                        mode = HeadMode.NUMBER
                    }
                    '\u0022' -> {  //doublequote
                        mode = HeadMode.STRING
                    }
                    '~' -> {
                        mode = HeadMode.FLOATING
                    }
                    '@' -> {
                        mode = HeadMode.DEAD
                        return
                    }
                    else -> {
                        doCell(cell, board)
                    }
                }
            }
            HeadMode.STRING -> {
                if(cell == '\u0022') {
                    mode = HeadMode.NORMAL
                } else {
                    stack.push(cell.toInt())
                }
            }
            HeadMode.NUMBER -> {
                if(cell !in '0'..'9') {
                    mode = HeadMode.NORMAL
                    doCell(cell, board)
                } else {
                    stack.push(stack.pop() * 10 + cell.toDigit())
                }
            }
            HeadMode.FLOATING -> {
                if(cell == '~') {
                    mode = HeadMode.NORMAL
                }
            }
            HeadMode.DEAD -> {
                return
            }
        }
        
        // move
        move(board)
    }

    /**
     * Interpret a cell as an instruction.
     */
    private fun doCell(cell: Char, board: Board) {
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
            '&' -> {
                register = stack.pop()
            }
            't' -> {
                stack.push(register)
            }
            '+' -> {
                stack.push(stack.pop() + stack.pop())
            }
            '-' -> {
                b = stack.pop()
                a = stack.pop()
                stack.push(a - b)
            }
            '*' -> {
                stack.push(stack.pop() * stack.pop())
            }
            '/' -> {
                b = stack.pop()
                a = stack.pop()
                stack.push(a / b)
            }
            'o' -> {
                print(stack.pop().toChar())
            }
            'O' -> {
                print(stack.pop())
            }
            '?' -> {
                if(stack.pop() != 0) reflect()
            }
            ':' -> {
                stack.dup()
            }
            'j' -> {
                move(board)
            }
            
        }
    }

    fun isDead(): Boolean {
        return mode == HeadMode.DEAD
    }

    // Reflect the direction of the head.
    private fun reflect() {
        dirX = -dirX
        dirY = -dirY
    }

    // Move the head to its next position.
    private fun move(board: Board) {
        var nextX = posX + dirX
        var nextY = posY + dirY
        if(!board.inBounds(nextX, nextY)) {
            reflect()
            nextX = posX + dirX
            nextY = posY + dirY
            if(!board.inBounds(nextX, nextY)) return
        }
        
        posX = nextX
        posY = nextY
    }
}

enum class HeadMode {
    NORMAL,
    NUMBER,
    STRING,
    FLOATING,
    DEAD
}

/**
 * Helper extension to convert a digit character
 * to its Int value.
 */
fun Char.toDigit() = this.toInt() - 48
