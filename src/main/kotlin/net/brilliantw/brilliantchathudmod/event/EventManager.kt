/*
Code From OKTW Galaxy Project
 */

package net.brilliantw.brilliantchathudmod.event

import kotlinx.coroutines.*
import net.minecraft.util.thread.ThreadExecutor
import net.brilliantw.brilliantchathudmod.MainEntrypoint
import net.brilliantw.brilliantchathudmod.event.annotation.EventListener
import net.brilliantw.brilliantchathudmod.event.type.Event
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName


class EventManager(private val clientThread: ThreadExecutor<*>) :
    CoroutineScope by CoroutineScope(Dispatchers.Default + SupervisorJob()) {
    companion object {
        @JvmStatic
        fun <T : Event> safeEmit(event: T) = MainEntrypoint.entrypoint?.eventManager?.emit(event) ?: event
    }

    private val asyncEventListeners =
        ConcurrentHashMap<KClass<*>, ConcurrentHashMap<Any, CopyOnWriteArrayList<KFunction<*>>>>()
    private val asyncEventCallback = ConcurrentHashMap<KClass<*>, CopyOnWriteArrayList<Function1<Event, Unit>>>()
    private val syncEventListeners = HashMap<KClass<*>, HashMap<Any, ArrayList<KFunction<*>>>>()
    private val syncEventCallback = HashMap<KClass<*>, ArrayList<Function1<Event, Unit>>>()

    fun <T : Event> emit(event: T): T {
        launch {
            asyncEventListeners[event::class]?.forEach { (obj, listeners) ->
                listeners.forEach {
                    it.javaMethod?.invoke(
                        obj,
                        event
                    )
                }
            }
            asyncEventCallback[event::class]?.forEach { it(event) }
        }

        if (!clientThread.isOnThread) {
            runBlocking(clientThread.asCoroutineDispatcher()) {
                syncEventListeners[event::class]?.forEach { (obj, listeners) ->
                    listeners.forEach {
                        it.javaMethod?.invoke(
                            obj,
                            event
                        )
                    }
                }
                syncEventCallback[Event::class]?.forEach { it(event) }
            }
        } else {
            syncEventListeners[event::class]?.forEach { (obj, listeners) ->
                listeners.forEach {
                    it.javaMethod?.invoke(
                        obj,
                        event
                    )
                }
            }
            syncEventCallback[Event::class]?.forEach { it(event) }
        }
        return event
    }

    fun register(obj: Any) {
        obj::class.memberFunctions.forEach {
            val annotation = it.findAnnotation<EventListener>() ?: return@forEach
            val event = it.valueParameters.first().type.jvmErasure

            require(event.isSubclassOf(Event::class)) { "Event register failed, type mismatch: ${event.jvmName}" }

            it.isAccessible = true

            if (annotation.sync) {
                syncEventListeners.getOrPut(event) { hashMapOf() }.getOrPut(obj) { arrayListOf() }.add(it)
            } else {
                asyncEventListeners.getOrPut(event) { ConcurrentHashMap() }.getOrPut(obj) { CopyOnWriteArrayList() }
                    .add(it)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Event> register(event: KClass<T>, listener: (T) -> Unit) {
        if (listener.javaClass.methods.first().getAnnotation(EventListener::class.java)?.sync == true) {
            syncEventCallback.getOrPut(event) { arrayListOf() }.add(listener as (Event) -> Unit)
        } else {
            asyncEventCallback.getOrPut(event) { CopyOnWriteArrayList() }.add(listener as (Event) -> Unit)
        }
    }

    fun <T : Event> unregister(event: KClass<T>, listener: (T) -> Unit) {
        if (listener.javaClass.methods.first().getAnnotation(EventListener::class.java)?.sync == true) {
            syncEventCallback[event]?.remove(listener)
        } else {
            asyncEventCallback[event]?.remove(listener)
        }

        removeEmpty()
    }

    fun unregister(obj: Any) {
        obj::class.memberFunctions.forEach {
            val annotation = it.findAnnotation<EventListener>() ?: return
            val event = it.parameters.first().type.jvmErasure

            if (!event.isSubclassOf(Event::class)) return@forEach

            if (annotation.sync) {
                syncEventListeners[event]?.get(obj)?.remove(it)
            } else {
                asyncEventListeners[event]?.get(obj)?.remove(it)
            }
        }

        removeEmpty()
    }

    private fun removeEmpty() {
        syncEventListeners.entries.removeIf { event ->
            event.value.entries.removeIf { it.value.isEmpty() }
            event.value.isEmpty()
        }
    }
}
