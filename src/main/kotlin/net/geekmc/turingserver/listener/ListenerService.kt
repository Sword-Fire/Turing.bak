package net.geekmc.turingserver.listener

import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode

object ListenerService {

    fun globalNode(): EventNode<Event?> {
        return MinecraftServer.getGlobalEventHandler()
    }

}