package ajc2.ahead

import java.io.File

/**
 * Code board object
 */
class Board {
    private val cells: List<IntArray>
    val height: Int
    val width: Int
    
    constructor(file: File) {
        val lines = file.readLines()
        height = lines.size
        width = lines.map { it.length }.max()!!
        cells = lines.map {
            it.padEnd(width).map(Char::toInt).toIntArray()
        }
    }

    constructor(string: String) {
        val lines = string.lines()
        height = lines.size
        width = lines.map { it.length }.max()!!
        cells = lines.map {
            it.padEnd(width).map(Char::toInt).toIntArray()
        }
    }

    operator fun get(index: Int) = cells[index]

    fun isValid(x: Int, y: Int, checkWalls: Boolean = true): Boolean {
        var valid = x in 0..width-1 && y in 0..height-1
        if(checkWalls) {
            valid = valid && !cells[y][x].isWall()
        }
        return valid
    }

    override fun toString(): String {
        return cells.map {
            it.joinToString("")
        }.joinToString("\n")
    }
}
