package commands.listeners

import commands.CommandManager
import commands.botcommands.BotCommand
import commands.botcommands.DriverStandings
import commands.botcommands.GetRace
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import service.F1DataService
import java.util.*

/**
 * Class for managing EventListeners relating to BotCommands and their buttons.
 * The class extends the JDA class ListenerAdapter
 */
class CommandListener(bot: JDA) : ListenerAdapter() {
    private val commandManager: CommandManager
    private val f1DataService: F1DataService
    var isReady = false
        private set

    init {
        f1DataService = F1DataService(bot, this)
        commandManager = CommandManager(f1DataService)
    }

    /**
     * Method to handle SlashCommands (/commandName) from Discord.
     * @param event event coming from Discord.
     */
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()
        f1DataService.setData()
        commandManager.commands[event.name]!!.execute(event)
    }

    /**
     * Method that sets the boolean ready to true.
     * It runs on the bots ReadyEvent.
     * This is to stop the Listener from updating commands before the CommandManager and F1DataService is ready.
     * It is initialized in the Main method through upsertCommands()
     * @param event event coming from Discord.
     */
    override fun onReady(event: ReadyEvent) {
        super.onReady(event)
        isReady = true
    }

    /**
     * Method to update commands when the bot joins a new server.
     * @param event event coming from Discord.
     */
    override fun onGuildJoin(event: GuildJoinEvent) {
        val guildList = ArrayList<Guild>()
        guildList.add(event.guild)
        upsertCommands(guildList)
    }

    /**
     * Method to update or create new commands on a discord server.
     * @param guilds List of servers to add/update commands for.
     */
    fun upsertCommands(guilds: List<Guild>) {
        val commandData: MutableList<CommandData> = ArrayList()
        for (command in commandManager.commandList) {
            val scd = Commands.slash(command.name, command.description)
            if (command.hasOptions()) {
                scd.addOptions(command.options)
            }
            commandData.add(scd)
        }
        for (cd in commandData) {
            for (g in guilds) {
                g.upsertCommand(cd).queue()
            }
        }
    }

    /**
     * Method handling button interactions it calls the specific commands button methods.
     * @param event event coming from Discord.
     */
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val buttonId  = event.button.id!!
        when (val buttonType = buttonId.split("-")[1]) {
            "dstandings" -> {
                val command: BotCommand = commandManager.commands["driverstandings"]!!
                if (command is DriverStandings) {
                    command.handleButtons(event, buttonId)
                }
            }

            "getrace", "resultpage" -> {
                val command: BotCommand = commandManager.commands["getrace"]!!
                if (command is GetRace) {
                    command.handleButtons(event, buttonId, buttonType, f1DataService.driverMap!!)
                }
            }
        }
    }
}
