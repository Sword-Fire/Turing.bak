package net.geekmc.turing.motd

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.ping.ResponseData
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

object MotdService {

    lateinit var motdData: ResponseData

    /**
     * Load the icon from the motd.png file and set the ping text,
     * which will be sent to player when they ping the server.
     */
    fun registerMotdListener() {

        motdData = ResponseData()
        motdData.description = Component.text()
            .append(
                Component.text("               Turing Server\n")
                    .color(NamedTextColor.RED)
            )
            .append(
                Component.text("              click to join!")
                    .color(NamedTextColor.AQUA)
            )
            .build()
        motdData.favicon = getIconBase64()

        MinecraftServer.getGlobalEventHandler().addListener(ServerListPingEvent::class.java) {
            it.responseData = motdData
        }
    }

    /**
     * Get the base64 encoded icon ,which could be directly sent to player.
     * @return base64 encoded icon with the Mojang-assigned prefix,"" if the icon is not found or could not be encoded.
     */
    private fun getIconBase64(): String {
        val sep = File.separator
        val path = "." + sep + "motd" + sep + "icon.png"
        if (!File(path).exists()) return ""
        try {
            val bytes = Files.readAllBytes(Paths.get(path))
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }
}