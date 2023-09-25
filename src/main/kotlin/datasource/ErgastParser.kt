package datasource

import model.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.time.Instant
import java.util.*
import kotlin.collections.HashMap

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
    fun getF1RaceData(forceUpdate: Boolean = false): MutableList<Race> {
        val URL = "https://ergast.com/api/f1/current.json"
        val validUpdate = ergastDataRetriever.validUpdate(URL)
        if (!(forceUpdate || validUpdate)) {
            return mutableListOf()
        }
        val json: JSONObject = ergastDataRetriever.getJson(URL, validUpdate)
        val jArray: JSONArray = json.getJSONObject("MRData")
            .getJSONObject("RaceTable")
            .getJSONArray("Races")

        val raceList: MutableList<Race> = mutableListOf()
        for (i in 0..< jArray.length()) {

            //JSON objects
            val jsonRace = jArray.getJSONObject(i)
            val jsonQualifying = jsonRace.getJSONObject("Qualifying")
            val jsonCircuit = jsonRace.getJSONObject("Circuit")
            val jsonCircuitLocation = jsonCircuit.getJSONObject("Location")

            //Values
            val raceInstant = getInstant(jsonRace.getString("date"), jsonRace.getString("time"))
            val round: Int = jsonRace.getInt("round")
            val qualiInstant = getInstant(jsonQualifying.getString("date"), jsonQualifying.getString("time"))
            val circuitName: String = jsonCircuit.getString("circuitName")
            val name: String = jsonRace.getString("raceName")
            val countryName: String = jsonCircuitLocation.getString("country")
            val countryCode: String = countryCodeMap[countryName]!!
            val r = Race(name, circuitName, raceInstant, qualiInstant, round, countryCode)

            if (jsonRace.has("Sprint")) {
                val jSprint: JSONObject = jsonRace.getJSONObject("Sprint")
                r.sprintInstant = getInstant(jSprint.getString("date"), jSprint.getString("time"))
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
    fun getRaceResults(forceUpdate: Boolean = false): MutableList<RaceResult> {
        val raceResults: MutableList<RaceResult> = mutableListOf()
        val url = "https://ergast.com/api/f1/current/results.json?limit=1000"
        val validUpdate = ergastDataRetriever.validUpdate(url)
        if (!(forceUpdate || validUpdate)) {
            return mutableListOf()
        }

        val json: JSONObject = ergastDataRetriever.getJson(url, validUpdate)
        val jArray: JSONArray = json.getJSONObject("MRData")
            .getJSONObject("RaceTable")
            .getJSONArray("Races")

        for (i in 0..<jArray.length()) {
            val raceResult = RaceResult(mutableListOf())

            //JSON Objects
            val race: JSONObject = jArray.getJSONObject(i)
            val resultArray: JSONArray = race.getJSONArray("Results")
            for (j in 0..<resultArray.length()) {
                //JSON Objects
                val jsonDriver: JSONObject = resultArray.getJSONObject(j)
                val jsonNestedDriver = jsonDriver.getJSONObject("Driver")

                //Values
                val driverId: String = jsonNestedDriver.getString("driverId")
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
    fun getF1DriverStandingsData(forceUpdate: Boolean = false): HashMap<String, Driver> {
        val url = "https://ergast.com/api/f1/current/driverStandings.json"
        val validUpdate = ergastDataRetriever.validUpdate(url)
        if (!(forceUpdate || validUpdate)) {
            return HashMap()
        }
        val json: JSONObject = ergastDataRetriever.getJson(url, validUpdate)
        val jArray: JSONArray = json.getJSONObject("MRData")
            .getJSONObject("StandingsTable")
            .getJSONArray("StandingsLists")
            .getJSONObject(0).getJSONArray("DriverStandings")

        val driverMap: HashMap<String, Driver> = HashMap()
        for (i in 0..<jArray.length()) {
            //JSON Objects
            val jDriver = jArray.getJSONObject(i)
            val jDriverInfo = jDriver.getJSONObject("Driver")
            val jDriverConstructor = jDriver.getJSONArray("Constructors").getJSONObject(0)

            //Values
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
    fun getF1ConstructorStandingsData(forceUpdate: Boolean = false): MutableList<Constructor> {
        val url = "https://ergast.com/api/f1/current/constructorStandings.json"
        val validUpdate = ergastDataRetriever.validUpdate(url)
        if (!(forceUpdate || validUpdate)) {
            return mutableListOf()
        }
        val json: JSONObject = ergastDataRetriever.getJson(url, validUpdate)
        val jArray: JSONArray = json.getJSONObject("MRData")
            .getJSONObject("StandingsTable")
            .getJSONArray("StandingsLists")
            .getJSONObject(0).getJSONArray("ConstructorStandings")

        val constructorStandings: MutableList<Constructor> = mutableListOf()
        for (i in 0..<jArray.length()) {
            //JSON Objects
            val jConstructor: JSONObject = jArray.getJSONObject(i)
            val jConstructorInfo: JSONObject = jConstructor.getJSONObject("Constructor")

            //Values
            val pos: Int = jConstructor.getInt("position")
            val name: String = jConstructorInfo.getString("name")
            val nationality: String = jConstructorInfo.getString("nationality")
            val points: Double = jConstructor.getDouble("points")
            val wins: Int = jConstructor.getInt("wins")
            val isoCode = nationalityCodeMap[nationality]!!

            constructorStandings.add(Constructor(pos, name, nationality, points, wins, isoCode))
        }
        return constructorStandings
    }

    /**
     * Gets an Instant from separate date and time strings. (Strings missing the T separator)
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
