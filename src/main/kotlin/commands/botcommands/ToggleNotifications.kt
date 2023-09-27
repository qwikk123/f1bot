package commands.botcommands

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class ToggleNotifications(name: String, description: String) : BotCommand(name, description){
    private val roleName = "F1Notifications"
    override fun execute(event: SlashCommandInteractionEvent) {
        val guild = event.guild!!
        val member = event.member!!

        val role = guild.roles.firstOrNull { it.name == roleName }
            ?: guild.createRole().setName(roleName).complete()!!

        if (member.roles.any { it.name == roleName }) {
            guild.removeRoleFromMember(member, role).queue()
            event.hook.sendMessage("Notifications for ${member.asMention} have been turned OFF").queue()
        }
        else {
            guild.addRoleToMember(member, role).queue()
            event.hook.sendMessage("Notifications for ${member.asMention} have been turned ON").queue()
        }
    }

}