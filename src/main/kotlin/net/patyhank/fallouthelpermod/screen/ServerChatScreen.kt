package net.patyhank.fallouthelpermod.screen

import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class ServerChatScreen(private val safe: Boolean) : ChatScreen("") {
    var server = ""
    val maxServer = 64
    override fun init() {
        super.init()
        chatField = object : TextFieldWidget(textRenderer, 4, height - 12, width - 4, 12, Text.translatable("")) {
            override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
                if (keyCode == 259 && server.isNotEmpty()) {
                    server = server.substring(0, server.length - 1)
                    client?.inGameHud?.setTitle(Text.of("-").copy().fillStyle(Style.EMPTY))
                    client?.inGameHud?.setTitleTicks(0, 20, 40)
                }
                return super.keyPressed(keyCode, scanCode, modifiers)
            }

            override fun write(text: String) {
                text.toIntOrNull() ?: return
                server += text
                val serverNum = (server).toIntOrNull() ?: return
                if ((server).length < 2) {
                    client?.inGameHud?.setTitle(Text.of(server).copy().fillStyle(Style.EMPTY))
                    client?.inGameHud?.setTitleTicks(0, 20, 40)
                    return
                }
                if (serverNum >= maxServer || serverNum < 1) {
                    client?.inGameHud?.clearTitle()
                    client?.inGameHud?.setTitle(Text.of("X").copy().fillStyle(Style.EMPTY.withColor(Formatting.RED)))
                    client?.inGameHud?.setTitleTicks(0, 20, 0)
                    return
                }
                client?.inGameHud?.clearTitle()
                client?.inGameHud?.setTitle(Text.of("OK!").copy().fillStyle(Style.EMPTY.withColor(Formatting.GREEN)))
                client?.inGameHud?.setTitleTicks(0, 20, 0)
                client?.player?.sendCommand((if (safe) "ts " else "server server") + server)
                client?.setScreen(null)
            }
        }
        this.chatField.setMaxLength(256)
        this.chatField.setDrawsBackground(false)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == 259 && server.isNotEmpty()) {
            server = server.substring(0, server.length - 1)
            client?.inGameHud?.setTitle(Text.of("-").copy().fillStyle(Style.EMPTY))
            client?.inGameHud?.setTitleTicks(0, 20, 40)
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true
        }
        if (keyCode == 256) {
            this.client?.setScreen(null)
            return true
        }
        return true
    }

}
