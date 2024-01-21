package io.github.defnot001.mixin;

import io.github.defnot001.minecraft.GameMessageHandler;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerAdvancements.class)
public class MixinPlayerAdvancements {

    @Shadow
    private ServerPlayer player;

    @Inject(method = "method_53637", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private void award(AdvancementHolder advancementHolder, DisplayInfo displayInfo, CallbackInfo ci) {
        var component = displayInfo.getType().createAnnouncement(advancementHolder, this.player);

        GameMessageHandler.INSTANCE.postSystemMessageToDiscord(component.getString());
    }
}
