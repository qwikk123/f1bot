package service

import commands.listeners.CommandListener
import datasource.F1DataSource
import model.Constructor
import model.Driver
import model.Race
import net.dv8tion.jda.api.JDA
import scheduling.MessageScheduler
import java.time.Instant

private const val scheduledTextChannel = "f1"

class F1DataService(bot: JDA, commandListener: CommandListener) {
    private val messageScheduler: MessageScheduler
    private val bot: JDA
    private val commandListener: CommandListener
    private val dataSource: F1DataSource
    private var nextRace: Race? = null

    /**
     * Creates an F1DataService and initializes its F1DataSource and MessageScheduler.
     * Then it tells the dataSource to set its data.
     * @param bot an instance of a JDA bot
     * @param commandListener an instance of a CommandListener
     */
    init {
        this.commandListener = commandListener
        dataSource = F1DataSource()
        this.bot = bot
        messageScheduler = MessageScheduler(bot.getTextChannelsByName(scheduledTextChannel, true))
        setData()
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
            if (commandListener.isReady) commandListener.upsertCommands(bot.guilds)
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
        if (nextRace!!.upcomingDate.isAfter(Instant.now())) {
            messageScheduler.setChannelList(bot.getTextChannelsByName(scheduledTextChannel, true))
            messageScheduler.cancel()
            messageScheduler.schedule(nextRace!!)
        }
    }

    /**
     * Method that sets a text channel description.
     */
    private fun updateTextChannelDescription() {
        val textChannels = bot.getTextChannelsByName(scheduledTextChannel, true)
        for (textChannel in textChannels) {
            textChannel.manager.setTopic("Everything Formula 1 | Next race: " + nextRace!!.raceRelativeTimestamp)
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