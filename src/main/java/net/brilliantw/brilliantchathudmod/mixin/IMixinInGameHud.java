package net.brilliantw.brilliantchathudmod.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InGameHud.class)
public interface IMixinInGameHud {
    @Accessor("chatHud")
    ChatHud getOringalChatHud();
}
