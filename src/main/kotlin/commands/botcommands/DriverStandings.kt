package commands.botcommands

import model.Driver
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import utils.EmbedCreator
import java.util.*

private const val pageSize = 7
/**
 * Class representing the /driverstandings command.
 */
class DriverStandings(name: String, description: String, private val driverMap: HashMap<String, Driver>) :
    BotCommand(name, description) {

    override fun execute(event: SlashCommandInteractionEvent) {
        val buttonList: MutableList<Button> = mutableListOf()
        buttonList.add(Button.danger("prev-dstandings", "Previous").asDisabled())
        buttonList.add(Button.danger("next-dstandings", "Next"))
        event.hook.sendMessageEmbeds(
            EmbedCreator.createDriverStandings(driverMap, 0, pageSize).build()
        )
            .setActionRow(buttonList)
            .queue()
    }

    /**
     * Method to handle the buttons for this bot command.
     * @param event event from Discord.
     * @param buttonId The pressed buttons id
     */
    fun handleButtons(event: ButtonInteractionEvent, buttonId: String?) {
        val pageNumber = event.message.embeds[0].footer!!.text
            ?.split("/")!![0]

        var page = pageNumber.toInt() - 1
        val buttonList: MutableList<Button> = event.message.buttons.map { it.asEnabled() }.toMutableList()
        when (buttonId) {
            "next-dstandings" -> {
                page++
                if (page * pageSize + pageSize >= driverMap.size) {
                    buttonList[1] = buttonList[1].asDisabled()
                }
            }

            "prev-dstandings" -> {
                page--
                if (page == 0) {
                    buttonList[0] = buttonList[0].asDisabled()
                }
            }
        }

        event.editMessageEmbeds(
            EmbedCreator.createDriverStandings(driverMap, page, pageSize).build()
        )
            .setActionRow(buttonList)
            .queue()
    }
}
