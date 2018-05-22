package ajc2.ahead

import java.io.File

class Board(val filePath: String) {
    init {
        val lines = File(filePath).readLines()
        val height = lines.size
        val width = lines.map { it.length }.max()
        val cells = Array(height){IntArray(width)}
    }

    operator fun get(index: Int): Int = cells[index]
}
