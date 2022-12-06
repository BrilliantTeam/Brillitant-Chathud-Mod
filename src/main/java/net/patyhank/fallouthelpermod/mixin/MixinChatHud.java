package net.patyhank.fallouthelpermod.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.patyhank.fallouthelpermod.event.EventManager;
import net.patyhank.fallouthelpermod.event.type.ServerMessageEvent;
import net.patyhank.fallouthelpermod.screen.hud.ExtendChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At("HEAD"), cancellable = true)
    private void addMessage(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
        if ((Object) this instanceof ExtendChatHud) {
            return;
        }
        if (EventManager.safeEmit(new ServerMessageEvent(message)).getCancel()) {
            ci.cancel();
        }
    }


}
