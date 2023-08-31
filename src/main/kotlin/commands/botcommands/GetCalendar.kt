package commands.botcommands

import model.Race
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import utils.EmbedCreator


class GetCalendar(name: String, description: String, private val raceList: List<Race>) : BotCommand(name, description) {

    override fun execute(event: SlashCommandInteractionEvent) {
        event.hook.sendMessageEmbeds(EmbedCreator.createCalendar(raceList).build()).queue()
    }
}
