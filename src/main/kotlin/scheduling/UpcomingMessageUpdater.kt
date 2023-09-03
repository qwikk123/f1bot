package scheduling

import java.time.Instant

class UpcomingMessageUpdater(private val messageScheduler: MessageScheduler) : Runnable {
    override fun run() {
        println("SCHEDULED UPDATE RUNNING AT ${Instant.now()}")
        messageScheduler.reSchedule()
    }

}