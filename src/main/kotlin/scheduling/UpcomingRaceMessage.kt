package scheduling

import model.Race
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.utils.FileUpload
import utils.EmbedCreator
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer

/**
 * Class representing an upcoming race message.
 */
class UpcomingRaceMessage(channelList: List<TextChannel>, scheduledTime: LocalDateTime, nextRace: Race) : Runnable {
    private val channelList: List<TextChannel>
    private val scheduledTime: LocalDateTime
    private val nextRace: Race

    /**
     * Creates an instance of UpcomingRaceMessage and initializes its variables.
     * @param channelList List of text channels to send messages in
     * @param scheduledTime Time for when the messages should be sent
     * @param nextRace The race to be included in the message
     */
    init {
        this.channelList = channelList
        this.scheduledTime = scheduledTime
        this.nextRace = nextRace
    }

    /**
     * Method that runs the scheduled task.
     * Sends an upcoming race message to every text channel in the channelList.
     */
    override fun run() {
        println("Scheduled at: " + scheduledTime + " Running at: " + LocalDateTime.now())
        val inputStream = Objects.requireNonNull(
            javaClass.getResourceAsStream(nextRace.imagePath), "inputStream is null"
        )
        channelList.forEach(
            Consumer { x: TextChannel ->
                x.sendMessageEmbeds(EmbedCreator.createUpcoming(nextRace).build())
                    .addFiles(FileUpload.fromData(inputStream, "circuitImage.png"))
                    .queue()
            }
        )
    }
}
