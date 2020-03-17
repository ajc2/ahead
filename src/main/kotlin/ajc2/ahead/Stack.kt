package ajc2.ahead

/**
 * Simple wrapper around ArrayList<Int>.
 */
class Stack : ArrayList<Int>() {
    fun push(item: Int) {
        add(item)
    }

    fun push(item: Char) {
        add(item.toInt())
    }

    fun pop(): Int {
        return if(size == 0) {
            0
        } else {
            removeAt(lastIndex)
        }
    }

    fun popString(): String {
        val sb = StringBuilder()
        while(size > 0) {
            val c = removeAt(lastIndex)
            if(c > 0) {
                sb.append(c.toChar())
            }
            else {
                break
            }
        }
        return sb.toString()
    }

    fun dup() {
        add(
            if(size == 0) 0 else last()
        )
    }
    
    fun shiftLeft() {
        if(size > 1) add(removeAt(0))
    }

    fun shiftRight() {
        if(size > 1) add(0, removeAt(lastIndex))
    }

    override fun indexOf(element: Int) = lastIndex - super.indexOf(element)
}
