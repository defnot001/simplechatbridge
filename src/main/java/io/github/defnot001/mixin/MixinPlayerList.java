package io.github.defnot001.mixin;

import io.github.defnot001.minecraft.GameMessageHandler;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerList {

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void onPlayerJoin(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci) {
        GameMessageHandler.INSTANCE.postSystemMessageToDiscord(serverPlayer.getScoreboardName() + " joined the game.");
    }
}
