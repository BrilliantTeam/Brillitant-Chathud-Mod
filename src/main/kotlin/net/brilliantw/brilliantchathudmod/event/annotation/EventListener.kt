package net.brilliantw.brilliantchathudmod.event.annotation

@Target(AnnotationTarget.FUNCTION)
annotation class EventListener(val sync: Boolean = false)
