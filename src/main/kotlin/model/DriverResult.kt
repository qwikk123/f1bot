package model

data class DriverResult(
    val driverId: String,
    val laps: Int,
    val gridStart: Int,
    val status: String,
    val points: Double,
    val pos: Int
)
