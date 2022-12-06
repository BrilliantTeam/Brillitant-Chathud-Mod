package net.patyhank.fallouthelpermod.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.patyhank.fallouthelpermod.chat.ChatChecker;
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

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/util/math/MatrixStack;I)V"))
    private void chatHud(ChatHud instance, MatrixStack matrices, int currentTick) {
        ChatHud hud = ChatChecker.Companion.getCurrentCategory();
        if (hud == null) {
            return;
        }

        hud.render(matrices, currentTick);
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
