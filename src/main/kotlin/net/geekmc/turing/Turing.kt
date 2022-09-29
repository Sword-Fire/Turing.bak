package net.geekmc.turing

import net.minestom.server.MinecraftServer
import net.minestom.server.extras.bungee.BungeeCordProxy
import net.minestom.server.extras.optifine.OptifineSupport
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.*

@Suppress("DEPRECATION")
object Turing {

    val LOGGER = LoggerFactory.getLogger(Turing::class.java)!!

    private lateinit var server: MinecraftServer

    @JvmStatic
    fun main(args: Array<String>) {
        server = MinecraftServer.init()
        if (Files.notExists(Path.of("config.yml"))) {
            saveResource("/config.yml", "config.yml")
        }
        val dumperOptions = DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        }
        val yaml = Yaml(dumperOptions)
        val config = yaml.loadAs(FileInputStream("config.yml"), Configuration::class.java)

        when {
            config.bungeecord -> {
                info("BungeeCord support enabled.")
                BungeeCordProxy.enable()
            }
            config.optifine -> {
                @Suppress("SpellCheckingInspection")
                info("Optifine support enabled.")
                OptifineSupport.enable()
            }
        }

        MinecraftServer.setBrandName(config.brandName)
        MinecraftServer.setChunkViewDistance(config.chunkViewDistance)
        MinecraftServer.setEntityViewDistance(config.entityViewDistance)

        server.start(config.address, config.port)
    }

    private fun saveResource(source: String, target: String, replace: Boolean = false) {
        try {
            val file = File(target).absoluteFile
            // 创造文件所在文件夹的路径。
            Files.createDirectories(Path.of(file.parent))
            // 从 Jar 中获取资源，
            // 若改成 getClassLoader()，则会以 Turing.jar 为原始文件坐标。
            val inputStream = Turing::class.java.getResourceAsStream(source) ?: kotlin.run {
                throw IllegalArgumentException("Resource $source can't be found in " + Turing::class.java.getResource(""))
            }
            // 按理说这不会发生。
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