package ajc2.ahead

import kotlin.random.Random
import kotlin.math.*

// charcodes
private const val DQUOTE =  34  // "
private const val TILDE  = 126  // ~
private const val ZERO   =  48  // 0
private const val NINE   =  57  // 9
private const val HASH   =  35  // #

/**
* Program execution and behavior.
* The head is the Ahead equivalent of the
* instruction pointer or program counter.
*/
class Head(private val io: IOWrapper) {
    private val rand = Random.Default

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
        val cell = board[posX,posY]
        when(mode) {
            HeadMode.NORMAL -> {
                // normal mode of execution
                doCell(cell, board)
            }
            HeadMode.STRING -> {
                // pushes chars on board to stack
                if(cell == DQUOTE) {
                    // exit stringmode when encountering a doublequote
                    mode = HeadMode.NORMAL
                }
		        else {
                    stack.push(cell)
                }
            }
            HeadMode.NUMBER -> {
                // reads number values onto the stack
                if(cell !in ZERO..NINE) {
                    // exit numbermode when cell is not a digit
                    mode = HeadMode.NORMAL
                    doCell(cell, board)
                }
                else {
                    stack.push(stack.pop() * 10 + cell.toDigit())
                }
            }
            HeadMode.FLOATING -> {
                // skips all instructions until next ~
                if(cell == TILDE) {
                    mode = HeadMode.NORMAL
                }
            }
            HeadMode.DEAD -> {
                // terminated; does nothing
                return
            }
        }

        // move
        move(board, checkWalls = mode != HeadMode.STRING)
    }

    /**
    * Interpret a cell as an instruction.
    */
    private fun doCell(cell: Int, board: Board) {
        if(cell.isWall()) {
            throw Exception("Head inside wall")
        }
        
        when(cell.toChar()) {
            in '0'..'9' -> {
                // Enter numbermode when a digit is encountered
                stack.push(cell.toDigit())
                mode = HeadMode.NUMBER
            }
            '"' -> {
                // enter stringmode
                mode = HeadMode.STRING
                stack.push(0)
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
                io.printChar(stack.pop())
            }
            'O' -> {
                // Output Number
                io.printNumber(stack.pop())
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
            'd' -> {
                // Stack Depth
                stack.push(stack.size)
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
                    val c = board[posX,posY]
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
            'h' -> {
                // Set Direction
                direction = HeadDirection.facing(stack.pop())
            }
            'H' -> {
                // Turn By
                direction = direction.turnBy(stack.pop())
            }
            'A' -> {
                // Push direction
                stack.push(direction.ordinal)
            }
            'W' -> {
                // writewhile
                // pop and write char until 0
                do {
                    val a = stack.pop()
                    if(a > 0) io.printChar(a)
                } while(a > 0)
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
                move(board, checkWalls = false)
                stack.push(board[posX,posY].toInt())
            }
            ',' -> {
                // Put down
                // Pop stack and place in next cell
                move(board, checkWalls = false)
                board[posX,posY] = stack.pop()
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
                // Input Number
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
                    a < b -> b downTo a
                    a > b -> b..a
                    else -> a..a
                }
                stack.addAll(range)
            }
            '[' -> {
                val b = stack.pop()
                val a = stack.pop()
                stack.push(min(a, b))
            }
            ']' -> {
                val b = stack.pop()
                val a = stack.pop()
                stack.push(max(a, b))
            }
            'K' -> {
                move(board)
                while(stack.size > 1) {
                    val c = board[posX,posY]
                    doCell(c, board)
                }
            }
            'u' -> {
                // Reverse stack
                stack.reverse()
            }
            'U' -> {
                // Reverse up to first 0
                val i = stack.indexOf(0)
                if(i == -1) {
                    stack.reverse()
                }
                else {
                    stack.subList(stack.size - i, stack.size)
                        .reverse()
                }
            }
            '{' -> {
                stack.shiftLeft()
            }
            '}' -> {
                stack.shiftRight()
            }
            'C' -> {
                // Increment register
                register++
            }
            'c' -> {
                // Decrement register
                register--
            }
            'S' -> {
                // Slurp input
                var i: Int? = 0
                do {
                    stack.push(i!!)
                    i = io.getChar()
                } while(i != null)
            }
            'M' -> {
                // Pop char and evaluate
                doCell(stack.pop(), board)
            }
            'f' -> {
                // Find on stack
                val v = stack.pop()
                stack.push(stack.indexOf(v))
            }
            'F' -> {
                // Find in string
                val s = stack.popString()
                val v = stack.pop()
                if(v <= 0) {
                    stack.push(-1)
                }
                else {
                    stack.push(s.indexOf(v.toChar()))
                }
            }
        }
    }

    fun isDead(): Boolean {
        return mode == HeadMode.DEAD
    }

    /**
    * Generate debug info string for the head.
    */
    fun debugInfo(): String {
        return """
        STATE: $mode
        POS: ($posX, $posY)
        DIR: $direction
        REG: $register
        STACK: ${stack.toString()}
        """.trimIndent()
    }

    // Reflect the direction of the head.
    private fun reflect() {
        direction = direction.turnBy(4)
    }

    // Move the head to its next position.
    private fun move(board: Board, checkWalls: Boolean = true) {
        var nextX = posX + direction.x
        var nextY = posY + direction.y

        // Try moving backwards if this is not a valid space
        if(!board.isValid(nextX, nextY, checkWalls)) {
            reflect()
            nextX = posX + direction.x
            nextY = posY + direction.y
            // Stay put if this is not valid
            if(!board.isValid(nextX, nextY, checkWalls)) return
        }
        
        posX = nextX
        posY = nextY
    }
}
