package net.patyhank.fallouthelpermod.screen

import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.patyhank.fallouthelpermod.FalloutHelperMod
import net.patyhank.fallouthelpermod.chat.ChatChecker

class ExtendChatScreen(originalChatText: String?) : ChatScreen(originalChatText) {
    private var btns = arrayListOf<ButtonWidget>()

    override fun init() {
        btns = arrayListOf()
        val chatCategory = FalloutHelperMod.helper?.chatChecker?.chatCategory
        var i = 0
        ChatChecker.chatSort.forEach { key ->
            val hud = chatCategory?.get(key)!!

            btns.add(ButtonWidget(8 + (i * 30), height - 36, 30, 12, Text.literal(key).apply {
                if (key == ChatChecker.currentCategoryName) style = Style.EMPTY.withColor(Formatting.DARK_GREEN)
            }) {
                ChatChecker.currentCategory = hud
                ChatChecker.currentCategoryName = key
                for (btn in btns) {
                    btn.message = btn.message.copy().apply {
                        style = Style.EMPTY.withColor(Formatting.WHITE)
                    }
                }
                it.message = Text.literal(key).fillStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN))
            }.apply {
                visible = true
            })
            i++
        }
        for (btn in btns) {
            addDrawableChild(btn)
        }
        super.init()
    }

    override fun sendMessage(chatText: String, addToHistory: Boolean): Boolean {
        if (chatText.isEmpty()) {
            return true
        }
        if (chatText[0].toString() == "/") {
            return super.sendMessage(chatText, addToHistory)
        }
        val prefix = ChatChecker.chatPrefix[ChatChecker.currentCategoryName]
            ?: return if (ChatChecker.chatPrefix.values.contains(chatText[0].toString())) {
                val first = ChatChecker.chatPrefix.filter { it.value == chatText[0].toString() }.keys.first()
                val chatCategory = FalloutHelperMod.helper?.chatChecker?.chatCategory
                ChatChecker.currentCategory = chatCategory?.get(first)!!
                ChatChecker.currentCategoryName = first
                super.sendMessage(chatText, addToHistory)
            } else {
                if (ChatChecker.currentCategoryName == "所有") {
                    return super.sendMessage(chatText, addToHistory)
                }
                val style = Style.EMPTY.apply {
                    withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("試著在訊息前加上頻道")))
                }
                ChatChecker.currentCategory?.addMessage(Text.literal("§c無法傳送訊息! 無前綴且不在可傳送頻道!").apply {
                }.setStyle(style))
                true
            }
        return super.sendMessage(prefix + chatText, addToHistory)
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
    }
}
