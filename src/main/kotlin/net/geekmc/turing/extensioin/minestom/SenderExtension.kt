package net.geekmc.turing.extensioin.minestom

import net.minestom.server.command.CommandSender

fun CommandSender.sendColoredMessage(message: String) {
    message.replace("&", "\u00A7").split("\n").forEach {
        this.sendMessage(it)
    }
}