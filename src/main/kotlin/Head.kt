package ajc2.ahead

import java.util.Random
import kotlin.math.*

/**
 * Program execution and behavior.
 * The head is the Ahead equivalent of the
 * instruction pointer or program counter.
 */
class Head {
    private val rand = Random()
    private val io = IOWrapper()

    val stack = Stack()
    var register = 0
    var posX = 0
    var posY = 0
    var direction = HeadDirection.EAST
    var mode = HeadMode.NORMAL
    
    /**
     * Do one step of execution on the board.
     */
    fun step(board: Board) {
        // interpret current cell
        val cell = board[posY][posX]
        when(mode) {
            HeadMode.NORMAL -> {
                // normal mode of execution
                doCell(cell, board)
            }
            HeadMode.STRING -> {
                // pushes chars on board to stack
                if(cell == '\u0022') {
                    // exit stringmode when encountering a doublequote
                    mode = HeadMode.NORMAL
                } else {
                    stack.push(cell.toInt())
                }
            }
            HeadMode.NUMBER -> {
                // reads number values onto the stack
                if(cell !in '0'..'9') {
                    // exit numbermode when cell is not a digit
                    mode = HeadMode.NORMAL
                    doCell(cell, board)
                } else {
                    stack.push(stack.pop() * 10 + cell.toDigit())
                }
            }
            HeadMode.FLOATING -> {
                // skips all instructions until next ~
                if(cell == '~') {
                    mode = HeadMode.NORMAL
                }
            }
            HeadMode.DEAD -> {
                // terminated; does nothing
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
        //println("DO: $cell")
        
        when(cell) {
            in '0'..'9' -> {
                // Enter numbermode when a digit is encountered
                stack.push(cell.toDigit())
                mode = HeadMode.NUMBER
            }
            '\u0022' -> {
                // Doublequote
                // enter stringmode
                mode = HeadMode.STRING
            }
            '~' -> {
                // enter floating mode
                mode = HeadMode.FLOATING
            }
            '@' -> {
                // Die
                // stop head
                mode = HeadMode.DEAD
            }
            '^' -> {
                // Go North
                direction = HeadDirection.NORTH
            }
            'v' -> {
                // Go South
                direction = HeadDirection.SOUTH
            }
            '<' -> {
                // Go West
                direction = HeadDirection.WEST
            }
            '>' -> {
                // Go East
                direction = HeadDirection.EAST
            }
            '&' -> {
                // Store
                // pop stack and put value in register for later
                register = stack.pop()
            }
            't' -> {
                // Think
                // copy register value and push to stack
                stack.push(register)
            }
            '+' -> {
                // Add top two stack items
                stack.push(stack.pop() + stack.pop())
            }
            '-' -> {
                // subtract top two stack items
                val b = stack.pop()
                val a = stack.pop()
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
                // multiply top two stack items
                stack.push(stack.pop() * stack.pop())
            }
            '/' -> {
                // divide top two stack items
                val b = stack.pop()
                val a = stack.pop()
                stack.push(a / b)
            }
            '%' -> {
                // Remainder
                // truncated division integer modulo
                val b = stack.pop()
                val a = stack.pop()
                stack.push(a % b)
            }
            ';' -> {
                // Modulo
                // floored division integer modulo
                val b = stack.pop()
                val a = stack.pop()
                stack.push((a - b.toDouble() * floor(a.toDouble() / b)).toInt())
            }
            'p' -> {
                // Power
                // compute a^b
                val b = stack.pop()
                val a = stack.pop()
                stack.push(
                        a.toDouble().pow(b.toDouble()).toInt()
                )
            }
            'o' -> {
                // Output Char
                print(stack.pop().toChar())
            }
            'O' -> {
                // Output Number
                print(stack.pop())
            }
            '?' -> {
                // Maybe Reflect
                if(stack.pop() != 0) reflect()
            }
            ':' -> {
                // Dup
                // Copy top stack item
                stack.dup()
            }
            'j' -> {
                // Jump
                // Move ahead an extra cell
                move(board)
            }
            '$' -> {
                // Pop
                // Delete the top stack item
                stack.pop()
            }
            // backslash
            '\\' -> {
                // swap top two stack items
                val b = stack.pop()
                val a = stack.pop()
                stack.push(b)
                stack.push(a)
            }
            'D' -> {
                // Dump
                // empty stack
                stack.clear()
            }
            ')' -> {
                // Greater Than
                val b = stack.pop()
                val a = stack.pop()
                stack.push((a > b).toInt())
            }
            '(' -> {
                // Less Than
                val b = stack.pop()
                val a = stack.pop()
                stack.push((a < b).toInt())
            }
            '=' -> {
                // Equal
                val b = stack.pop()
                val a = stack.pop()
                stack.push((a == b).toInt())
            }
            '!' -> {
                // Logic Not
                stack.push((stack.pop() == 0).toInt())
            }
            '_' -> {
                // East/West If
                // Go East if popped value is 0, else go West
                direction = if(stack.pop() == 0) {
                    HeadDirection.EAST
                } else {
                    HeadDirection.WEST
                }
            }
            '|' -> {
                // North/South If
                // Go North if popped value is 0, else go South
                direction = if(stack.pop() == 0) {
                    HeadDirection.SOUTH
                } else {
                    HeadDirection.NORTH
                }
            }
            'k' -> {
                // Iterate
                // Do next cell N times
                // where N is the top of stack.
                // If N is less than 1, skip next cell
                val a = stack.pop()
                move(board)
                if(a > 0) {
                    val c = board[posY][posX]
                    repeat(a) {
                        doCell(c, board)
                    }
                }
            }
            'L' -> {
                // 90deg left turn
                direction = direction.turnBy(-2)
            }
            'R' -> {
                // 90deg right turn
                direction = direction.turnBy(2)
            }
            'l' -> {
                // 45deg left turn
                direction = direction.turnBy(-1)
            }
            'r' -> {
                // 45deg right turn
                direction = direction.turnBy(1)
            }
            'W' -> {
                // writewhile
                // pop and write char until 0
                do {
                    val a = stack.pop()
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
                // Push Newline
                stack.push('\n'.toInt())
            }
            '\'' -> {
                // Pick Up
                // Push next cell to stack and skip
                move(board)
                stack.push(board[posY][posX].toInt())
            }
            'i' -> {
                // Input Char
                val i: Int? = io.getChar()
                if(i == null) {
                    reflect()
                } else {
                    stack.push(i)
                }
            }
            'I' -> {
                // Input Char
                val i: Int? = io.getNumber()
                if(i == null) {
                    reflect()
                } else {
                    stack.push(i)
                }
            }
            'E' -> {
                // Expand Range
                val b = stack.pop()
                val a = stack.pop()
                val range = when {
                    a < b -> a..b
                    a > b -> a downTo b
                    else -> a..a
                }
                for(i in range) {
                    stack.push(i)
                }
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
        var nextX = posX + direction.x
        var nextY = posY + direction.y

        // Try moving backwards if this is not a valid space
        if(!board.inBounds(nextX, nextY)) {
            reflect()
            nextX = posX + direction.x
            nextY = posY + direction.y
            // Stay put if this is not valid
            if(!board.inBounds(nextX, nextY)) return
        }
        
        posX = nextX
        posY = nextY
    }
}
