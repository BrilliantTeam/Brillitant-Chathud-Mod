package net.patyhank.fallouthelpermod.event.type

open class CancelableEvent : Event {
    var cancel = false
        set(cancel) {
            if (!this.cancel) field = cancel
        }
}
