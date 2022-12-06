package net.patyhank.fallouthelpermod.event.annotation

@Target(AnnotationTarget.FUNCTION)
annotation class EventListener(val sync: Boolean = false)
