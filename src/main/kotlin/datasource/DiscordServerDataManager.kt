package datasource

import model.DiscordServer
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class DiscordServerDataManager {
    private val serverConfigPath: String = "server_settings/notifications.json"
    fun getServerNotificationToggles(): JSONArray {
        val f = File(serverConfigPath)
        if (f.isFile) return getDataFromFile(f)

        Files.createDirectories(Paths.get(f.getParent()))
        return JSONArray()
    }

    fun updateServerNotificationToggles(serverList: MutableList<DiscordServer>) {
        val f = File(serverConfigPath)
        val jsonArray = JSONArray()
        serverList.forEach {
            jsonArray.put(JSONObject()
                    .put("textChannel",it.messageChannel.id)
                    .put("guild",it.server.id)) }
        f.writeText(jsonArray.toString())
    }
    private fun getDataFromFile(f: File) = JSONArray(JSONTokener(f.readText()))
}