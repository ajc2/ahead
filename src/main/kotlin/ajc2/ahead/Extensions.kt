package ajc2.ahead

// Some helper extensions.

/**
 * Helper extension to convert a digit character
 * to its Int value.
 */
fun Char.toDigit() = this.toInt() - 48

/**
 * Helper extension to convert boolean to 1/0.
 */
fun Boolean.toInt() = if(this) 1 else 0
