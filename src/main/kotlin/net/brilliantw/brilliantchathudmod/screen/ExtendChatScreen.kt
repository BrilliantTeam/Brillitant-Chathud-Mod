package net.brilliantw.brilliantchathudmod.screen

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.brilliantw.brilliantchathudmod.MainEntrypoint
import net.brilliantw.brilliantchathudmod.chat.ChatChecker

class ExtendChatScreen(originalChatText: String?) : ChatScreen(originalChatText) {
    private var btns = arrayListOf<ButtonWidget>()

    override fun init() {
        btns = arrayListOf()
        val chatCategory = MainEntrypoint.entrypoint?.chatChecker?.chatCategory
        var i = 0
        ChatChecker.chatSort.forEach { key ->
            val hud = chatCategory?.get(key)!!

            val widget = ButtonWidget.builder(Text.literal(key).apply {
                if (key == ChatChecker.currentCategoryName) style = Style.EMPTY.withColor(Formatting.DARK_GREEN)
            }) { wid: ButtonWidget ->
                ChatChecker.currentCategory = hud
                ChatChecker.currentCategoryName = key
                for (btn in btns) {
                    btn.message = btn.message.copy().apply {
                        style = Style.EMPTY.withColor(Formatting.WHITE)
                    }
                }
                wid.message = Text.literal(key).fillStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN))
                focused = chatField
            }
                .size(30, 12)
                .position(8 + (i * 30), height - 36)
                .build()
                .apply {
                    visible = true
                }

            btns.add(widget)
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
                val chatCategory = MainEntrypoint.entrypoint?.chatChecker?.chatCategory
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

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        if (focused != chatField)
            chatField.charTyped(chr, modifiers);
        return super.charTyped(chr, modifiers)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (focused != chatField)
            chatField.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}
