package net.brilliantw.brilliantchathudmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MessageHandler.class)
public class MixinMessageHandler {
    @Shadow
    @Final
    private MinecraftClient client;

    @Redirect(method = "processChatMessageInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V"))
    private void procMsg(ChatHud instance, Text message, MessageSignatureData signature, MessageIndicator indicator) {
        ((IMixinInGameHud) client.inGameHud).getOringalChatHud().addMessage(message, signature, indicator);
    }

    @Redirect(method = "onGameMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V"))
    private void procGameMsg(ChatHud instance, Text message) {
        ((IMixinInGameHud) client.inGameHud).getOringalChatHud().addMessage(message);
    }
}
