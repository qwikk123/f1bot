package service

import commands.CommandManager
import commands.listeners.CommandListener
import datasource.discorddata.DiscordDataSource
import datasource.f1data.F1DataSource
import model.Constructor
import model.DiscordServer
import model.Driver
import model.Race
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import scheduling.MessageScheduler
import java.time.Instant

private const val scheduledTextChannel = "f1"

class F1DataService(val bot: JDA) {

    private val messageScheduler: MessageScheduler =
        MessageScheduler(this)

    private val f1DataSource: F1DataSource = F1DataSource(bot)
    private val discordDataSource: DiscordDataSource = DiscordDataSource(bot)
    val commandManager: CommandManager
    private val commandListener: CommandListener

    val raceList: MutableList<Race>
        get() = f1DataSource.raceList
    val driverMap: HashMap<String, Driver>
        get() = f1DataSource.driverMap
    val constructorStandings: MutableList<Constructor>
        get() = f1DataSource.constructorStandings
    val serverNotificationList: MutableList<DiscordServer>
        get() = discordDataSource.serverNotificationsList

    lateinit var nextRace: Race

    init {
        setNextRace()
        refreshScheduler()

        commandManager = CommandManager(this)
        commandListener = CommandListener(this)
        commandListener.upsertCommands(bot.guilds)

        bot.addEventListener(commandListener)
        println("Init complete")
    }

    /**
     * setData() will request an F1DataSource to update its data.
     * If the dataSource is updated the service will update its nextRace and
     * tell the messageScheduler to reschedule the new race
     */
    fun setData() {
        if (f1DataSource.setData()) {
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

    fun toggleNotifications(server: Guild, messageChannel: MessageChannel): Boolean {
        val removed = serverNotificationList.removeIf { it.server.id == server.id }
        if (!removed) {
            serverNotificationList.add(DiscordServer(messageChannel, server))
        }
        discordDataSource.updateNotifications()
        return removed
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