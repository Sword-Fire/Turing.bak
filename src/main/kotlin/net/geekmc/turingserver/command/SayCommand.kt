package net.geekmc.turingserver.command

import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.event.EventDispatcher
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.message.ChatPosition
import net.minestom.server.message.Messenger
import net.minestom.server.utils.entity.EntityFinder

object SayCommand : Command("say", "hey") {
    init {

        //ArgumentEntity argPlayer = ArgumentType.Entity("player0").onlyPlayers(true).singleEntity(true);
        defaultExecutor =
            CommandExecutor { sender, _ ->
                sender.sendMessage("&c命令用法不正确。")
            }

        addSyntax(
            { sender: CommandSender, context: CommandContext ->
                val finder = context.get<EntityFinder>("player")
                val p = finder.findFirstPlayer(null, null)
                if (p == null) {
                    sender.sendMessage("can't find player")
                    return@addSyntax
                }
                val playerChatEvent = PlayerChatEvent(
                    p,
                    MinecraftServer.getConnectionManager().onlinePlayers,
                    { Component.text("<" + p.username + "> " + context.get("word")) },
                    context.get("word")
                )
                EventDispatcher.callCancellable(playerChatEvent) {
                    val formatFunction = playerChatEvent.chatFormatFunction
                    val textObject: Component
                    textObject = // Custom format
                        formatFunction?.apply(playerChatEvent) ?: // Default format
                                playerChatEvent.defaultChatFormat.get()
                    val recipients = playerChatEvent.recipients
                    if (!recipients.isEmpty()) {
                        // delegate to the messenger to avoid sending messages we shouldn't be
                        Messenger.sendMessage(recipients, textObject, ChatPosition.CHAT, p.uuid)
                    }
                }
            }, ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true), ArgumentType.Word("word")
        )
    }
}