package net.patyhank.fallouthelpermod.screen.hud

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHud

class ExtendChatHud(client: MinecraftClient) : ChatHud(client) {
    val hudID = System.currentTimeMillis()
}
