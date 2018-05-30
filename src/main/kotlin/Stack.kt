package ajc2.ahead

import java.util.ArrayDeque

/**
 * Simple wrapper around ArrayDeque<Int>.
 */
class Stack {
    private val deque = ArrayDeque<Int>()
    val size get() = deque.size

    fun push(item: Int) = deque.push(item)
    fun push(item: Char) = deque.push(item.toInt())
    fun pop() = deque.poll() ?: 0
    fun dup() = deque.push(deque.peek() ?: 0)
    fun clear() = deque.clear()
}
