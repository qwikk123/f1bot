import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import service.F1DataService
import java.io.File

fun main() {
    val token = File("token/token.txt").useLines { it.firstOrNull() }
    if (token == null) { println("Token error"); return }

    val bot = JDABuilder.createDefault(token)
        .setActivity(Activity.listening("F1 theme song"))
        .enableIntents(GatewayIntent.MESSAGE_CONTENT)
        .build()
    bot.awaitReady()

    F1DataService(bot)
}