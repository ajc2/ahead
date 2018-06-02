package ajc2.ahead

import java.util.Random
import kotlin.math.*

class Head {
    private val rand = Random()
    val stack = Stack()
    private val stdin = System.`in`.reader()

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
        println("CURRENT: $cell")
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
        var a: Int
        var b: Int
        var c: Char
        
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
                // multiply top two stack items
                stack.push(stack.pop() * stack.pop())
            }
            '/' -> {
                // divide top two stack items
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
                b = stack.pop()
                a = stack.pop()
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
                b = stack.pop()
                a = stack.pop()
                stack.push((a > b).toInt())
            }
            '(' -> {
                // Less Than
                b = stack.pop()
                a = stack.pop()
                stack.push((a < b).toInt())
            }
            '=' -> {
                // Equal
                b = stack.pop()
                a = stack.pop()
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
                a = stack.pop()
                move(board)
                if(a > 0) {
                    c = board[posY][posX]
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

/**
 * Enum class to represent the current state of the head.
 */
enum class HeadMode {
    NORMAL,
    NUMBER,
    STRING,
    FLOATING,
    DEAD
}

/**
 * Enum class to represent the head's current traveling direction.
 * Contains facilities for changing direction as well.
 */
enum class HeadDirection(val x: Int, val y: Int) {
    EAST      ( 1,  0),
    SOUTHEAST ( 1,  1),
    SOUTH     ( 0,  1),
    SOUTHWEST (-1,  1),
    WEST      (-1,  0),
    NORTHWEST (-1, -1),
    NORTH     ( 0, -1),
    NORTHEAST ( 1, -1);

    /**
     * "Turn" this enum value by o steps.
     * 1 step = 1 enum = 45 degrees clockwise/right.
     * Negative values turn counter-clockwise.
     */
    fun turnBy(o: Int): HeadDirection {
        // Get all direction values, in CW order.
        val vals = HeadDirection.values()
        val count = vals.size

        // Find the new direction by "turning" o steps.
        val next = ((ordinal + o) % count + count) % count

        // Return the enum value representing this direction.
        return vals[next]
    }

    // Companion methods
    companion object {
        /**
         * Pick a random direction to travel.
         * If cardinal is true, only select from NESW.
         */
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
