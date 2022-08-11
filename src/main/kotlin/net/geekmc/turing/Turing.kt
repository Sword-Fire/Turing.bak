package net.geekmc.turing

import net.geekmc.turing.configuration.SettingsConfig
import net.minestom.server.MinecraftServer
import net.minestom.server.extras.bungee.BungeeCordProxy
import net.minestom.server.extras.optifine.OptifineSupport
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
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

        // optimize support
        OptifineSupport.enable()

        // BungeeCord support
        BungeeCordProxy.enable()

        if (Files.notExists(Path.of("settings.yml"))) {
            // 加/代表jar根目录，否则需要把settings放到turingserver包下
            saveResource("settings.yml")
        }

        // 设置yaml使用的格式
        val dumperOptions = DumperOptions()
        dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        val yaml = Yaml(dumperOptions)

//        val map: MutableMap<String?, Any?> = yaml.load(FileInputStream("settings.yml"))
//        map["a.233"] = 1
//        map["b"] = object : LinkedHashMap<String?, Any?>() {
//            init {
//                put("next", 2)
//            }
//        }
//        val writer = FileWriter("target.yml")
//        yaml.dump(map, writer)

        val settings: SettingsConfig = yaml.load(FileInputStream("settings.yml"))
        println("port: ${settings.port}")
        println("bungeecord: ${settings.bungeecord}")
        println("list: ${settings.list}")

        //启动服务器
        minecraftServer.start("0.0.0.0", 25565)

        // 在这之后才能使用协程。

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