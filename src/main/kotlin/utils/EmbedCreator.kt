package utils

import model.Constructor
import model.Driver
import model.DriverResult
import model.Race
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.ceil
import kotlin.math.min

/**
 * Class containing utility functions for creating embed messages.
 */
object EmbedCreator {
    private val color = Color.RED
    private const val THUMBNAIL_URL = "https://i.imgur.com/7wyu3ng.png"
    private val df: DecimalFormat = DecimalFormat("#.#", DecimalFormatSymbols(Locale.US))
    fun createRace(r: Race): EmbedBuilder {
        return createRace(r, "")
    }

    fun createUpcoming(r: Race): EmbedBuilder {
        return createRace(r, "This weekend: ")
    }

    /**
     * Creates an EmbedBuilder for a race message.
     * @param r the race to create a message for.
     * @param extraTitle extra prefix for the embeds title.
     * @return an EmbedBuilder containing the race.
     */
    private fun createRace(r: Race, extraTitle: String): EmbedBuilder {
        val eb = EmbedBuilder()
        setTheme(eb)
        eb.setTitle("$extraTitle#${r.round} ${getCountryCodeEmoji(r.getCountryCode())} ${r.name}")
        eb.addField("Race: ", "${r.raceTimestamp}\n${r.raceRelativeTimestamp}", true)
        if (r.hasSprint()) eb.addField("Sprint: ", r.sprintTimestamp, true)
        eb.addField("Qualifying: ", r.qualifyingTimestamp, true)
        eb.addField("Circuit: ", r.circuitName, false)
        eb.setImage("attachment://circuitImage.png")
        return eb
    }

    /**
     * Creates an EmbedBuilder for the driver profile message.
     * @param driver the driver to create a profile for
     * @return an EmbedBuilder with the driver profile
     */
    fun createDriverProfile(driver: Driver): EmbedBuilder {
        val eb = EmbedBuilder()
        setTheme(eb)
        eb.setTitle("#${driver.permanentNumber} ${getCountryCodeEmoji(driver.isoCode)}${driver.name}")
        eb.addField("Team:", driver.constructorName, false)
        eb.addField("Position", "#${driver.pos}", true)
        eb.addField("Wins", java.lang.String.valueOf(driver.wins), true)
        eb.addField("Points", df.format(driver.points), true)
        eb.setImage("attachment://driverImage.png")
        return eb
    }

    /**
     * Creates an EmbedBuilder for the driver standings message.
     * @param driverMap Map of the drivers for this F1 season.
     * @param page The page to create standings for.
     * @return an EmbedBuilder with the driver standings.
     */
    fun createDriverStandings(driverMap: HashMap<String, Driver>, page: Int, pageSize: Int): EmbedBuilder {
        val driverStandings: List<Driver> = driverMap.values.sortedBy { driver -> driver.pos }
        val eb = EmbedBuilder()
        setTheme(eb)

        eb.setTitle("Driver Standings")
        eb.addField("Driver:", "", true)
        eb.addField("Team:", "", true)
        eb.addField("Points:", "", true)

        val start = pageSize * page
        for (driver in driverStandings.subList(
            start,
            min((start + pageSize), driverStandings.size)
        )) {
            eb.addField("#${driver.pos} ${getCountryCodeEmoji(driver.isoCode)}${driver.name}", "", true)
            eb.addField(driver.constructorName, "", true)
            eb.addField(df.format(driver.points), "", true)
        }

        val maxPage = ceil(driverStandings.size.toDouble() / pageSize).toInt()
        eb.setFooter("${(page + 1)}/$maxPage")

        return eb
    }

    /**
     * Creates an EmbedBuilder for the constructor standings message.
     * @param constructorStandings A list with the constructors from this F1 season in sorted order.
     * @return an EmbedBuilder containing the constructor standings.
     */
    fun createConstructorStandings(constructorStandings: List<Constructor>): EmbedBuilder {
        val eb = EmbedBuilder()
        setTheme(eb)
        eb.setTitle("Constructor Standings")

        for (constructor in constructorStandings) {
            eb.addField(
                "#${constructor.pos} ${getCountryCodeEmoji(constructor.isoCode)} ${constructor.name}",
                "Points: ${df.format(constructor.points)}",
                true
            )
        }
        return eb
    }

    /**
     * Creates an EmbedBuilder for a race result page.
     * It creates a table format inside a discord codeblock.
     * @param r the race to create a result embed for.
     * @param driverMap a list of drivers in the current F1 season.
     * @param page the page to create the embed for.
     * @return an EmbedBuilder containing the result page.
     */
    fun createRaceResult(r: Race, driverMap: HashMap<String, Driver>, page: Int, pageSize: Int): EmbedBuilder {
        val eb = EmbedBuilder()
        setTheme(eb)

        eb.setTitle("#${r.round} ${getCountryCodeEmoji(r.getCountryCode())}${r.name}")
        val start = pageSize * page
        val format = "%-4s  %-16s  %-7s  %s"
        var codeBlockText = "```${String.format(format, "Pos:", "Driver:", "Points:", "Status:")}\n"

        val raceResultList: List<DriverResult> = r.raceResult!!.driverResultList
        for (driverResult in raceResultList.subList(
            start, min((start + pageSize), raceResultList.size)
        )) {
            val d: Driver = driverMap[driverResult.driverId]!!
            codeBlockText += (String.format(
                format,
                "#" + driverResult.pos,          //Pos
                d.name,                          //Driver
                df.format(driverResult.points),  //Points
                driverResult.status)             //Status
                    + "\n")
        }
        codeBlockText += "```"
        eb.addField("Result", codeBlockText, true)

        val maxPage = ceil(raceResultList.size.toDouble() / pageSize).toInt()
        eb.setFooter("${(page + 1)}/$maxPage")

        return eb
    }

    /**
     * Creates an EmbedBuilder for the season calendar.
     * @param raceList list of races in the current F1 season.
     * @return an EmbedBuilder containing the season calendar
     */
    fun createCalendar(raceList: List<Race>, nextRace:Race ): EmbedBuilder {
        val eb = EmbedBuilder()
        setTheme(eb)
        eb.setTitle("Calendar")

        for (r in raceList) {
            var name: String
            var value: String

            if (r.round == nextRace.round) {
                name = ">NEXT RACE<\n#${r.round} ${getCountryCodeEmoji(r.getCountryCode())} ${r.name}"
                value = r.raceTimestampDateOnly
            }
            else {
                name = ""
                value = "#${r.round} ${getCountryCodeEmoji(r.getCountryCode())} ${r.name}\n${r.raceTimestampDateOnly}"
            }
            eb.addField(name, value, true)
        }
        return eb
    }

    /**
     * Function to convert a country iso code into a discord flag emoji.
     * :flag_ISOCODE:
     * @param isoCode the iso country code to make an emoji for.
     * @return a string containing a discord flag emoji.
     */
    private fun getCountryCodeEmoji(isoCode: String): String {
        return ":flag_$isoCode:"
    }

    /**
     * Sets the general theme of an EmbedBuilder
     * @param eb EmbedBuilder to set the theme for.
     */
    private fun setTheme(eb: EmbedBuilder) {
        eb.setThumbnail(THUMBNAIL_URL)
        eb.setColor(color)
    }
}
