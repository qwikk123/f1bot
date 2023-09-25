package commands.botcommands

import model.Driver
import model.Race
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.utils.FileUpload
import utils.EmbedCreator
import java.io.InputStream
import java.util.regex.Pattern

private const val pageSize = 10

/**
 * Class representing the /getrace command.
 */
class GetRace(name: String, description: String, private val raceList: MutableList<Race>) : BotCommand(name, description) {
    init {
        options.add(
            OptionData(
                OptionType.INTEGER,
                "racenumber",
                "Race to get info from",
                true,
                true
            ).setMinValue(1).setMaxValue(raceList.size.toLong())
        )
    }

    override fun execute(event: SlashCommandInteractionEvent) {
        val buttonList: MutableList<Button> = mutableListOf()
        buttonList.add(Button.danger("info-getrace", "Info").asDisabled())
        buttonList.add(Button.danger("result-getrace", "Result"))
        val index: Int = event.getOption("racenumber")!!.asInt - 1

        val race: Race = raceList[index]
        val inputStream :InputStream = javaClass.getResourceAsStream(race.imagePath)!!

        val action = event.hook.sendMessageEmbeds(EmbedCreator.createRace(race).build())
                .addFiles(FileUpload.fromData(inputStream, "circuitImage.png"))

        if (race.hasRaceResult()) {
            action.addActionRow(buttonList)
        }

        action.queue()
    }

    /**
     * Method for handling button presses for this command.
     * @param event event from Discord.
     * @param buttonId The id for the pressed button
     * @param buttonType The type for the pressed button
     * @param driverMap Map containing the drivers from this F1 season.
     */
    fun handleButtons(
        event: ButtonInteractionEvent,
        buttonId: String,
        buttonType: String,
        driverMap: HashMap<String, Driver>
    ) {
        val buttonList: MutableList<Button> = event.message.buttons.map { it.asEnabled() }.toMutableList()
        val title: String = event.message.embeds[0].title!!

        val matcher = Pattern.compile("\\d+").matcher(title)
        if (!matcher.find()) {
            println("Invalid pattern in getrace")
            return
        }
        val index = matcher.group(0).toInt() - 1
        val race: Race = raceList[index]
        if (buttonId == "info-getrace") {
            clickInfo(buttonList, event, race)
        } else if (buttonId == "result-getrace") {
            clickResult(buttonList, event, race, driverMap)
        } else if (buttonType == "resultpage") {
            clickResultPage(buttonList, event, race, driverMap, buttonId)
        }
    }

    /**
     * Method to handle the press of the Info button.
     * @param buttonList List of buttons from the current Discord event
     * @param event event from Discord
     * @param race Current race to get info from
     */
    private fun clickInfo(buttonList: MutableList<Button>, event: ButtonInteractionEvent, race: Race) {
        buttonList[0] = buttonList[0].asDisabled()
        val inputStream = javaClass.getResourceAsStream(race.imagePath)!!

        event.editMessageEmbeds(EmbedCreator.createRace(race).build())
            .setFiles(FileUpload.fromData(inputStream, "circuitImage.png"))
            .setActionRow(buttonList.subList(0, 2))
            .queue()
    }

    /**
     * Method to handle the press of the Result button.
     * @param buttonList List of buttons from the current Discord event
     * @param event event from Discord
     * @param race Current race to get info from
     * @param driverMap Map containing the drivers from this F1 season.
     */
    private fun clickResult(
        buttonList: MutableList<Button>,
        event: ButtonInteractionEvent,
        race: Race,
        driverMap: HashMap<String, Driver>
    ) {
        buttonList[1] = buttonList[1].asDisabled()
        buttonList.add(Button.danger("prev-resultpage", "Prev").asDisabled())
        buttonList.add(Button.danger("next-resultpage", "Next"))
        event.editMessageEmbeds(EmbedCreator.createRaceResult(race, driverMap, 0, pageSize).build())
            .setActionRow(buttonList)
            .setReplace(true)
            .queue()
    }

    /**
     * Method for handling paging for the race result table.
     * @param buttonList List of buttons from the current Discord event
     * @param event event from Discord
     * @param race Current race to get info from
     * @param driverMap Map containing the drivers from this F1 season.
     * @param buttonId Id for the pressed button
     */
    private fun clickResultPage(
        buttonList: MutableList<Button>,
        event: ButtonInteractionEvent,
        race: Race,
        driverMap: HashMap<String, Driver>,
        buttonId: String
    ) {
        val footer: String = event.message.embeds[0].footer!!.text!!

        var page = footer.split("/")[0].toInt() - 1
        val maxPage = footer.split("/")[1].toInt()

        when (buttonId) {
            "next-resultpage" -> {
                page++
                if (page * pageSize + pageSize >= maxPage) {
                    buttonList[3] = buttonList[3].asDisabled()
                }
            }

            "prev-resultpage" -> {
                page--
                if (page == 0) {
                    buttonList[2] = buttonList[2].asDisabled()
                }
            }
        }
        buttonList[1] = buttonList[1].asDisabled()
        event.editMessageEmbeds(EmbedCreator.createRaceResult(race, driverMap, page, pageSize).build())
            .setActionRow(buttonList)
            .queue()
    }
}
