package net.geekmc.turing.command

import net.geekmc.turing.instance.InstanceService
import net.minestom.server.command.builder.Command

object SaveCommand : Command("save") {

    init {

        setDefaultExecutor { sender, _ ->
            sender.sendMessage("&c命令用法不正确。")
        }

        addSyntax({ sender, _ ->

            val world = InstanceService.getInstance(InstanceService.MAIN_INSTANCE);
            world.saveInstance()
            sender.sendMessage("成功保存全局数据。")

            val t = System.currentTimeMillis()
            world.saveChunksToStorage()
            sender.sendMessage(
                "成功保存 " + world.chunks.size
                        + "个区块，耗时 " + (System.currentTimeMillis() - t) + "ms。"
            )

        })

    }

}