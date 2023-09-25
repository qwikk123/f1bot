package model

import net.dv8tion.jda.api.utils.TimeFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Class that represents an F1 race.
 */
class Race(
    val name: String,
    val circuitName: String,
    val raceInstant: Instant,
    qualiInstant: Instant,
    val round: Int,
    private val countryCode: String
) {
    var sprintInstant: Instant? = null
        set(value) {
            field = value
            sprintTimestamp = TimeFormat.DATE_TIME_SHORT.atInstant(value!!).toString()
        }
    var raceResult: RaceResult? = null

    val raceRelativeTimestamp: String = TimeFormat.RELATIVE.atInstant(raceInstant).toString()
    val raceTimestampDateOnly: String = TimeFormat.DATE_LONG.atInstant(raceInstant).toString()
    val raceTimestamp: String = TimeFormat.DATE_TIME_SHORT.atInstant(raceInstant).toString()
    val qualifyingTimestamp: String = TimeFormat.DATE_TIME_SHORT.atInstant(qualiInstant).toString()
    lateinit var sprintTimestamp: String

    val imagePath: String = "/circuitimages/${circuitName.replace(" ".toRegex(), "")}.png"
    val upcomingDate: Instant = raceInstant.minus(2, ChronoUnit.DAYS)

    fun hasRaceResult(): Boolean = raceResult != null
    fun hasSprint(): Boolean = sprintInstant != null
    fun getCountryCode(): String = countryCode.lowercase(Locale.getDefault())
}
