package model

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel

data class DiscordServer(val messageChannel:MessageChannel, val server: Guild)
