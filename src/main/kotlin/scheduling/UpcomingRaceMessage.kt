package scheduling

import model.Race
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.utils.FileUpload
import utils.EmbedCreator
import java.time.LocalDateTime

private const val scheduledTextChannel = "f1"

/**
 * Class representing an upcoming race message.
 */
class UpcomingRaceMessage(
    private var bot: JDA,
    private val scheduledTime: LocalDateTime,
    private val nextRace: Race) : Runnable {

    /**
     * Method that runs the scheduled task.
     * Sends an upcoming race message to every text channel in the channelList.
     */
    override fun run() {
        println("Scheduled at: $scheduledTime Running at: ${LocalDateTime.now()}")
        val inputStream = javaClass.getResourceAsStream(nextRace.imagePath)!!

        bot.getTextChannelsByName(scheduledTextChannel, true).forEach { x ->
            x.sendMessageEmbeds(EmbedCreator.createUpcoming(nextRace).build())
                .addFiles(FileUpload.fromData(inputStream, "circuitImage.png"))
                .queue()
        }
    }
}
