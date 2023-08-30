import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import service.F1DataService

fun main() {
    val token = Any().javaClass.getResource("token/token.txt")!!.readText()

    val bot = JDABuilder.createDefault(token)
        .setActivity(Activity.listening("F1 theme song"))
        .enableIntents(GatewayIntent.MESSAGE_CONTENT)
        .build()
    bot.awaitReady()

    F1DataService(bot)
}