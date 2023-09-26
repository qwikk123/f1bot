package commands.botcommands

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import service.F1DataService

class ToggleNotifications(name: String, description: String, private val f1DataService: F1DataService) : BotCommand(name, description){
    override fun execute(event: SlashCommandInteractionEvent) {
        f1DataService.toggleNotifications(event.guild!!, event.messageChannel)
        event.hook.sendMessage("Server Notifications have been toggled").queue()
    }

}