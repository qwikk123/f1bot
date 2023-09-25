package service

import commands.CommandManager
import commands.listeners.CommandListener
import datasource.F1DataSource
import model.Constructor
import model.Driver
import model.Race
import net.dv8tion.jda.api.JDA
import scheduling.MessageScheduler
import java.time.Instant

private const val scheduledTextChannel = "f1"

class F1DataService(val bot: JDA) {

    private val messageScheduler: MessageScheduler =
        MessageScheduler(this)

    private val dataSource: F1DataSource
    val commandManager: CommandManager
    private val commandListener: CommandListener

    val raceList: MutableList<Race>
        get() = dataSource.raceList
    val driverMap: HashMap<String, Driver>
        get() = dataSource.driverMap
    val constructorStandings: MutableList<Constructor>
        get() = dataSource.constructorStandings

    lateinit var nextRace: Race

    init {
        dataSource = F1DataSource()
        println("Initialized datasource")
        setNextRace()
        println("nextRace set")

        commandManager = CommandManager(this)
        println("Initialized commandManager")

        commandListener = CommandListener(this)
        println("Initialized commandListener")

        commandListener.upsertCommands(bot.guilds)
        println("Updating /commands for Discord")

        bot.addEventListener(commandListener)
        println("Added commandListener to JDA bot")
        println("Init complete")
    }

    /**
     * setData() will request an F1DataSource to update its data.
     * If the dataSource is updated the service will update its nextRace and
     * tell the messageScheduler to reschedule the new race
     */
    fun setData() {
        if (dataSource.setData()) {
            setNextRace()
            refreshScheduler()
            commandListener.upsertCommands(bot.guilds)
        }
    }

    /**
     * Finds and sets the next race from raceList.
     */
    fun setNextRace() {
        nextRace =
            raceList.firstOrNull { it.raceInstant.isAfter(Instant.now()) }
            ?: raceList[raceList.size-1]
        updateTextChannelDescription()
    }

    /**
     * Refreshes the messageScheduler if the current time is before its scheduled datetime
     * (The scheduled message datetime is always 2 days before the race date)
     */
    fun refreshScheduler() {
        if (nextRace.upcomingDate.isAfter(Instant.now())) {
            messageScheduler.cancel()
            messageScheduler.schedule()
        }
        messageScheduler.cancelUpdate()
        messageScheduler.scheduleUpdate()
    }

    /**
     * Method that sets a text channel description.
     */
    private fun updateTextChannelDescription() {
        val textChannels = bot.getTextChannelsByName(scheduledTextChannel, true)
        for (textChannel in textChannels) {
            textChannel.manager.setTopic("Everything Formula 1 | Next race: ${nextRace.raceRelativeTimestamp}")
                .queue()
        }
    }
}