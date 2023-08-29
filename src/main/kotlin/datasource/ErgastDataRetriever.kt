package datasource

import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.FileTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

/**
 * Class for retrieving data from the Ergast API
 */
class ErgastDataRetriever {
    /**
     * Method to get json information from the ergast api or the cache
     * @param URL url pointing to the Ergast API endpoint.
     * @param validUpdate Whether to use cache or retrieve new data.
     * @return a JSONObject containing Ergast API data.
     */
    fun getJson(URL: String, validUpdate: Boolean): JSONObject {
        val fileName = getFileNameOfURL(URL)
        val f = File("cache/$fileName")
        if (f.isFile() && !validUpdate) {
            println("Retrieving data from cache")
            return getJsonFromFile(f)
        }
        println("Retrieving data from ergast")
        return getJsonFromURL(URL)
    }

    /**
     * Method to get a JSONObject from the cache
     * @param f Which file to retrieve data from.
     * @return A JSONObject containing Ergast API data.
     */
    private fun getJsonFromFile(f: File): JSONObject {
        val json = f.readText()
        return JSONObject(JSONTokener(json))
    }

    /**
     * Method to get a JSONObject from an Ergast endpoint.
     * @param URL Which Ergast endpoint to retrieve data from.
     * @return A JSONObject containing Ergast API data.
     */
    private fun getJsonFromURL(URL: String): JSONObject {
        val fileName = getFileNameOfURL(URL)
        return try {
            println(URL)
            val url = URI(URL).toURL()
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestMethod("GET")
            conn.connect()
            val inputStreamReader = InputStreamReader(url.openStream())
            val bufferedReader = BufferedReader(inputStreamReader)
            val json: String = bufferedReader.lines().collect(Collectors.joining())
            val f = File("cache/$fileName")
            Files.createDirectories(Paths.get(f.getParent()))
            val fileWriter = FileWriter(f, false)
            fileWriter.write(json)
            fileWriter.close()
            println("UPDATING: " + f.path)
            println("UPDATED AT: " + LocalDateTime.now())
            println()
            JSONObject(JSONTokener(json))
        } catch (e: IOException) {
            println("FILE IO ERROR OR ERGAST CONNECTION FAILED")
            println("RETRIEVING FROM CACHE\n")
            val f = File("cache/$fileName")
            getJsonFromFile(f)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * Method converting a url endpoint to a filename.
     * @param URL url of Ergast API endpoint.
     * @return String containing a filename.
     */
    private fun getFileNameOfURL(URL: String): String {
        return URL.replace("/+".toRegex(), "_")
            .replace(":".toRegex(), "")
            .replace("\\?".toRegex(), "")
            .replaceFirst("\\.".toRegex(), "")
    }

    /**
     * Method to check whether an update from the api is needed or not.
     * The update interval is set to wait at least 24 hours between updates.
     * @param URL url of Ergast Endpoint.
     * @return true if the cache is out of date.
     */
    fun validUpdate(URL: String): Boolean {
        val fileName = getFileNameOfURL(URL)
        val f = File("cache/$fileName")
        return try {
            if (f.isFile()) {
                val ft: FileTime = Files.getLastModifiedTime(Paths.get(f.path))
                Instant.now().isAfter(ft.toInstant().plus(1, ChronoUnit.DAYS))
            } else {
                true
            }
        } catch (e: IOException) {
            //use ergast for info and try to create/recreate the file;
            true
        }
    }
}
