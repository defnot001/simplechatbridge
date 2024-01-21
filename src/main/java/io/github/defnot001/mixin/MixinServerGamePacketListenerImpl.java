package io.github.defnot001.mixin;

import io.github.defnot001.minecraft.GameMessageHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleChat(Lnet/minecraft/server/network/TextFilter$FilteredText;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastMessage(Lnet/minecraft/network/chat/Component;Ljava/util/function/Function;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V"))
    private void handleChat(TextFilter.FilteredText filteredText, CallbackInfo ci) {
        ServerPlayer player = this.player;
        String message = filteredText.getRaw();

        GameMessageHandler.INSTANCE.postChatMessageToDiscord(player, message);
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onDisconnect(Component component, CallbackInfo ci) {
        GameMessageHandler.INSTANCE.postSystemMessageToDiscord(this.player.getScoreboardName() + " left the game.");
    }
}
