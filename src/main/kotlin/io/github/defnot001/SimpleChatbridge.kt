package io.github.defnot001

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.send.AllowedMentions
import com.mojang.logging.LogUtils
import io.github.defnot001.config.Config
import io.github.defnot001.discordbot.MessageReceivedListener
import io.github.defnot001.minecraft.GameMessageHandler
import io.github.defnot001.minecraft.registerStateChangeEvents
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.slf4j.Logger
import java.util.concurrent.TimeUnit

const val MOD_ID = "simple_chatbridge"
val LOGGER: Logger = LogUtils.getLogger()


object SimpleChatbridge : ModInitializer {
	lateinit var config: Config
	lateinit var jda: JDA
		private set
	lateinit var webhookClient: WebhookClient
		private set

	override fun onInitialize() {
		config = Config.loadConfig()
		LOGGER.info("Successfully loaded config file.")

		ServerLifecycleEvents.SERVER_STARTING.register { server ->
			jda = JDABuilder
					.createDefault(config.botToken)
					.setActivity(Activity.watching("Chatbridges"))
					.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.MESSAGE_CONTENT)
					.addEventListeners(MessageReceivedListener(server))
					.build()

			jda.awaitReady()
			LOGGER.info("Successfully initialized Discord Bot.")

			webhookClient = WebhookClientBuilder(config.webhookUrl)
					.setAllowedMentions(AllowedMentions.none())
					.build()
		}

		registerStateChangeEvents()
		GameMessageHandler.registerChatMessageEventListener()

		ServerLifecycleEvents.SERVER_STOPPING.register {
			LOGGER.info("Stopping Discord Bot...")
			jda.shutdown()

			if (!jda.awaitShutdown(10, TimeUnit.SECONDS)) {
				LOGGER.warn("Discord Bot did not shutdown within 10 seconds. Forcing shutdown...")
				jda.shutdownNow()
				jda.awaitShutdown()

				LOGGER.info("Successfully stopped Discord Bot.")
				return@register
			}

			LOGGER.info("Successfully stopped Discord Bot.")
		}
	}
}