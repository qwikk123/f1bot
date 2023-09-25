package datasource

import model.Constructor
import model.Driver
import model.Race
import model.RaceResult

/**
 * Class representing the source of F1 data.
 */
class F1DataSource {
    private val ergastParser: ErgastParser = ErgastParser()

    var raceList: MutableList<Race>
    var driverMap: HashMap<String, Driver>
    var constructorStandings: MutableList<Constructor>

    init {
        raceList = ergastParser.getF1RaceData(true)
        driverMap = ergastParser.getF1DriverStandingsData(true)
        constructorStandings = ergastParser.getF1ConstructorStandingsData(true)
        setRaceResults(true)
    }

    /**
     * Method that updates all F1 data tied to this datasource.
     * It updates the raceList, driverMap, constructorStandings and the results tied to races that have any.
     * If the ErgastParser returns null it will view the data as up to date.
     * @return true if any of the data was updated or false if nothing changed.
     */
    fun setData(): Boolean {
        var updated = false
        val newRaceList: MutableList<Race> = ergastParser.getF1RaceData()
        if (newRaceList.isNotEmpty()) {
            raceList = newRaceList
            updated = true
        }
        val newDriverMap: HashMap<String, Driver> = ergastParser.getF1DriverStandingsData()
        if (newDriverMap.isNotEmpty()) {
            driverMap = newDriverMap
            updated = true
        }
        val newConstructorStandings: MutableList<Constructor> =
            ergastParser.getF1ConstructorStandingsData()
        if (newConstructorStandings.isNotEmpty()) {
            constructorStandings = newConstructorStandings
            updated = true
        }
        updated = updated || setRaceResults()

        //      Any more api requests will require a delay. Max 4 polls per second/ 200 per hour
        return updated
    }

    private fun setRaceResults(forceUpdate: Boolean = false): Boolean {
        val raceResults: MutableList<RaceResult> = ergastParser.getRaceResults(forceUpdate)
        val isEmpty = raceResults.isNotEmpty()
        if (isEmpty) {
            for (i in raceResults.indices) {
                raceList[i].raceResult = raceResults[i]
            }
        }
        return isEmpty
    }
}
