package net.patyhank.fallouthelpermod

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.MinecraftClient
import net.patyhank.fallouthelpermod.chat.ChatChecker
import net.patyhank.fallouthelpermod.event.EventManager
import kotlin.coroutines.CoroutineContext

class FalloutHelperMod : ClientModInitializer, CoroutineScope {
    private val job = SupervisorJob()
    lateinit var client: MinecraftClient
        private set
    lateinit var eventManager: EventManager
        private set
    lateinit var chatChecker: ChatChecker
        private set
    override val coroutineContext: CoroutineContext
        get() = job + client.asCoroutineDispatcher()

    companion object {
        var helper: FalloutHelperMod? = null
            private set
    }

    override fun onInitializeClient() {
        helper = this
        ClientLifecycleEvents.CLIENT_STARTED.register(ClientLifecycleEvents.ClientStarted {
            client = it
            eventManager = EventManager(client)
            chatChecker = ChatChecker(client)
            eventManager.register(chatChecker)
        })

        ClientLifecycleEvents.CLIENT_STOPPING.register(ClientLifecycleEvents.ClientStopping {
            job.complete()
        })
    }
}
