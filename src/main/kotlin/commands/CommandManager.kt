package commands

import commands.botcommands.*
import service.F1DataService

/**
 * Class for managing all the bot commands.
 * It maps the commands to their respective names/ids.
 */
class CommandManager(f1DataService: F1DataService) {
    val commands = HashMap<String, BotCommand>()

    /**
     * Creates an instance of CommandManager and initializes all the commands the bot will use.
     * @param f1DataService F1DataService to get initial command data from.
     */
    init {
        val ping: BotCommand = Ping(
            "ping",
            "ping the bot :)"
        )
        commands[ping.name] = ping
        val getRace: BotCommand = GetRace(
            "getrace",
            "Get info from a specific Grand Prix",
            f1DataService.raceList!!
        )
        commands[getRace.name] = getRace
        val nextRace: BotCommand = NextRace(
            "nextrace",
            "Get info from the next Grand Prix",
            f1DataService.raceList!!
        )
        commands[nextRace.name] = nextRace
        val driverStandings: BotCommand = DriverStandings(
            "driverstandings",
            "Get the current standings in the drivers championship",
            f1DataService.driverMap!!
        )
        commands[driverStandings.name] = driverStandings
        val constructorStandings: BotCommand = ConstructorStandings(
            "constructorstandings",
            "Get the current standings in the constructor championship",
            f1DataService.constructorStandings!!
        )
        commands[constructorStandings.name] = constructorStandings
        val getDriver: BotCommand = GetDriver(
            "getdriver",
            "get information about a driver",
            f1DataService.driverMap!!
        )
        commands[getDriver.name] = getDriver
        val getCalendar: BotCommand = GetCalendar(
            "getcalendar",
            "show the current f1 calendar",
            f1DataService.raceList!!
        )
        commands[getCalendar.name] = getCalendar
    }

    val commandList: List<BotCommand>
        get() = commands.values.toList()
}
