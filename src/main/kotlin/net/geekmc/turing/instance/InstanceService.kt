package net.geekmc.turing.instance

import net.minestom.server.MinecraftServer
import net.minestom.server.instance.Instance
import net.minestom.server.instance.InstanceManager
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import java.util.HashMap

object InstanceService {
    private lateinit var instanceManager: InstanceManager
    private val map: MutableMap<String, Instance> = HashMap()
    const val MAIN_INSTANCE = "world2"

    fun initialize() {
        instanceManager = MinecraftServer.getInstanceManager()
    }

    fun createInstanceContainer(name: String): Boolean {
        if (map.containsKey(name)) return false
        val instance = instanceManager.createInstanceContainer()
        instance.setGenerator { unit: GenerationUnit ->
            unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK)
        }
        map[name] = instance
        return true
    }

    fun getInstance(instanceName: String): Instance {
        if (!map.containsKey(instanceName)) throw IllegalArgumentException("Instance $instanceName does not exist")
        return map[instanceName]!!
    }
}