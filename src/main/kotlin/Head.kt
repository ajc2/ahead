package ajc2.ahead

import java.util.Random
import kotlin.math.*

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
        var a: Int
        var b: Int
        var c: Char
        
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
            'a' -> {
                // Absolute Value
                stack.push(stack.pop().absoluteValue)
            'm' -> {
                // Minus
                // Negate top of stack
                stack.push(-stack.pop())
            }
            '*' -> {
                stack.push(stack.pop() * stack.pop())
            }
            '/' -> {
                b = stack.pop()
                a = stack.pop()
                stack.push(a / b)
            }
            'p' -> {
                // Power
                // compute a^b
                b = stack.pop()
                a = stack.pop()
                stack.push(
                        a.toDouble().pow(b.toDouble()).toInt()
                )
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
            '$' -> {
                stack.pop()
            }
            // backslash
            '\\' -> {
                // swap top two stack items
                b = stack.pop()
                a = stack.pop()
                stack.push(b)
                stack.push(a)
            }
            'D' -> {
                // dump stack
                stack.clear()
            }
            ')' -> {
                b = stack.pop()
                a = stack.pop()
                stack.push((a > b).toInt())
            }
            '(' -> {
                b = stack.pop()
                a = stack.pop()
                stack.push((a < b).toInt())
            }
            '=' -> {
                b = stack.pop()
                a = stack.pop()
                stack.push((a == b).toInt())
            }
            '!' -> {
                stack.push((stack.pop() == 0).toInt())
            }
            '_' -> {
                dirY = 0
                dirX = if(stack.pop() == 0) 1 else -1
            }
            '|' -> {
                dirX = 0
                dirY = if(stack.pop() == 0) 1 else -1
            }
            'k' -> {
                a = stack.pop()
                if(a < 1) return
                move(board)
                c = board[posY][posX]
                repeat(a) {
                    doCell(c, board)
                }
            }
            'L' -> {
                // 90deg left
            }
            'R' -> {
                // 90deg right
            }
            'l' -> {
                // 45deg left
            }
            'r' -> {
                // 45deg right
            }
            'W' -> {
                // writewhile
                // pop and write char until 0
                do {
                    a = stack.pop()
                    if(a != 0) print(a.toChar())
                } while(a != 0)
            }
            'n' -> {
                // Maybe North
                // go North if pop is true
                if(stack.pop() != 0) {
                    dirX = 0
                    dirY = -1
                }
            }
            'e' -> {
                // Maybe East
                if(stack.pop() != 0) {
                    dirX = 1
                    dirY = 0
                }
            }
            's' -> {
                // Maybe South
                if(stack.pop() != 0) {
                    dirX = 0
                    dirY = -1
                }
            }
            'w' -> {
                // Maybe West
                if(stack.pop() != 0) {
                    dirX = -1
                    dirY = 0
                }
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

/**
 * Helper extension to convert boolean to 1/0.
*/
fun Boolean.toInt() = if(this) 1 else 0
