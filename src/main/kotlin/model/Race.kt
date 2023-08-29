package model

import net.dv8tion.jda.api.utils.TimeFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Class that represents an F1 race.
 */
class Race
/**
 * Creates an instance of Race representing an F1 Race
 *
 * @param name         name of the race
 * @param circuitName  name of the circuit the race is using
 * @param raceInstant  the datetime for when the race starts
 * @param qualiInstant the datetime for when the qualifying starts
 * @param round        the race position in the calendar
 * @param countryCode  The name of the country where this race takes place
 */(
    val name: String,
    val circuitName: String,
    val raceInstant: Instant,
    private val qualiInstant: Instant,
    val round: Int,
    private val countryCode: String
) {
    private var sprintInstant: Instant? = null
    private var raceResult: RaceResult? = null
    fun setRaceResult(raceResult: RaceResult?) {
        this.raceResult = raceResult
    }

    fun getRaceResult(): RaceResult? {
        return raceResult
    }

    fun hasRaceResult(): Boolean {
        return raceResult != null
    }

    fun getCountryCode(): String {
        return countryCode.lowercase(Locale.getDefault())
    }

    val raceRelativeTimestamp: String
        get() = TimeFormat.RELATIVE.atInstant(raceInstant).toString()
    val raceTimestampDateOnly: String
        get() = TimeFormat.DATE_LONG.atInstant(raceInstant).toString()
    val raceTimestamp: String
        get() = TimeFormat.DATE_TIME_SHORT.atInstant(raceInstant).toString()
    val qualifyingTimestamp: String
        get() = TimeFormat.DATE_TIME_SHORT.atInstant(qualiInstant).toString()
    val sprintTimestamp: String
        get() = TimeFormat.DATE_TIME_SHORT.atInstant(sprintInstant!!).toString()

    fun setSprint(sprintInstant: Instant?) {
        this.sprintInstant = sprintInstant
    }

    fun hasSprint(): Boolean {
        return sprintInstant != null
    }

    val imagePath: String
        get() = "/circuitimages/" + circuitName.replace(" ".toRegex(), "") + ".png"
    val upcomingDate: Instant
        get() = raceInstant.minus(2, ChronoUnit.DAYS)
}
