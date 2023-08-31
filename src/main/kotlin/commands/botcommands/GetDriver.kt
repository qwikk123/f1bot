package commands.botcommands

import model.Driver
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.utils.FileUpload
import utils.EmbedCreator

/**
 * Class representing the /getdriver command.
 */
class GetDriver(name: String, description: String, private val driverMap: HashMap<String, Driver>) : BotCommand(name, description) {
    init {
        val choiceList = ArrayList<Command.Choice>()
        driverMap.values.forEach { choiceList.add(Command.Choice(it.name, it.driverId))}
        options.add(
            OptionData(
                OptionType.STRING,
                "drivername",
                "Select driver to get info from",
                true,
                false
            ).addChoices(choiceList)
        )
    }

    override fun execute(event: SlashCommandInteractionEvent) {
        val driverId: String = (event.getOption("drivername"))!!.asString
        val driver: Driver = driverMap[driverId]!!
        val inputStream = javaClass.getResourceAsStream("/driverimages/${driver.code}.png")!!

        event.hook.sendMessageEmbeds(EmbedCreator.createDriverProfile(driver).build())
            .addFiles(FileUpload.fromData(inputStream, "driverImage.png"))
            .queue()
    }
}
