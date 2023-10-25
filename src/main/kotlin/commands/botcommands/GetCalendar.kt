package commands.botcommands

import model.Race
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import utils.EmbedCreator


class GetCalendar(name: String, description: String, private val raceList: List<Race>,
                  private val nextRace:Race) : BotCommand(name, description) {

    override fun execute(event: SlashCommandInteractionEvent) {
        event.hook.sendMessageEmbeds(EmbedCreator.createCalendar(raceList, nextRace).build()).queue()
    }
}
