package net.patyhank.fallouthelpermod.mixin;


import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.patyhank.fallouthelpermod.screen.ServerChatScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = {"onKey"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (this.client.currentScreen != null) {
            return;
        }
        InputUtil.Key key1 = InputUtil.fromKeyCode(key, scancode);
        if (key1.getCategory() == InputUtil.Type.KEYSYM && key1.getCode() == 334) {
            this.client.setScreenAndRender(new ServerChatScreen(false));
            ci.cancel();
        }
    }
}
