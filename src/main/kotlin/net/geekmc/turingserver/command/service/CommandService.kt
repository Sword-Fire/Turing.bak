package net.geekmc.turingserver.command.service

import net.geekmc.turingserver.command.*
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.utils.callback.CommandCallback

object CommandService {

    fun registerAll() {
        val manager = MinecraftServer.getCommandManager()

        manager.unknownCommandCallback =
            CommandCallback { sender: CommandSender, _: String ->
                sender.sendMessage(
                    "未知命令."
                )
            }

        manager.register(SayCommand)
        manager.register(StopCommand)
        manager.register(TestCommand)
        manager.register(SaveCommand)
        manager.register(GamemodeCommand)

        manager.register(object : Command("kill") {
            init {
                defaultExecutor = CommandExecutor { sender, _ ->
                    sender.sendMessage("你死了")
                }
            }
        })

    }

}