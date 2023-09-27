package commands

import commands.botcommands.*
import service.F1DataService

/**
 * Class for managing all the bot commands.
 * It maps the commands to their respective names/ids.
 */
class CommandManager(f1DataService: F1DataService) {
    val commands = HashMap<String, BotCommand>()
    val commandList: List<BotCommand>
        get() = commands.values.toList()

    init {
        //Create commands
        val ping: BotCommand = Ping(
            "ping",
            "ping the bot :)"
        )
        val getRace: BotCommand = GetRace(
            "getrace",
            "Get info from a specific Grand Prix",
            f1DataService.raceList
        )
        val nextRace: BotCommand = NextRace(
            "nextrace",
            "Get info from the next Grand Prix",
            f1DataService.raceList
        )
        val driverStandings: BotCommand = DriverStandings(
            "driverstandings",
            "Get the current standings in the drivers championship",
            f1DataService.driverMap
        )
        val constructorStandings: BotCommand = ConstructorStandings(
            "constructorstandings",
            "Get the current standings in the constructor championship",
            f1DataService.constructorStandings
        )
        val getDriver: BotCommand = GetDriver(
            "getdriver",
            "get information about a driver",
            f1DataService.driverMap
        )
        val getCalendar: BotCommand = GetCalendar(
            "getcalendar",
            "show the current f1 calendar",
            f1DataService.raceList
        )
        val toggleNotifications: BotCommand = ToggleNotifications(
            "togglenotifications",
            "Toggle message pings on/off"
        )
        val toggleServerMessages: BotCommand = ToggleServerMessages(
            "toggleservermessages",
            "Toggle messages for this server on/off (Requires Admin)",
            f1DataService
        )

        //Insert commands into map
        commands[ping.name] = ping
        commands[getRace.name] = getRace
        commands[nextRace.name] = nextRace
        commands[driverStandings.name] = driverStandings
        commands[constructorStandings.name] = constructorStandings
        commands[getDriver.name] = getDriver
        commands[getCalendar.name] = getCalendar
        commands[toggleNotifications.name] = toggleNotifications
        commands[toggleServerMessages.name] = toggleServerMessages
    }
}
