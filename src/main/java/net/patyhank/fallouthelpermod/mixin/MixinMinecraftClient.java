package net.patyhank.fallouthelpermod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ChatPreviewWarningScreen;
import net.patyhank.fallouthelpermod.screen.ExtendChatScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "openChatScreen", cancellable = true, at = @At("HEAD"))
    private void openChatScreen(String text, CallbackInfo ci) {
        ci.cancel();
        setScreen(new ExtendChatScreen(text));
    }

    @Inject(method = "setScreen", cancellable = true, at = @At("HEAD"))
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof ChatPreviewWarningScreen) {
            ci.cancel();
        }
    }
}
