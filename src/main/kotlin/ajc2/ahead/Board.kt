package ajc2.ahead

import java.io.File

/**
 * Code board object
 */
class Board(val src: String) {
    private val cells: IntArray
    val comment: String
    val height: Int
    val width: Int

    init {
        val lines = src.lines()
        width = lines.map { it.length }.max()!!
        comment = lines.takeWhile { it.startsWith("#") }
            .map { it.drop(1) }
            .joinToString("\n")
        cells = lines.dropWhile { it.startsWith("#") }
            .flatMap { it.padEnd(width).map(Char::toInt) }
            .toIntArray()
        height = cells.size / width
    }

    operator fun get(index: Int) = cells[index]
    operator fun get(x: Int, y: Int) = cells[y*width+x]

    operator fun set(index: Int, value: Int) {
        cells[index] = value
    }
    operator fun set(x: Int, y: Int, value: Int) {
        cells[y*width+x] = value
    }

    fun isValid(x: Int, y: Int, checkWalls: Boolean = true): Boolean {
        var valid = x in 0..width-1 && y in 0..height-1
        if(checkWalls) {
            valid = valid && !cells[y*width+x].isWall()
        }
        return valid
    }

    override fun toString(): String {
        return cells.asIterable().chunked(width) {
            it.map {
                if(it < 0) {
                    '\uFFFD'
                }
                else {
                    it.toChar()
                }
            }.joinToString("")
        }.joinToString("\n")
    }
}
