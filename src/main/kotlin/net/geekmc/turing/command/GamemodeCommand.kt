package net.geekmc.turing.command

import net.geekmc.turing.extensioin.minestom.sendColoredMessage
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.utils.entity.EntityFinder

object GamemodeCommand : Command("gamemode", "gm") {


    private fun sendUsageMessage(sender: CommandSender) =
        sender.sendMessage("正确用法: /gamemode ( 0 | 1 | 2 | 3 | SURVIVAL | CREATIVE | ADVENTURE | SPECTATOR ) [玩家名字]")

    private val gameModeArgument =
        ArgumentType.Word("mode").from("0", "1", "2", "3", "SURVIVAL", "CREATIVE", "ADVENTURE", "SPECTATOR")
    private val targetArgument: ArgumentEntity = ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true)


    private fun setGameMode(sender: CommandSender, player: Player, mode: String) {
        when (mode.uppercase()) {
            "0", "SURVIVAL" -> {
                player.gameMode = GameMode.SURVIVAL
                sender.sendMessage("已将玩家 ${player.username} 的游戏模式设置为 SURVIVAL")
            }
            "1", "CREATIVE" -> {
                player.gameMode = GameMode.CREATIVE
                sender.sendMessage("已将玩家 ${player.username} 的游戏模式设置为 CREATIVE")
            }
            "2", "ADVENTURE" -> {
                player.gameMode = GameMode.ADVENTURE
                sender.sendMessage("已将玩家 ${player.username} 的游戏模式设置为 ADVENTURE")
            }
            "3", "SPECTATOR" -> {
                player.gameMode = GameMode.SPECTATOR
                sender.sendMessage("已将玩家 ${player.username} 的游戏模式设置为 SPECTATOR")
            }
            else -> throw IllegalArgumentException("未知的游戏模式 $mode")
        }
    }

    init {
        defaultExecutor = CommandExecutor { sender, _ ->
            sendUsageMessage(sender)
        }

        addSyntax({ sender, context ->
            if (sender !is Player) {
                sender.sendColoredMessage("&c只有玩家能使用这个命令")
                return@addSyntax
            }
            setGameMode(sender, sender, context.get("mode"))
        }, gameModeArgument)

        addSyntax({ sender, context ->

            val finder: EntityFinder = context.get("player")

            val player: Player = finder.findFirstPlayer(null, null) ?: run { //尝试搜索输入的玩家名
                sender.sendColoredMessage("&c未找到玩家 ${context.getRaw("player")}")
                return@addSyntax
            }

            setGameMode(sender, player, context.get("mode"))

        }, gameModeArgument, targetArgument)

    }
}