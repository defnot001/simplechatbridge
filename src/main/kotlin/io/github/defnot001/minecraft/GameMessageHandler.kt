package io.github.defnot001.minecraft

import club.minnced.discord.webhook.send.WebhookMessageBuilder
import io.github.defnot001.SimpleChatbridge
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
import net.minecraft.server.level.ServerPlayer;

object GameMessageHandler {
    fun postSystemMessageToDiscord(message: String) {
        SimpleChatbridge.useBot {
            webhook.send(message)
        }
    }

    fun registerChatMessageEventListener() {
        ServerMessageEvents.CHAT_MESSAGE.register { message, sender, _ ->
            postChatMessageToDiscord(sender, message.decoratedContent().string)
        }
    }

    private fun postChatMessageToDiscord(player: ServerPlayer, message: String) {
        SimpleChatbridge.useBot {
            webhook.send(WebhookMessageBuilder().run {
                setUsername(player.scoreboardName)
                setAvatarUrl("https://visage.surgeplay.com/face/256/${player.stringUUID}")
                setContent(message)
                build()
            })
        }
    }
}