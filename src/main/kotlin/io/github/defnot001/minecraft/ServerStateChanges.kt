package io.github.defnot001.minecraft

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents

fun registerStateChangeEvents() {
    ServerLifecycleEvents.SERVER_STARTING.register { _ ->
        GameMessageHandler.postSystemMessageToDiscord("Starting Server...")
    }

    ServerLifecycleEvents.SERVER_STARTED.register { _ ->
        GameMessageHandler.postSystemMessageToDiscord("Server started successfully.")
    }

    ServerLifecycleEvents.SERVER_STOPPING.register { _ ->
        GameMessageHandler.postSystemMessageToDiscord("Stopping Server...")
    }

    ServerLifecycleEvents.SERVER_STOPPING.register { _ ->
        GameMessageHandler.postSystemMessageToDiscord("Server stopped successfully.")
    }
}