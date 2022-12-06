package net.patyhank.fallouthelpermod.chat

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHud
import net.patyhank.fallouthelpermod.event.annotation.EventListener
import net.patyhank.fallouthelpermod.event.type.ServerMessageEvent
import net.patyhank.fallouthelpermod.screen.hud.ExtendChatHud

class ChatChecker(val client: MinecraftClient) {

    companion object {
        var currentCategory: ChatHud? = null
        lateinit var currentCategoryName: String
        private lateinit var systemCategory: ExtendChatHud
        private lateinit var mixedCategory: ExtendChatHud
        val chatRegex = Regex("^\\[(\\S+)] <(\\S+)?-(\\w+)-?(\\[\\S+])*> ([\\S\\s]+)", RegexOption.MULTILINE)
        val chatSort = arrayOf("所有", "領地", "公共", "閒聊", "交易", "抽獎", "系統")
        val msgOut = Regex("^\\[您 -> (\\w+)] ([\\s\\S]+)", RegexOption.MULTILINE)
        val msgIn = Regex("^\\[(\\w+) -> 您] ([\\s\\S]+)", RegexOption.MULTILINE)
        val chatPrefix = mapOf(
            "領地" to "~",
            "公共" to "!",
            "閒聊" to "@",
            "交易" to "$",
            "抽獎" to "%",
        )
    }


    val chatCategory = hashMapOf<String, ExtendChatHud>()

    init {
        systemCategory = ExtendChatHud(client)
        mixedCategory = ExtendChatHud(client)
        chatCategory["公共"] = ExtendChatHud(client)
        chatCategory["閒聊"] = ExtendChatHud(client)
        chatCategory["交易"] = ExtendChatHud(client)
        chatCategory["抽獎"] = ExtendChatHud(client)
        chatCategory["私人"] = ExtendChatHud(client)
        val claimHud = ExtendChatHud(client)
        chatCategory["領地"] = claimHud
        chatCategory["系統"] = systemCategory
        chatCategory["所有"] = mixedCategory
        currentCategory = mixedCategory
        currentCategoryName = "所有"
    }


    @EventListener(true)
    fun handleChat(event: ServerMessageEvent) {
        event.cancel = true
        val messageStr = event.message.string
        if (messageStr.matches(chatRegex)) {
            val matches = chatRegex.find(messageStr)!!
            val category = matches.groupValues[1]
            var chatHud = chatCategory[category]
            if (category == "區域") {
                chatHud = chatCategory["領地"]
            }
            println(chatHud == null)
            chatHud?.addMessage(event.message)
        }
        if (messageStr.startsWith("[系統]")) {
            systemCategory.addMessage(event.message)
        }
        if (messageStr.matches(msgOut) || messageStr.matches(msgIn)) {
            for (mutableEntry in chatCategory) {
                if (mutableEntry.key == "所有") continue
                mutableEntry.value.addMessage(event.message)
            }
        }
        mixedCategory.addMessage(event.message)

    }


}
