package ajc2.ahead

import java.util.Random
import kotlin.math.*

// TODO: Good I/O.
class Head {
    private val rand = Random()
    val stack = Stack()

    var register = 0
    var posX = 0
    var posY = 0
    var direction = HeadDirection.EAST
    var mode = HeadMode.NORMAL

    private val stdin = System.`in`.reader()
    /**
    * Do one step of execution on the board.
    */
    fun step(board: Board) {
        // interpret current cell
        val cell = board[posY][posX]
        when(mode) {
            HeadMode.NORMAL -> {
                doCell(cell, board)
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
            in '0'..'9' -> {
                stack.push(cell.toDigit())
                mode = HeadMode.NUMBER
            }
            '\u0022' -> {
                mode = HeadMode.STRING
            }
            '~' -> {
                mode = HeadMode.FLOATING
            }
            '@' -> {
                mode = HeadMode.DEAD
            }
            '^' -> {
                direction = HeadDirection.NORTH
            }
            'v' -> {
                direction = HeadDirection.SOUTH
            }
            '<' -> {
                direction = HeadDirection.WEST
            }
            '>' -> {
                direction = HeadDirection.EAST
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
            }
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
            '%' -> {
                // Modulo
                b = stack.pop()
                a = stack.pop()
                stack.push(a % b)
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
                direction = if(stack.pop() == 0) {
                    HeadDirection.EAST
                } else {
                    HeadDirection.WEST
                }
            }
            '|' -> {
                direction = if(stack.pop() == 0) {
                    HeadDirection.SOUTH
                } else {
                    HeadDirection.NORTH
                }
            }
            'k' -> {
                a = stack.pop()
                if(a > 0) {
                    move(board)
                    c = board[posY][posX]
                    repeat(a) {
                        doCell(c, board)
                    }
                }
            }
            'L' -> {
                // 90deg left
                direction = direction.turnBy(-2)
            }
            'R' -> {
                // 90deg right
                direction = direction.turnBy(2)
            }
            'l' -> {
                // 45deg left
                direction = direction.turnBy(-1)
            }
            'r' -> {
                // 45deg right
                direction = direction.turnBy(1)
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
                    direction = HeadDirection.NORTH
                }
            }
            'e' -> {
                // Maybe East
                if(stack.pop() != 0) {
                    direction = HeadDirection.EAST
                }
            }
            's' -> {
                // Maybe South
                if(stack.pop() != 0) {
                    direction = HeadDirection.SOUTH
                }
            }
            'w' -> {
                // Maybe West
                if(stack.pop() != 0) {
                    direction = HeadDirection.WEST
                }
            }
            'b' -> {
                // Bounce
                reflect()
            }
            'x' -> {
                // Random
                direction = HeadDirection.random()
            }
            'X' -> {
                // Cardinal Random
                direction = HeadDirection.random(cardinal = true)
            }
            'N' -> {
                stack.push('\n'.toInt())
            }
            '\'' -> {
                move(board)
                stack.push(board[posY][posX].toInt())
            }
            'i' -> {
                stack.push(stdin.read())
            }
        }
    }

    fun isDead(): Boolean {
        return mode == HeadMode.DEAD
    }

    // Reflect the direction of the head.
    private fun reflect() {
        direction = direction.turnBy(4)
    }

    // Move the head to its next position.
    private fun move(board: Board) {
        val dirX = direction.x
        val dirY = direction.y
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

enum class HeadDirection(val x: Int, val y: Int) {
    EAST      ( 1,  0),
    SOUTHEAST ( 1,  1),
    SOUTH     ( 0,  1),
    SOUTHWEST (-1,  1),
    WEST      (-1,  0),
    NORTHWEST (-1, -1),
    NORTH     ( 0, -1),
    NORTHEAST ( 1, -1);

    fun turnBy(o: Int): HeadDirection {
        val vals = HeadDirection.values()
        val count = vals.size
        var next = (ordinal + o % count) % count
        if(next < 0) next += count
        return vals[next]
    }

    companion object {
        fun random(cardinal: Boolean = false): HeadDirection {
            return if(cardinal) {
                values()[Random().nextInt(4) * 2]
            } else {
                values()[Random().nextInt(8)]
            }
        }
    }
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
