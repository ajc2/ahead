package ajc2.ahead

import java.util.Random

/**
 * Enum class to represent the head's current traveling direction.
 * Contains facilities for changing direction as well.
 */
enum class HeadDirection(val x: Int, val y: Int) {
    EAST      ( 1,  0),
    SOUTHEAST ( 1,  1),
    SOUTH     ( 0,  1),
    SOUTHWEST (-1,  1),
    WEST      (-1,  0),
    NORTHWEST (-1, -1),
    NORTH     ( 0, -1),
    NORTHEAST ( 1, -1);

    /**
     * "Turn" this enum value by o steps.
     * 1 step = 1 enum = 45 degrees clockwise/right.
     * Negative values turn counter-clockwise.
     */
    fun turnBy(o: Int): HeadDirection {
        // Get all direction values, in CW order.
        val vals = HeadDirection.values()
        val count = vals.size

        // Find the new direction by "turning" o steps.
        val next = ((ordinal + o) % count + count) % count

        // Return the enum value representing this direction.
        return vals[next]
    }

    // Companion methods
    companion object {
        /**
         * Pick a random direction to travel.
         * If cardinal is true, only select from NESW.
         */
        fun random(cardinal: Boolean = false): HeadDirection {
            return if(cardinal) {
                values()[Random().nextInt(4) * 2]
            } else {
                values()[Random().nextInt(8)]
            }
        }
    }
}
