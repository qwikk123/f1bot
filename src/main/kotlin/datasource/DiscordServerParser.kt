package datasource

import model.DiscordServer
import net.dv8tion.jda.api.JDA

class DiscordServerParser(val bot: JDA, private val discordData: DiscordServerDataManager) {

    fun getServerNotificationToggles(): MutableList<DiscordServer> {
        val serverJsonArray = discordData.getServerNotificationToggles()
        val serverList = mutableListOf<DiscordServer>()
        if (serverJsonArray.isEmpty) return serverList

        for (i in 0 ..< serverJsonArray.length()) {
            val jsonObject = serverJsonArray.getJSONObject(i)
            val messageChannel = bot.getTextChannelById(jsonObject.getString("textChannel"))
            val server = bot.getGuildById(jsonObject.getString("guild"))

            serverList.add(DiscordServer(messageChannel!!,server!!))
        }
        return serverList
    }
}