package net.brilliantw.brilliantchathudmod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.brilliantw.brilliantchathudmod.chat.ChatChecker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Shadow
    @Final
    private ChatHud chatHud;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;III)V"))
    private void chatHud(ChatHud instance, DrawContext context, int currentTick, int x, int y) {
        ChatHud hud = ChatChecker.Companion.getCurrentCategory();
        if (hud == null) {
            return;
        }

        hud.render(context, currentTick, x, y);
    }


    @Inject(method = "getChatHud", at = @At("HEAD"), cancellable = true)
    private void getChatUI(CallbackInfoReturnable<ChatHud> cir) {
        ChatHud currentCategory = ChatChecker.Companion.getCurrentCategory();
        if (currentCategory != null) {
            cir.setReturnValue(currentCategory);
        } else {
            cir.setReturnValue(chatHud);
        }
    }

}
