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

class F1DataService(private val bot: JDA) {

    private val messageScheduler: MessageScheduler =
        MessageScheduler(bot.getTextChannelsByName(scheduledTextChannel, true))
    private val dataSource: F1DataSource = F1DataSource()
    private lateinit var nextRace: Race
    val commandManager: CommandManager
    private val commandListener: CommandListener

    init {
        setData()
        println("setData() completed")

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
        val updated: Boolean = dataSource.setData()
        if (updated) {
            setNextRace(dataSource.retrieveRaceList())
            commandListener?.upsertCommands(bot.guilds)
        }
        updateTextChannelDescription()
    }

    /**
     * Finds the next race from today in raceList, updates nextRace and tells the scheduler to refresh itself.
     * @param raceList List of races in the current F1 season.
     */
    private fun setNextRace(raceList: MutableList<Race>?) {
        for (r in raceList!!) {
            if (r.raceInstant.isAfter(Instant.now())) {
                nextRace = r
                refreshScheduler()
                return
            }
        }
    }

    /**
     * Refreshes the messageScheduler if the current time is before its scheduled datetime
     * (The scheduled message datetime is always 2 days before the race date)
     */
    private fun refreshScheduler() {
        if (nextRace.upcomingDate.isAfter(Instant.now())) {
            messageScheduler.channelList = bot.getTextChannelsByName(scheduledTextChannel, true)
            messageScheduler.cancel()
            messageScheduler.schedule(nextRace)
        }
    }

    /**
     * Method that sets a text channel description.
     */
    private fun updateTextChannelDescription() {
        val textChannels = bot.getTextChannelsByName(scheduledTextChannel, true)
        for (textChannel in textChannels) {
            textChannel.manager.setTopic("Everything Formula 1 | Next race: " + nextRace.raceRelativeTimestamp)
                .queue()
        }
    }

    val raceList: MutableList<Race>?
        get() = dataSource.retrieveRaceList()
    val driverMap: HashMap<String, Driver>?
        get() = dataSource.retrieveDriverMap()
    val constructorStandings: MutableList<Constructor>?
        get() = dataSource.retrieveConstructorStandings()
}