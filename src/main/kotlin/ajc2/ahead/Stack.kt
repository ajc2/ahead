package ajc2.ahead

/**
 * Simple wrapper around MutableList<Int>.
 */
class Stack {
    private val deque = MutableList<Int>(0){0}
    val size get() = deque.size

    fun push(item: Int) {
        deque.add(item)
    }

    fun push(item: Char) {
        deque.add(item.toInt())
    }

    fun pop(): Int {
        return if(deque.size == 0) {
            0
        } else {
            deque.removeAt(deque.lastIndex)
        }
    }

    fun dup() {
        deque.add(
            if(deque.size == 0) 0 else deque.last()
        )
    }

    fun clear() {
        deque.clear()
    }
    
    fun shiftLeft() {
        if(size > 1) deque.add(deque.removeAt(0))
    }

    fun shiftRight() {
        if(size > 1) deque.add(0, deque.removeAt(deque.lastIndex))
    }

    fun reverse() {
        deque.reverse()
    }
}
