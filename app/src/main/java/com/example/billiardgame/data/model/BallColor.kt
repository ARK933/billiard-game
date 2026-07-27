package com.example.billiardgame.data.model

enum class BallColor(val colorInt: Int) {
    CUE(0xFFF5F5F5.toInt()),              // White
    SOLID_YELLOW(0xFFFFFF00.toInt()),     // 1
    SOLID_BLUE(0xFF0000FF.toInt()),       // 2
    SOLID_RED(0xFFFF0000.toInt()),        // 3
    SOLID_PURPLE(0xFF800080.toInt()),     // 4
    SOLID_ORANGE(0xFFFF8C00.toInt()),     // 5
    SOLID_GREEN(0xFF008000.toInt()),      // 6
    SOLID_MAROON(0xFF800000.toInt()),     // 7
    EIGHT_BLACK(0xFF000000.toInt()),      // 8
    STRIPE_YELLOW(0xFFFFFF00.toInt()),    // 9
    STRIPE_BLUE(0xFF0000FF.toInt()),      // 10
    STRIPE_RED(0xFFFF0000.toInt()),       // 11
    STRIPE_PURPLE(0xFF800080.toInt()),    // 12
    STRIPE_ORANGE(0xFFFF8C00.toInt()),    // 13
    STRIPE_GREEN(0xFF008000.toInt()),     // 14
    STRIPE_MAROON(0xFF800000.toInt());    // 15  <-- 注意这里改成了分号 ';'

    companion object {
        fun fromNumber(num: Int): BallColor = when (num) {
            0 -> CUE
            in 1..7 -> values()[num]
            8 -> EIGHT_BLACK
            in 9..15 -> values()[num]
            else -> CUE
        }
    }
}
