package io.github.defnot001.discordbot

import io.github.defnot001.SimpleChatbridge
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer

class MessageReceivedListener(private val server: MinecraftServer) : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        if (event.channel.id != SimpleChatbridge.config.discordChannelID) return
        if (event.isWebhookMessage) return


        if (event.message.hasAttachment) {
            sendIngameNotificationAttachment(event.author.name, event.message)
        }

        sendIngameMessage(event.author.name, event.message.contentDisplay)
    }

    private fun sendIngameMessage(author: String, content: String) {
        val message = formatChatMessage(author, content)

        if (content.isNotEmpty()) {
            server.execute {
                server.playerList.broadcastSystemMessage(message, false)
            }
        }
    }

    private fun sendIngameNotificationAttachment(author: String, message: Message) {
        val attachmentMessage = if (message.hasMultipleAttachments) "multiple attachments" else "an attachment"

        server.execute {
            server.playerList.broadcastSystemMessage(
                Component.literal("$author posted $attachmentMessage in Discord.")
                    .withStyle(ChatFormatting.GRAY),
                false
            )
        }
    }

    private fun formatChatMessage(author: String, content: String): Component {
        return Component.literal("[")
                .append(Component.literal(author).withStyle { it.withColor(ChatFormatting.GRAY) })
                .append(Component.literal("] $content"))
    }

    private val Message.hasAttachment get() = this.attachments.isNotEmpty()

    private val Message.hasMultipleAttachments get() = this.attachments.size > 1
}