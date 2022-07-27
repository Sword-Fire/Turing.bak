package net.geekmc.turing

import kotlinx.coroutines.*
import net.geekmc.turing.command.service.CommandService
import net.geekmc.turing.coroutine.Ticking
import net.geekmc.turing.instance.InstanceService
import net.geekmc.turing.motd.MotdService
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.player.PlayerSpawnEvent
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


object Turing {

    lateinit var minecraftServer: MinecraftServer

    @JvmStatic
    fun main(args: Array<String>) {

        minecraftServer = MinecraftServer.init()

        //Manager.dimensionType.

        // Register Instance
        InstanceService.initialize()
        InstanceService.createInstanceContainer(InstanceService.MAIN_INSTANCE)
        val world = InstanceService.getInstance(InstanceService.MAIN_INSTANCE)

        // Register listeners
        Manager.globalEvent.addListener(PlayerLoginEvent::class.java) {
            val p = it.player

            it.setSpawningInstance(world)
            p.respawnPoint = Pos(0.0, 40.0, 0.0)
            p.sendMessage("Welcome to server, " + p.username + " !")
        }

        // 应该放PlayerJoin Event 异步获取skin然后主线程setSkin
        // init skin
//        Manager.globalEvent.listen<PlayerSkinInitEvent> {
//
//            handler {
//                skin = PlayerSkin.fromUsername(player.username)
//            }
//        }

        Manager.globalEvent.listen<PlayerSpawnEvent> {
            handler {
                if (!isFirstSpawn) return@handler

                val scope = CoroutineScope(Dispatchers.IO)
                scope.launch {

                    //println("*${Thread.currentThread().name} get skin from mojang")
                    delay(3000)
                    val skin = withContext(Dispatchers.IO) {
                        PlayerSkin.fromUsername(player.username)
                    }
                    //println("finish get skin at ${System.currentTimeMillis()}")

                    // 切到主线程设置玩家皮肤
                    withContext(Dispatchers.Ticking) {
                        delay(100)
                        println("${Thread.currentThread().name} set skin")
                        player.skin = skin
                    }
                    println("${Thread.currentThread().name} coroutine end")

                }

            }
        }

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            println("hello")
        }
        scope.cancel()

        val job=GlobalScope.launch {
            println("hello")
        }


        job.cancel()

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

//         BungeeCordProxy.enable()
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

            val inputStream = Turing::class.java.getResourceAsStream(source)
                ?: throw IllegalArgumentException(
                    "Resource $source can't be found in " + Turing::class.java.getResource(
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