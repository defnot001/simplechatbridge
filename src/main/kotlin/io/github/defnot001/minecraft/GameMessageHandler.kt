package io.github.defnot001.minecraft

import club.minnced.discord.webhook.send.WebhookMessageBuilder
import io.github.defnot001.SimpleChatbridge
import net.minecraft.server.level.ServerPlayer;

object GameMessageHandler {
    fun postChatMessageToDiscord(player: ServerPlayer, message: String) {
        SimpleChatbridge.webhookClient.send(WebhookMessageBuilder().run {
            setUsername(player.scoreboardName)
            setAvatarUrl("https://visage.surgeplay.com/face/256/${player.stringUUID}")
            setContent(message)
            build()
        })
    }

    fun postSystemMessageToDiscord(message: String) {
        SimpleChatbridge.webhookClient.send(message)
    }


}