package ajc2.ahead

import java.io.File

class Board(val filePath: String) {
    private val cells: Array<CharArray>
    val height: Int
    val width: Int
    
    init {
        val lines = File(filePath).readLines()
        height = lines.size
        width = lines.map { it.length }.max()
        cells = lines.map {
            it.padEnd(width).toCharArray()
        }.toTypedArray()
    }

    operator fun get(index: Int): CharArray = cells[index]

    fun debug() {
        for(row in cells) {
            for(cell in row) {
                print(cell)
            }
            println()
        }
    }
    
}
