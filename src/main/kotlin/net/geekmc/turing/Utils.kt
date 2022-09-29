package net.geekmc.turing

fun info(vararg messages: Any) {
    messages.forEach { Turing.LOGGER.info(it.toString()) }
}