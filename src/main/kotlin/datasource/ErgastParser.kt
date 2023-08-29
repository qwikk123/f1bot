package datasource

import model.*
import org.json.JSONArray
import org.json.JSONObject
import sun.security.ec.point.ProjectivePoint.Mutable
import java.io.IOException
import java.time.Instant
import java.util.*

/**
 * Class for parsing data from the Ergast API for the f1bot's model classes and datasource.
 */
class ErgastParser {
    private val ergastDataRetriever: ErgastDataRetriever = ErgastDataRetriever()
    private val countryCodeMap: HashMap<String, String> = HashMap()
    private val nationalityCodeMap: HashMap<String, String>

    /**
     * Creates an instance of the ErgastParser and initializes an ErgastDataRetriever.
     */
    init {
        for (iso in Locale.getAvailableLocales()) {
            countryCodeMap[iso.displayCountry] = iso.country
        }
        countryCodeMap["UAE"] = "ae"
        countryCodeMap["USA"] = "us"
        countryCodeMap["UK"] = "gb"
        nationalityCodeMap = readNationalityCodeMapCSV()
    }

    /**
     * Parses information from an ErgastDataRetriever.
     * The data is parsed as a JSON object.
     * The method will not update anything unless validUpdate() returns true.
     * @param forceUpdate forces the update of data (this is mostly used for initial data retrieval on bot startup)
     * @return A list containing a Race instance for each race in the F1 season.
     */
    fun getF1RaceData(forceUpdate: Boolean): MutableList<Race>? {
        val URL = "https://ergast.com/api/f1/current.json"
        val validUpdate = ergastDataRetriever.validUpdate(URL)
        if (!forceUpdate && !validUpdate) {
            return null
        }
        val json: JSONObject = ergastDataRetriever.getJson(URL, validUpdate)
        val jArray: JSONArray = json.getJSONObject("MRData")
            .getJSONObject("RaceTable")
            .getJSONArray("Races")
        val raceList: MutableList<Race> = mutableListOf()
        for (i in 0..< jArray.length()) {
            val jRace: JSONObject = jArray.getJSONObject(i)
            val raceInstant = getInstant(jRace.getString("date"), jRace.getString("time"))
            val round: Int = jRace.getInt("round")
            val jQualifying: JSONObject = jRace.getJSONObject("Qualifying")
            val qualiInstant = getInstant(jQualifying.getString("date"), jQualifying.getString("time"))
            val circuit: JSONObject = jRace.getJSONObject("Circuit")
            val circuitName: String = circuit.getString("circuitName")
            val name: String = jRace.getString("raceName")
            val countryName: String = jRace.getJSONObject("Circuit").getJSONObject("Location").getString("country")
            val countryCode: String = countryCodeMap[countryName]!!
            val r = Race(name, circuitName, raceInstant, qualiInstant, round, countryCode)
            if (jRace.has("Sprint")) {
                val jSprint: JSONObject = jRace.getJSONObject("Sprint")
                r.setSprint(getInstant(jSprint.getString("date"), jSprint.getString("time")))
            }
            raceList.add(r)
        }
        return raceList
    }

    /**
     * Parses information from an ErgastDataRetriever.
     * The data is parsed as a JSON object.
     * The method will not update anything unless validUpdate() returns true.
     * @param forceUpdate forces the update of data (this is mostly used for initial data retrieval on bot startup)
     * @return A List containing RaceResults for each race in the F1 season
     */
    fun getRaceResults(forceUpdate: Boolean): MutableList<RaceResult>? {
        val raceResults: MutableList<RaceResult> = mutableListOf()
        val URL = "https://ergast.com/api/f1/current/results.json?limit=1000"
        val validUpdate = ergastDataRetriever.validUpdate(URL)
        if (!forceUpdate && !validUpdate) {
            return null
        }

        val json: JSONObject = ergastDataRetriever.getJson(URL, validUpdate)
        val jArray: JSONArray = json.getJSONObject("MRData")
            .getJSONObject("RaceTable")
            .getJSONArray("Races")

        for (i in 0..<jArray.length()) {
            val raceResult = RaceResult(mutableListOf())
            val race: JSONObject = jArray.getJSONObject(i)
            val resultArray: JSONArray = race.getJSONArray("Results")
            for (j in 0..<resultArray.length()) {
                val jsonDriver: JSONObject = resultArray.getJSONObject(j)
                val driverId: String = jsonDriver.getJSONObject("Driver").getString("driverId")
                val laps: Int = jsonDriver.getInt("laps")
                val gridStart: Int = jsonDriver.getInt("grid")
                val status: String = jsonDriver.getString("status")
                val points: Double = jsonDriver.getDouble("points")
                val pos: Int = jsonDriver.getInt("position")
                val rDriver = DriverResult(driverId, laps, gridStart, status, points, pos)
                raceResult.driverResultList.add(rDriver)
            }
            raceResults.add(raceResult)
        }
        return raceResults
    }

