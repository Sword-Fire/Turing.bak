package net.geekmc.turing.configuration

// values here will be the default values,if the responding key doesn't exist in config.yml
data class ConfigFile(
    var address: String = "0.0.0.0",
    var port: Int = 25565,
    var bungeecord: Boolean = false,
    var optfine: Boolean = true,
    var chunkViewDistance: Int = 16,
    var entityViewDistance: Int = 16,
    var brandName: String = "ExampleBrandName",
    var unknownCommandMessage: String = "未知命令: {cmd}",
)