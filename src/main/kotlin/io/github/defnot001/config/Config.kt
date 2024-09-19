package io.github.defnot001.config

import io.github.defnot001.LOGGER
import io.github.defnot001.MOD_ID
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.fabricmc.loader.api.FabricLoader
import java.io.IOException
import kotlin.io.path.createDirectories
import kotlin.io.path.inputStream
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

@Serializable
data class Config(
    val botToken: String = "",
    val webhookUrl: String = "",
    var discordChannelID: String = "",
    var broadcastAdvancements: Boolean = true
) {
    fun getMissingFields(): List<String> {
        val missing = ArrayList<String>()

        if (botToken.isBlank()) {
            missing.add("Missing bot token.")
        }
        if (webhookUrl.isBlank()) {
            missing.add("Missing Discord webhook URL.")
        }
        if (discordChannelID.isBlank()) {
            missing.add("Missing Discord channel ID.")
        }
        return missing
    }

    @OptIn(ExperimentalSerializationApi::class)
    companion object {
        private val modConfigDirPath = FabricLoader.getInstance().configDir.resolve(MOD_ID)
        private val modConfigFilePath = modConfigDirPath.resolve("$MOD_ID.json")
        private val json = Json {
            encodeDefaults = true
            prettyPrint = true
            prettyPrintIndent = "  "
        }

        fun read(): Config {
            if (modConfigFilePath.notExists()) {
                LOGGER.info("Config not found. Creating a default config.")
                return Config().also { write(it) }
            }

            return try {
                modConfigFilePath.inputStream().use { stream ->
                    json.decodeFromStream<Config>(stream)
                }
            } catch (exception: Exception) {
                // IOException or SerializationException
                LOGGER.error("Failed to read the config file. Creating a default config.", exception)
                Config().also { write(it) }
            }
        }

        private fun write(config: Config) {
            try {
                modConfigDirPath.createDirectories()
                modConfigFilePath.outputStream().use { stream ->
                    json.encodeToStream(config, stream)
                }
            } catch (exception: IOException) {
                LOGGER.error("Failed to write config.", exception)
            } catch (exception: SerializationException) {
                LOGGER.error("Failed to serialize config.", exception)
            }
        }
    }
}