    /**
     * Parses information from an ErgastDataRetriever.
     * The data is parsed as a JSON object.
     * The method will not update anything unless validUpdate() returns true.
     * @param forceUpdate forces the update of data (this is mostly used for initial data retrieval on bot startup)
     * @return A HashMap containing all the drivers in the F1 season mapped to their driverId
     */
    fun getF1DriverStandingsData(forceUpdate: Boolean): HashMap<String, Driver>? {
        val URL = "https://ergast.com/api/f1/current/driverStandings.json"
        val validUpdate = ergastDataRetriever.validUpdate(URL)
        if (!forceUpdate && !validUpdate) {
            return null
        }
        val json: JSONObject = ergastDataRetriever.getJson(URL, validUpdate)
        val jArray: JSONArray = json.getJSONObject("MRData")
            .getJSONObject("StandingsTable")
            .getJSONArray("StandingsLists")
            .getJSONObject(0).getJSONArray("DriverStandings")
        val driverMap: HashMap<String, Driver> = HashMap<String, Driver>()
        for (i in 0..<jArray.length()) {
            val jDriver: JSONObject = jArray.getJSONObject(i)
            val jDriverInfo: JSONObject = jDriver.getJSONObject("Driver")
            val jDriverConstructor: JSONObject = jDriver.getJSONArray("Constructors").getJSONObject(0)
            val pos: Int = jDriver.getInt("position")
            val name: String = jDriverInfo.getString("givenName") + " " + jDriverInfo.getString("familyName")
            val points: Double = jDriver.getDouble("points")
            val wins: Int = jDriver.getInt("wins")
            val constructorName: String = jDriverConstructor.getString("name")
            val code: String = jDriverInfo.getString("code")
            val nationality: String = jDriverInfo.getString("nationality")
            val driverId: String = jDriverInfo.getString("driverId")
            val permanentNumber: Int = jDriverInfo.getInt("permanentNumber")
            val isoCode: String = nationalityCodeMap[nationality]!!
            driverMap[driverId] =
                Driver(pos, name, constructorName, points, wins, code, nationality, driverId, permanentNumber, isoCode)
        }
        return driverMap
    }

    /**
     * Parses information from an ErgastDataRetriever.
     * The data is parsed as a JSON object.
     * The method will not update anything unless validUpdate() returns true.
     * @param forceUpdate forces the update of data (this is mostly used for initial data retrieval on bot startup)
     * @return A list containing the constructors for this F1 season.
     */
    fun getF1ConstructorStandingsData(forceUpdate: Boolean): MutableList<Constructor>? {
        val URL = "https://ergast.com/api/f1/current/constructorStandings.json"
        val validUpdate = ergastDataRetriever.validUpdate(URL)
        if (!forceUpdate && !validUpdate) {
            return null
        }
        val json: JSONObject = ergastDataRetriever.getJson(URL, validUpdate)
        val jArray: JSONArray = json.getJSONObject("MRData")
            .getJSONObject("StandingsTable")
            .getJSONArray("StandingsLists")
            .getJSONObject(0).getJSONArray("ConstructorStandings")
        val constructorStandings: MutableList<Constructor> = mutableListOf()
        for (i in 0..<jArray.length()) {
            val jConstructor: JSONObject = jArray.getJSONObject(i)
            val jConstructorInfo: JSONObject = jConstructor.getJSONObject("Constructor")
            val pos: Int = jConstructor.getInt("position")
            val name: String = jConstructorInfo.getString("name")
            val nationality: String = jConstructorInfo.getString("nationality")
            val points: Double = jConstructor.getDouble("points")
            val wins: Int = jConstructor.getInt("wins")
            constructorStandings.add(Constructor(pos, name, nationality, points, wins))
        }
        return constructorStandings
    }

    /**
     * Gets a LocalDateTime object from separate string and time strings. (Strings missing the T separator)
     * @param date String representing date
     * @param time String representing time
     * @return A new LocalDateTime object.
     */
    private fun getInstant(date: String, time: String): Instant {
        return Instant.parse(date + "T" + time)
    }

    private fun readNationalityCodeMapCSV(): HashMap<String, String> {
        try {
                javaClass.getResourceAsStream("/nationalitycodemapping/nationality_code_map.csv").use { inputStream ->
                val map = HashMap<String, String>()
                val scanner = Scanner(inputStream!!)
                while (scanner.hasNextLine()) {
                    val tokens = scanner.nextLine().split(",")
                    map[tokens[3]] = tokens[0].lowercase(Locale.getDefault())
                }
                scanner.close()
                return map
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
