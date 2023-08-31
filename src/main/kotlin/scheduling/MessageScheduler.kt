package scheduling

import model.Race
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Class for scheduling messages for the bot to send to specific discord text channels.
 */
class MessageScheduler(var channelList: List<TextChannel>) {
    private val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var upcomingRaceFuture: ScheduledFuture<*>? = null

    /**
     * Schedules a message containing information about the upcoming race.
     * @param nextRace the next race in the F1 season.
     */
    fun schedule(nextRace: Race) {
        val upcomingRaceMessage = UpcomingRaceMessage(channelList, LocalDateTime.now(), nextRace)
        println(("SCHEDULED TASK FOR: " + nextRace.upcomingDate) + "\n")
        upcomingRaceFuture = executorService.schedule(
            upcomingRaceMessage,
            Instant.now().until(nextRace.upcomingDate, ChronoUnit.MINUTES),
            TimeUnit.MINUTES
        )
    }

    /**
     * Method to cancel a scheduled task.
     */
    fun cancel() {
        upcomingRaceFuture?.cancel(true)
    }
}
