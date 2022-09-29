package net.geekmc.turing

data class Configuration(
    var address: String = "0.0.0.0",
    var port: Int = 25565,
    var bungeecord: Boolean = false,
    var optifine: Boolean = true,
    var chunkViewDistance: Int = 16,
    var entityViewDistance: Int = 16,
    var brandName: String = "ExampleBrandName",
    var unknownCommandMessage: String = "未知命令: {cmd}"
)