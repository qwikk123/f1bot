package commands.botcommands

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import service.F1DataService

class ToggleNotifications(name: String, description: String, private val f1DataService: F1DataService) : BotCommand(name, description){
    override fun execute(event: SlashCommandInteractionEvent) {
        if (event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
            val removed = f1DataService.toggleNotifications(event.guild!!, event.messageChannel)
            val toggleStatus = if(removed) "OFF" else "ON"
            event.hook.sendMessage("Server Notifications have been toggled $toggleStatus").queue()
        }
        else {
            event.hook.sendMessage("You do not have permission to use this command").queue()
        }
    }

}