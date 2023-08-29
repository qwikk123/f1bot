package commands.botcommands

import model.Constructor
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import utils.EmbedCreator

/**
 * Class representing the /constructorstandings command.
 */
class ConstructorStandings(name: String, description: String, private val constructorStandings: List<Constructor>) :
    BotCommand(name, description) {

    override fun execute(event: SlashCommandInteractionEvent) {
        event.hook.sendMessageEmbeds(
            EmbedCreator.createConstructorStandings(constructorStandings).build()).queue()
    }
}
