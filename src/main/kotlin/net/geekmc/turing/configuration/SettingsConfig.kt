package net.geekmc.turing.configuration

data class SettingsConfig(
    var port: Int? = null,
    var bungeecord: Boolean? = null,
    var optfine: Boolean? = null,
    var list: List<Int>? = null
)