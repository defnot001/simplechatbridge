package io.github.defnot001

import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.send.AllowedMentions
import com.mojang.logging.LogUtils
import io.github.defnot001.config.Config
import io.github.defnot001.discordbot.ChatbridgeBot
import io.github.defnot001.discordbot.MessageReceivedListener
import io.github.defnot001.minecraft.GameMessageHandler
import io.github.defnot001.minecraft.registerStateChangeEvents
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.exceptions.InvalidTokenException
import net.dv8tion.jda.api.requests.GatewayIntent
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.slf4j.Logger
import java.util.concurrent.TimeUnit

const val MOD_ID = "simple_chatbridge"
val LOGGER: Logger = LogUtils.getLogger()

object SimpleChatbridge : ModInitializer {
	@Suppress("JoinDeclarationAndAssignment")
	@JvmField
	val config: Config
	val enabled: Boolean
		get() = bot != null

	private val bot: ChatbridgeBot?

	init {
		// This will be called when onInitialize is invoked
		config = Config.read()
		LOGGER.info("Successfully loaded config file.")

		bot = createBot()
		if (bot == null) {
			LOGGER.info("Chatbrige is disabled.")
		}
	}

	override fun onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register { server ->
			useBot {
				jda.addEventListener(MessageReceivedListener(server))
				jda.awaitReady()

				LOGGER.info("Successfully initialized Discord Bot.")
			}
		}

		registerStateChangeEvents()
		GameMessageHandler.registerChatMessageEventListener()

		ServerLifecycleEvents.SERVER_STOPPED.register {
			useBot {
				LOGGER.info("Stopping Discord Bot...")
				jda.shutdown()
				webhook.close()

				if (!jda.awaitShutdown(10, TimeUnit.SECONDS)) {
					LOGGER.warn("Discord Bot did not shutdown within 10 seconds. Forcing shutdown...")
					jda.shutdownNow()
					jda.awaitShutdown()
				}

				LOGGER.info("Successfully stopped Discord Bot.")
			}
		}
	}

	fun useBot(block: ChatbridgeBot.() -> Unit) {
		bot?.apply(block)
	}

	private fun createBot(): ChatbridgeBot? {
		val missing = config.getMissingFields()
		if (missing.isNotEmpty()) {
			LOGGER.error("Failed to load bot, invalid configuration:")
			for (missed in missing) {
				LOGGER.error(missed)
			}
			return null
		}

		val jda = try {
			JDABuilder.createDefault(config.botToken).apply {
				setActivity(Activity.watching("Chatbridges"))
				enableIntents(
					GatewayIntent.GUILD_MESSAGES,
					GatewayIntent.GUILD_WEBHOOKS,
					GatewayIntent.MESSAGE_CONTENT
				)
			}.build()
		} catch (exception: InvalidTokenException) {
			LOGGER.error("Failed to load bot, invalid bot token.")
			return null
		}

		val webhook = try {
			WebhookClientBuilder(config.webhookUrl).apply {
				setAllowedMentions(AllowedMentions.none())
			}.build()
		} catch (exception: IllegalArgumentException) {
			LOGGER.error("Failed to load bot, invalid webhook url.")
			return null
		}
		return ChatbridgeBot(jda, webhook)
	}
}