package commands.listeners

import commands.CommandManager
import commands.botcommands.BotCommand
import commands.botcommands.DriverStandings
import commands.botcommands.GetRace
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import service.F1DataService
import java.util.*

/**
 * Class for managing EventListeners relating to BotCommands and their buttons.
 * The class extends the JDA class ListenerAdapter
 */
class CommandListener(private val f1DataService: F1DataService) : ListenerAdapter() {
    private val commandManager: CommandManager = f1DataService.commandManager

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
