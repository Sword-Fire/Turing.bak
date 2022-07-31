package net.geekmc.turing.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import net.geekmc.turing.Turing
import world.cepi.kstom.Manager
import java.util.concurrent.Executor

val Dispatchers.Ticking: CoroutineDispatcher
    get() = MinestomMainThreadExecutor.asCoroutineDispatcher()

object MinestomMainThreadExecutor : Executor {

    override fun execute(runnable: Runnable) {
        Manager.scheduler.scheduleNextTick(runnable)
    }

}