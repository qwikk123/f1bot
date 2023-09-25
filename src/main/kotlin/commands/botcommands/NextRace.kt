package commands.botcommands

import model.Race
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.utils.FileUpload
import utils.EmbedCreator
import java.time.Instant

/**
 * Class representing the /nextrace command.
 */
class NextRace(name: String, description: String, private val raceList: MutableList<Race>) : BotCommand(name, description) {

    override fun execute(event: SlashCommandInteractionEvent) {
        val nextRace: Race = nextRace
        val inputStream = javaClass.getResourceAsStream(nextRace.imagePath)!!

        event.hook.sendMessageEmbeds(EmbedCreator.createRace(nextRace).build())
            .addFiles(FileUpload.fromData(inputStream, "circuitImage.png"))
            .queue()
    }

    private val nextRace: Race
        get() {
            return raceList.firstOrNull { it.raceInstant.isAfter(Instant.now()) }
                ?: raceList[raceList.size-1]
        }
}
