package net.geekmc.turing.command

import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext

object StopCommand :Command("stop"){

    init {
        setDefaultExecutor { sender, _ ->
            sender.sendMessage("&c触发了默认命令。")
        }

        addSyntax({ sender: CommandSender, _: CommandContext ->
            MinecraftServer.stopCleanly()
            sender.sendMessage("&c成功停止服务器。")
        })

    }

}