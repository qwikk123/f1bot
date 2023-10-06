package datasource.discorddata

import net.dv8tion.jda.api.JDA

class DiscordDataSource(bot: JDA) {
    private val discordServerDataManager = DiscordServerDataManager()
    private val discordServerParser = DiscordServerParser(bot)
    val serverNotificationsList = discordServerParser.parseServerNotifications(discordServerDataManager.getServerNotifications())

    fun updateNotifications() {
        discordServerDataManager.updateServerNotifications(serverNotificationsList)
    }
}