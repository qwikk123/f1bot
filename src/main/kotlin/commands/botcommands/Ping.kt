package commands.botcommands

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

/**
 * Class representing the /ping command.
 */
class Ping(name: String, description: String) : BotCommand(name, description) {
    override fun execute(event: SlashCommandInteractionEvent) {
        val ping = event.timeCreated.until(OffsetDateTime.now(), ChronoUnit.MILLIS)
        event.hook.sendMessage("pong after: $ping ms :)").queue()
    }
}
