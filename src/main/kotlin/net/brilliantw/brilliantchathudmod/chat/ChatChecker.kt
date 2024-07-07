package net.brilliantw.brilliantchathudmod.chat

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHud
import net.brilliantw.brilliantchathudmod.event.annotation.EventListener
import net.brilliantw.brilliantchathudmod.event.type.ServerMessageEvent
import net.brilliantw.brilliantchathudmod.screen.hud.ExtendChatHud

class ChatChecker(val client: MinecraftClient) {

    companion object {
        var currentCategory: ChatHud? = null
        lateinit var currentCategoryName: String
        private lateinit var systemCategory: ExtendChatHud
        private lateinit var mixedCategory: ExtendChatHud
        val chatRegex = Regex("｜(\\S{2})｜(?:(\\S{2})｜|)\\S+(?:：|\\s->\\s)[\\S\\s]+", RegexOption.MULTILINE)
        val replyRegex = Regex("｜(\\S{2})｜\\s+┌──\\s\\[(\\S{2})]\\s\\[\\S+]\\s[\\S\\s]+\n", RegexOption.MULTILINE)
        val chatSort = arrayOf("所有", "聊天", "交易", "區域", "私訊", "系統")
        val chatPrefix = mapOf(
            "聊天" to "!",
            "交易" to "?",
            "區域" to "-"
        )
    }


    val chatCategory = hashMapOf<String, ExtendChatHud>()

    init {
        systemCategory = ExtendChatHud(client)
        mixedCategory = ExtendChatHud(client)
        chatCategory["聊天"] = ExtendChatHud(client)
        chatCategory["交易"] = ExtendChatHud(client)
        chatCategory["區域"] = ExtendChatHud(client)
        chatCategory["私訊"] = ExtendChatHud(client)
        chatCategory["系統"] = systemCategory
        chatCategory["所有"] = mixedCategory
        currentCategory = mixedCategory
        currentCategoryName = "所有"
    }


    @EventListener(true)
    fun handleChat(event: ServerMessageEvent) {
        event.cancel = true
        val messageStr = event.message.string
        println(messageStr)
        if (messageStr.matches(chatRegex)) {
            val matches = chatRegex.find(messageStr)!!
            val category = matches.groupValues[1]
            val chatHud = chatCategory[category]
            println(chatHud == null)
            println()
            chatHud?.addMessage(event.message)

            if (category == "私訊") {
                for (mutableEntry in chatCategory) {
                    if (mutableEntry.key == "所有" || mutableEntry.key == "私訊") continue
                    mutableEntry.value.addMessage(event.message)
                }
            }
        } else if (messageStr.matches(replyRegex)) {
            chatCategory["聊天"]?.addMessage(event.message)
        }

        mixedCategory.addMessage(event.message)
    }


}
