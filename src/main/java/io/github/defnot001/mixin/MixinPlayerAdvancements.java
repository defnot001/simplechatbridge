package io.github.defnot001.mixin;

import io.github.defnot001.SimpleChatbridge;
import io.github.defnot001.minecraft.GameMessageHandler;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public class MixinPlayerAdvancements {

    @Shadow
    private ServerPlayer player;

    @Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V"))
    private void award(Advancement advancement, String string, CallbackInfoReturnable<Boolean> cir) {
        if (SimpleChatbridge.config.getBroadcastAdvancements()) {
            var component =  new TranslatableComponent(
                "chat.type.advancement." + advancement.getDisplay().getFrame().getName(), this.player.getDisplayName(), advancement.getChatComponent()
            );

            GameMessageHandler.INSTANCE.postSystemMessageToDiscord(component.getString());
        }
    }
}
