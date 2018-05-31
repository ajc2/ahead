package ajc2.ahead

import java.io.File

class Board(val filePath: String) {
    private val cells: Array<CharArray>
    val height: Int
    val width: Int
    
    init {
        val lines = File(filePath).readLines()
        height = lines.size
        width = lines.map { it.length }.max()!!
        cells = lines.map {
            it.padEnd(width).toCharArray()
        }.toTypedArray()
    }

    operator fun get(index: Int): CharArray = cells[index]

    fun inBounds(x: Int, y: Int): Boolean {
        return x in 0..width-1 && y in 0..height-1
    }

    override fun toString(): String {
        return cells.map {
            it.joinToString("")
        }.joinToString("\n")
    }
}
