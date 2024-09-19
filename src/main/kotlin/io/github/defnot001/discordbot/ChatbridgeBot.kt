package io.github.defnot001.discordbot

import club.minnced.discord.webhook.WebhookClient
import net.dv8tion.jda.api.JDA

class ChatbridgeBot(
    val jda: JDA,
    val webhook: WebhookClient
)