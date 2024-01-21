package io.github.defnot001.discordbot

import io.github.defnot001.SimpleChatbridge
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.minecraft.ChatFormatting
import net.minecraft.Util
import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.MinecraftServer

class MessageReceivedListener(private val server: MinecraftServer) : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        if (event.channel.id != SimpleChatbridge.config.discordChannelID) return
        if (event.isWebhookMessage) return

        sendIngameMessage(event.author.name, event.message.contentDisplay)
    }

    private fun sendIngameMessage(author: String, content: String) {
        val message = formatChatMessage(author, content)

        server.execute {
            server.playerList.broadcastMessage(message, ChatType.CHAT, Util.NIL_UUID)
        }
    }

    private fun formatChatMessage(author: String, content: String): Component {
        return TextComponent("[")
                .append(TextComponent(author).withStyle { it.withColor(ChatFormatting.GRAY) })
                .append(TextComponent("] $content"))
    }
}