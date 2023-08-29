package model

data class Driver (
    val pos: Int,
    val name: String,
    val constructorName:String,
    val points:Double,
    val wins: Int,
    val code: String,
    val nationality: String,
    val driverId: String,
    val permanentNumber: Int,
    val isoCode: String
)