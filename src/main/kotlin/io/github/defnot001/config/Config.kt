package io.github.defnot001.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.defnot001.LOGGER
import io.github.defnot001.MOD_ID
import net.fabricmc.loader.api.FabricLoader
import java.io.IOException
import java.nio.file.Files
import kotlin.reflect.full.memberProperties
import kotlin.system.exitProcess

class Config {
    @JvmField
    var botToken: String? = null

    @JvmField
    var webhookUrl: String? = null

    @JvmField
    var discordChannelID: String? = null

    @JvmField
    var broadcastAdvancements: Boolean? = null

    val safeBotToken: String get() = botToken ?: throw IllegalStateException("Missing Discord Bot Token from Config")
    val safeWebhookUrl: String get() = webhookUrl ?: throw IllegalStateException("Missing Webhook URL from Config")
    val safeDiscordChannelID: String get() = discordChannelID ?: throw IllegalStateException("Missing Discord Channel ID from Config")
    val safeBroadcastAdvancements: Boolean get() = broadcastAdvancements ?: throw IllegalStateException("Missing broadcastAdvancements setting from Config")

    companion object {
        private val modConfigDirPath = FabricLoader.getInstance().configDir.resolve(MOD_ID)
        private val modConfigFilePath = modConfigDirPath.resolve("$MOD_ID.json")
        private val gsonPretty = GsonBuilder().setPrettyPrinting().create()

        fun loadConfig(): Config {
            if (!configExists()) {
                handleMissingConfig()
            }

            val currentConfig = readConfigFromFile()

            checkConfigIntegrity(currentConfig)

            if (!isConfigUpdated(currentConfig)) {
                LOGGER.info("Updating config with new default fields.")
                addNewDefaultsToExistingConfig(currentConfig)
                saveConfig(currentConfig)
                return currentConfig
            }

            return currentConfig
        }

        private fun configExists(): Boolean {
            return Files.exists(modConfigFilePath)
        }

        private fun handleMissingConfig() {
            LOGGER.error("Config file not found. Creating a default config. Please update it before restarting.")
            createDefaultConfigFile()
            exitProcess(0)
        }


        private fun readConfigFromFile(): Config {
            return try {
                Files.newBufferedReader(modConfigFilePath).use { reader ->
                    Gson().fromJson(reader, Config::class.java)
                        ?: throw IOException("Failed to parse config file.")
                }
            } catch (e: IOException) {
                LOGGER.error("Failed to read the config file.", e)
                exitProcess(0)
            }
        }

        private fun getDefaultConfigInstance(): Config {
            return Config().apply {
                botToken = ""
                webhookUrl = ""
                discordChannelID = ""
                broadcastAdvancements = true
            }
        }

        private fun createDefaultConfigFile() {
            try {
                val defaultConfig = getDefaultConfigInstance()
                val json = gsonPretty.toJson(defaultConfig)

                Files.createDirectories(modConfigDirPath)
                Files.newBufferedWriter(modConfigFilePath).use { it.write(json) }
            } catch (e: IOException) {
                LOGGER.error("Could not create default config file.", e)
            }

        }

        private fun isConfigUpdated(currentConfig: Config): Boolean {
            return Config::class.memberProperties.all { property ->
                property.call(currentConfig) != null
            }
        }

        private fun addNewDefaultsToExistingConfig(currentConfig: Config) {
            val currentConfigJson = Gson().toJsonTree(currentConfig).asJsonObject
            val defaultConfigJson = Gson().toJsonTree(getDefaultConfigInstance()).asJsonObject

            defaultConfigJson.entrySet().forEach { (key, value) ->
                if (!currentConfigJson.has(key)) {
                    currentConfigJson.add(key, value)
                }
            }

            Gson().fromJson(currentConfigJson, Config::class.java).apply {
                currentConfig.botToken = this.botToken
                currentConfig.webhookUrl = this.webhookUrl
                currentConfig.discordChannelID = this.discordChannelID
                currentConfig.broadcastAdvancements = this.broadcastAdvancements
            }
        }

        private fun saveConfig(config: Config) {
            try {
                Files.newBufferedWriter(modConfigFilePath).use { writer ->
                    gsonPretty.toJson(config, writer)
                }
            } catch (e: IOException) {
                LOGGER.error("Could not save config file.", e)
                exitProcess(0)
            }
        }

        private fun checkConfigIntegrity(config: Config) {
            val missingConfigs = mutableListOf<String>()

            if (!config.hasBotToken) {
                missingConfigs.add("Bot Token")
                LOGGER.error("Bot Token is missing from the SimpleChatbridge configuration!")
            }

            if (!config.hasWebhookUrl) {
                missingConfigs.add("Webhook URL")
                LOGGER.error("Discord Webhook URL is missing from the SimpleChatbridge configuration!")
            }

            if (!config.hasDiscordChannelID) {
                missingConfigs.add("Channel ID")
                LOGGER.error("Discord Channel ID is missing from the SimpleChatbridge configuration!")
            }

            if (missingConfigs.isNotEmpty()) {
                exitProcess(0)
            }
        }

        private val Config.hasBotToken get() = !this.botToken.isNullOrEmpty()
        private val Config.hasWebhookUrl get() = !this.webhookUrl.isNullOrEmpty()
        private val Config.hasDiscordChannelID get() = !this.discordChannelID.isNullOrEmpty()
     }


}

