package datasource

import model.Constructor
import model.Driver
import model.Race
import model.RaceResult

/**
 * Class representing the source of F1 data.
 */
class F1DataSource {
    private var raceList: MutableList<Race>? = null
    private var driverMap: HashMap<String, Driver>? = null
    private var constructorStandings: MutableList<Constructor>? = null
    private val ergastParser: ErgastParser = ErgastParser()

    /**
     * Method that updates all F1 data tied to this datasource.
     * It updates the raceList, driverMap, constructorStandings and the results tied to races that have any.
     * If the ErgastParser returns null it will view the data as up to date.
     * @return true if any of the data was updated or false if nothing changed.
     */
    fun setData(): Boolean {
        var updated = false
        val newRaceList: MutableList<Race>? = ergastParser.getF1RaceData(raceList == null)
        if (newRaceList != null) {
            raceList = newRaceList
            updated = true
        }
        val newDriverMap: HashMap<String, Driver>? = ergastParser.getF1DriverStandingsData(driverMap == null)
        if (newDriverMap != null) {
            driverMap = newDriverMap
            updated = true
        }
        val newConstructorStandings: MutableList<Constructor>? =
            ergastParser.getF1ConstructorStandingsData(constructorStandings == null)
        if (newConstructorStandings != null) {
            constructorStandings = newConstructorStandings
            updated = true
        }
        val raceResults: MutableList<RaceResult>? = ergastParser.getRaceResults(raceList!![0].getRaceResult() == null)
        if (raceResults != null) {
            for (i in raceResults.indices) {
                raceList!![i].setRaceResult(raceResults[i])
            }
            updated = true
        }
        //      Any more api requests will require a delay. Max 4 polls per second/ 200 per hour
        return updated
    }

    fun retrieveRaceList(): MutableList<Race>? {
        return raceList
    }

    fun retrieveDriverMap(): HashMap<String, Driver>? {
        return driverMap
    }

    fun retrieveConstructorStandings(): MutableList<Constructor>? {
        return constructorStandings
    }
}
