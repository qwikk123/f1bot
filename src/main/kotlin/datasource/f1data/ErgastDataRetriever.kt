package datasource.f1data

import org.json.JSONObject
import org.json.JSONTokener
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.FileTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Class for retrieving data from the Ergast API
 */
class ErgastDataRetriever {
    /**
     * Method to get json information from the ergast api or the cache
     * @param url url pointing to the Ergast API endpoint.
     * @param validUpdate Whether to use cache or retrieve new data.
     * @return a JSONObject containing Ergast API data.
     */
    fun getJson(url: String, validUpdate: Boolean): JSONObject {
        val fileName = getFileNameOfURL(url)
        val f = File("cache/$fileName")
        if (f.isFile() && !validUpdate) {
            println("Retrieving data from cache")
            return getJsonFromFile(f)
        }
        println("Retrieving data from ergast")
        return getJsonFromURL(url)
    }

    /**
     * Method to check whether an update from the api is needed or not.
     * The update interval is set to wait at least 24 hours between updates.
     * @param url url of Ergast Endpoint.
     * @return true if the cache is out of date.
     */
    fun validUpdate(url: String): Boolean {
        val fileName = getFileNameOfURL(url)
        val f = File("cache/$fileName")
        return try {
            if (f.isFile()) {
                val ft: FileTime = Files.getLastModifiedTime(Paths.get(f.path))
                Instant.now().isAfter(ft.toInstant().plus(1, ChronoUnit.DAYS))
            } else {
                true
            }
        } catch (e: IOException) {
            //use Ergast for info and try to create/recreate the file;
            true
        }
    }

    /**
     * Method to get a JSONObject from the cache
     * @param f Which file to retrieve data from.
     * @return A JSONObject containing Ergast API data.
     */
    private fun getJsonFromFile(f: File): JSONObject = JSONObject(JSONTokener(f.readText()))

    /**
     * Method to get a JSONObject from an Ergast endpoint.
     * @param urlString Which Ergast endpoint to retrieve data from.
     * @return A JSONObject containing Ergast API data.
     */
    private fun getJsonFromURL(urlString: String): JSONObject {
        val fileName = getFileNameOfURL(urlString)
        val connectionTimeout = 2000
        val readTimeout = 2000

        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = connectionTimeout
            connection.readTimeout = readTimeout

            val json: String = connection.inputStream.bufferedReader().use { it.readText() }

            val f = File("cache/$fileName")
            Files.createDirectories(Paths.get(f.parent))
            f.writeText(json)

            println("UPDATING: " + f.path)
            println("UPDATED AT: " + LocalDateTime.now())
            println()

            JSONObject(JSONTokener(json))
        } catch (e: IOException) {
            println(e)
            println(urlString)
            println("FILE IO ERROR OR CONNECTION FAILED")
            println("RETRIEVING FROM CACHE\n")
            val f = File("cache/$fileName")
            getJsonFromFile(f)
        }
    }

    /**
     * Method converting a URL endpoint to a filename.
     * @param url url of Ergast API endpoint.
     * @return String containing a filename.
     */
    private fun getFileNameOfURL(url: String): String {
        return url.replace("/+".toRegex(), "_")
            .replace(":".toRegex(), "")
            .replace("\\?".toRegex(), "")
            .replaceFirst("\\.".toRegex(), "")
    }
}
