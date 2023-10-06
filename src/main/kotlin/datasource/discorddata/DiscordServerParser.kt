package datasource.discorddata

import model.DiscordServer
import net.dv8tion.jda.api.JDA
import org.json.JSONArray

class DiscordServerParser(val bot: JDA) {

    fun parseServerNotifications(serverJsonArray: JSONArray): MutableList<DiscordServer> {
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