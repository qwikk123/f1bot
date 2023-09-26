package scheduling

import model.Race
import net.dv8tion.jda.api.utils.FileUpload
import service.F1DataService
import utils.EmbedCreator
import java.time.LocalDateTime

/**
 * Class representing an upcoming race message.
 */
class UpcomingRaceMessage(
    private val f1DataService: F1DataService,
    private val scheduledTime: LocalDateTime,
    private val nextRace: Race) : Runnable {

    /**
     * Method that runs the scheduled task.
     * Sends an upcoming race message to every text channel in the channelList.
     */
    override fun run() {
        println("Scheduled at: $scheduledTime Running at: ${LocalDateTime.now()}")

        f1DataService.serverNotificationList.forEach { x ->
            val role = f1DataService.bot.getGuildById(x.server.id)?.roles?.firstOrNull { it.name == "F1Notifications" }
            val messageChannel = f1DataService.bot.textChannels.firstOrNull { it.id == x.messageChannel.id }
            if (role != null) messageChannel?.sendMessage(role.asMention)?.queue()

            val inputStream = javaClass.getResourceAsStream(nextRace.imagePath)!!
            messageChannel?.sendMessageEmbeds(EmbedCreator.createUpcoming(nextRace).build())
                ?.addFiles(FileUpload.fromData(inputStream, "circuitImage.png"))
                ?.queue()
        }
    }
}
