package net.geekmc.turing

import net.geekmc.turing.configuration.ConfigFile
import net.minestom.server.MinecraftServer
import net.minestom.server.extras.bungee.BungeeCordProxy
import net.minestom.server.extras.optifine.OptifineSupport
import net.minestom.server.utils.callback.CommandCallback
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import world.cepi.kstom.Manager
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.*


object Turing {

    private lateinit var server: MinecraftServer
    private val LOGGER = LoggerFactory.getLogger(Turing::class.java)

    @JvmStatic
    fun main(args: Array<String>) {

        server = MinecraftServer.init()

        if (Files.notExists(Path.of("config.yml"))) {
            // 加/代表jar根目录，否则需要把settings放到turingserver包下
            saveResource("/configurations/config.yml", "config.yml")
        }

        // 设置yaml使用的格式
        val dumperOptions = DumperOptions()
        dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        val yaml = Yaml(dumperOptions)

        val settings = yaml.loadAs(FileInputStream("config.yml"), ConfigFile::class.java)

        // bungeecord
        if (settings.bungeecord) {
            LOGGER.info("Bungeecord support is enabled.")
            BungeeCordProxy.enable()
        } else {
            LOGGER.info("Bungeecord support is disabled.")
        }

        // optfine
        if (settings.optfine) {
            LOGGER.info("Optfine support is enabled.")
            OptifineSupport.enable()
        } else {
            LOGGER.info("Optfine support is disabled.")
        }

        // brand name
        MinecraftServer.setBrandName(settings.brandName)


        @Suppress("deprecation")
        MinecraftServer.setChunkViewDistance(settings.chunkViewDistance)
        @Suppress("deprecation")
        MinecraftServer.setEntityViewDistance(settings.entityViewDistance)

        //启动服务器
        server.start(settings.address, settings.port)

        // 在这之后才能使用协程。

//        val map: MutableMap<String?, Any?> = yaml.load(FileInputStream("config.yml"))
//        for ((index, value) in map) {
//            LOGGER.info("$index -> ${value?.javaClass} $value")
//        }

//        map["a.233"] = 1
//        map["b"] = object : LinkedHashMap<String?, Any?>() {
//            init {
//                put("next", 2)
//            }
//        }
//        val writer = FileWriter("target.yml")
//        yaml.dump(map, writer)

    }

    fun saveResource(source: String, target: String, replace: Boolean = false) {
        try {

            val f = File(target).absoluteFile
            Files.createDirectories(Path.of(f.parent)) // 创造文件所在文件夹的路径

            // 从jar中获取资源
            // 若改成java.getClassLoader()，则会以Turing.jar为原始文件坐标
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