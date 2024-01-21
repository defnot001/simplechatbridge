package io.github.defnot001.mixin;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "broadcastMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void broadcastMessage(Component component, ChatType chatType, UUID uUID, CallbackInfo ci) {
        LOGGER.info("Message from Mixin: Component(" + component.getString() + "), ChatType(" + chatType.toString() + ")");
    }
}
