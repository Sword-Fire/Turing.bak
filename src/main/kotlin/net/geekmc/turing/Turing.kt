package net.geekmc.turing

import net.geekmc.turing.configuration.SettingsConfig
import net.minestom.server.MinecraftServer
import net.minestom.server.extras.bungee.BungeeCordProxy
import net.minestom.server.extras.optifine.OptifineSupport
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Construct
import org.yaml.snakeyaml.constructor.Constructor
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

        if (Files.notExists(Path.of("settings.yml"))) {
            // 加/代表jar根目录，否则需要把settings放到turingserver包下
            saveResource("settings.yml")
        }

        // 设置yaml使用的格式
        val dumperOptions = DumperOptions()
        dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        val yaml = Yaml(dumperOptions)

        val settings = yaml.loadAs(FileInputStream("settings.yml"), SettingsConfig::class.java)
        if (settings.optfine!!) {
            println("Turing >> Optfine support is enabled.")
            OptifineSupport.enable()
        } else {
            println("Turing >> Optfine support is disabled.")
        }

        if (settings.bungeecord!!) {
            println("Turing >> Bungeecord support is enabled.")
            BungeeCordProxy.enable()
        } else {
            println("Turing >> Bungeecord support is disabled.")
        }
        //启动服务器
        minecraftServer.start("0.0.0.0", settings.port!!)

        // 在这之后才能使用协程。

        //        val map: MutableMap<String?, Any?> = yaml.load(FileInputStream("settings.yml"))
//        map["a.233"] = 1
//        map["b"] = object : LinkedHashMap<String?, Any?>() {
//            init {
//                put("next", 2)
//            }
//        }
//        val writer = FileWriter("target.yml")
//        yaml.dump(map, writer)


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

            val f = File(target).absoluteFile
            Files.createDirectories(Path.of(f.parent)) // 创造文件所在文件夹的路径

            // 从jar中获取资源
            // todo 把Turing.class改了
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