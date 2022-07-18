package net.geekmc.turingserver

import net.geekmc.turingserver.command.service.CommandService
import net.geekmc.turingserver.instance.InstanceService
import net.geekmc.turingserver.listener.ListenerService
import net.geekmc.turingserver.motd.MotdService
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.entity.EntityTickEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.extras.optifine.OptifineSupport
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import world.cepi.kstom.Manager
import world.cepi.kstom.event.listen
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.nio.file.*


object TuringServer {

    lateinit var minecraftServer: MinecraftServer

    @JvmStatic
    fun main(args: Array<String>) {

        minecraftServer = MinecraftServer.init()

        // Register Instance
        InstanceService.initialize()
        InstanceService.createInstanceContainer(InstanceService.MAIN_INSTANCE)
        val world = InstanceService.getInstance(InstanceService.MAIN_INSTANCE)

        // Register listeners
        ListenerService.globalNode().addListener(
            PlayerLoginEvent::class.java
        ) { e ->
            val p = e.player

            e.setSpawningInstance(world)
            p.respawnPoint = Pos(0.0, 40.0, 0.0)
            p.sendMessage("Welcome to server, " + p.username + " !")
        }

        Manager.globalEvent.listen<EntityTickEvent> {
            expireCount = 50
            removeWhen { entity.isCustomNameVisible }
            filters += { entity.isGlowing }
            handler {
                entity.setGravity(5.0, .5)
            }
        }

        OptifineSupport.enable()

        // Register commands
        CommandService.registerAll()

        // Register motd
        MotdService.registerMotdListener()
        MinecraftServer.setBrandName("GeekMC")

        if (Files.notExists(Path.of("settings.yml"))) {
            // 加/代表jar根目录，否则需要把settings放到turingserver包下
            saveResource("settings.yml")
        }

        // 设置yaml使用的格式
        val dumperOptions = DumperOptions()
        dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        val yaml = Yaml(dumperOptions)

        val map: MutableMap<String?, Any?> = yaml.load(FileInputStream("settings.yml"))
        map["a.233"] = 1
        map["b"] = object : LinkedHashMap<String?, Any?>() {
            init {
                put("next", 2)
            }
        }
        val writer = FileWriter("target.yml")
        println("changed to block")
        yaml.dump(map, writer)
        println("successfully dump data to target.yml")
        //启动服务器
        minecraftServer.start("0.0.0.0", 25565)

    }


    fun saveResource(resourcePath: String) {
        saveResource(resourcePath, resourcePath, false)
    }

    fun saveResource(resourcePath: String, replace: Boolean) {
        saveResource(resourcePath, resourcePath, replace)
    }

    fun saveResource(resourcePath: String, filePath: String) {
        saveResource(resourcePath, filePath, false)
    }

    fun saveResource(source: String, target: String, replace: Boolean) {
        try {
            val f = File(target).absoluteFile //一定要abs

            Files.createDirectories(Path.of(f.parent))

            val inputStream = TuringServer::class.java.getResourceAsStream(source)
                ?: throw IllegalArgumentException(
                    "Resource $source can't be found in " + TuringServer::class.java.getResource(
                        ""
                    )
                )
            // 按理说这不会发生
            if (replace) {
                Files.copy(inputStream, Paths.get(target), StandardCopyOption.REPLACE_EXISTING)
            } else {
                try {
                    Files.copy(inputStream, Paths.get(target))
                } catch (e: FileAlreadyExistsException) {
                    e.printStackTrace()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}