package ajc2.ahead

import java.util.ArrayDeque

class Head {
    val direction = HeadDirection.EAST
    val stack = ArrayDeque<Char>
    val register = '\u0000'
}

enum class HeadDirection(val x: Int, val y: Int) {
    EAST      ( 1,  0),
    SOUTHEAST ( 1,  1),
    SOUTH     ( 0,  1),
    SOUTHWEST (-1,  1),
    WEST      (-1,  0),
    NORTHWEST (-1, -1),
    NORTH     ( 0, -1),
    NORTHEAST ( 1, -1)
}

