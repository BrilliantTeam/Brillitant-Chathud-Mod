package net.brilliantw.brilliantchathudmod

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.MinecraftClient
import net.brilliantw.brilliantchathudmod.chat.ChatChecker
import net.brilliantw.brilliantchathudmod.event.EventManager
import kotlin.coroutines.CoroutineContext

class MainEntrypoint : ClientModInitializer, CoroutineScope {
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
        var entrypoint: MainEntrypoint? = null
            private set
    }

    override fun onInitializeClient() {
        entrypoint = this
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
