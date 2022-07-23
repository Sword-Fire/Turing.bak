package net.geekmc.turing.manager

import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.instance.InstanceManager
import net.minestom.server.monitoring.BenchmarkManager
import net.minestom.server.timer.SchedulerManager

object TuringManager {

    val globalNode: GlobalEventHandler
        get() = MinecraftServer.getGlobalEventHandler()

    val scheduler: SchedulerManager
        get() = MinecraftServer.getSchedulerManager()

    val instance: InstanceManager
        get() = MinecraftServer.getInstanceManager()

    val benchmark: BenchmarkManager
        get() = MinecraftServer.getBenchmarkManager()

}