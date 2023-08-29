package commands.botcommands

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData

/**
 * Abstract class for bot commands.
 */
abstract class BotCommand
/**
 * Creates an instance of this command.
 * @param name This commands name
 * @param description This commands description
 */(val name: String, val description: String) {
    val options: MutableList<OptionData> = mutableListOf()

    /**
     * Method to execute the command.
     * @param event event coming from Discord.
     */
    abstract fun execute(event: SlashCommandInteractionEvent)

    fun hasOptions() = options != null
}
