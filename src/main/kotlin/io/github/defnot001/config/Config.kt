package io.github.defnot001.config

import com.google.gson.Gson
import io.github.defnot001.LOGGER
import io.github.defnot001.MOD_ID
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException
import java.nio.file.NoSuchFileException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.system.exitProcess

class Config {
    @JvmField
    var botToken = ""

    @JvmField
    var webhookUrl = ""

    @JvmField
    var discordChannelID = ""

    companion object {
        private val modConfigDirPath = FabricLoader.getInstance().configDir.resolve(MOD_ID)
        private val modConfigFilePath = modConfigDirPath.resolve("$MOD_ID.json")

        fun loadConfig(): Config {
            try {
                Files.newBufferedReader(modConfigFilePath).use { reader ->
                    return Gson().fromJson(reader, Config::class.java)
                            ?: throw IOException("Could not load config file.")
                }
            } catch (e: NoSuchFileException) {
                LOGGER.error("Could not find config file, creating default config instead. Please fill out the config file before restarting.", e)

                createDefaultConfig()
                exitProcess(0)
            } catch (e: IOException) {
                LOGGER.error("Failed to read the config file.", e)
                exitProcess(0)
            }
        }

        private fun createDefaultConfig() {
            try {
                val defaultConfig = Config::class.java.getResourceAsStream("/defaultConfig.json")
                        ?: throw IOException("Could not find default config file.")

                Files.createDirectories(modConfigDirPath)
                Files.copy(defaultConfig, modConfigFilePath, StandardCopyOption.REPLACE_EXISTING)
            } catch (e: IOException) {
                LOGGER.error("Could not create default config file.", e)
            }
        }
    }


}

