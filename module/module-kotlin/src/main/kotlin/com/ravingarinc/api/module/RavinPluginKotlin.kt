package com.ravingarinc.api.module

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import com.ravingarinc.api.I
import java.util.logging.Level

abstract class RavinPluginKotlin : SuspendingJavaPlugin(), RavinPlugin {
    private val modules: MutableMap<Class<out Module>, Module> = LinkedHashMap()

    val pubModules: Map<Class<out Module>, Module> get() = modules

    override suspend fun onLoadAsync() {
        I.load(this, false)
    }

    override suspend fun onEnableAsync() {
        loadModules()
        load()
        loadCommands()
    }

    override suspend fun onDisableAsync() {
        cancel()
    }

    override fun onDisable() {
        super.onDisable()
        server.scheduler.cancelTasks(this)
        info("$name is now disabled.")
    }

    open suspend fun load() {
        var loaded = 0
        modules.values.forEach { module ->
            try {
                if (module is SuspendingModule) {
                    module.suspendInitialise()
                } else {
                    module.initialise()
                }
            } catch (exception: ModuleLoadException) {
                I.log(if (module.isRequired) Level.SEVERE else Level.INFO, exception.message, exception.cause)
            }
            if (module.isLoaded || !module.isRequired) loaded++
        }
        if (loaded > 1) {
            if (loaded == modules.size) {
                info("$name has been enabled successfully!")
            } else {
                info("$name has been partially enabled!")
                warn("${modules.size - loaded} module/s have failed to load!")
            }
        } else {
            info("No modules could be loaded! $name will now shutdown...")
            onDisableAsync();
        }
    }

    override fun reload() {
        launch {
            cancel()
            load()
        }
    }

    open suspend fun cancel() {
        modules.values.reversed().filter { it.isLoaded }.forEach { module ->
            try {
                if (module is SuspendingModule) {
                    module.suspendCancel()
                } else {
                    module.cancel()
                }
                module.isLoaded = false
            } catch (exception: Exception) {
                severe("Encountered exception cancelling module!", exception)
            }
        }
    }

    override fun <T : Module> addModule(module: Class<T>) {
        Module.initialise(this, module).ifPresent {
            modules[module] = it
        }
    }

    @Suppress("unchecked")
    override fun <T : Module> getModule(type: Class<T>): T {
        val m = modules[type]
            ?: throw IllegalArgumentException("Could not find module of type ${type.name}. Contact developer! Most likely, #.getModule() has been called from a module's constructor! Please use a lazy intialisation!")
        return m as T
    }


}

fun log(level: Level, message: String, throwable: Throwable? = null) {
    I.log(level, message, throwable)
}

fun info(message: String, throwable: Throwable? = null) {
    log(Level.INFO, message, throwable)
}

fun warn(message: String, throwable: Throwable? = null) {
    log(Level.WARNING, message, throwable)
}

fun severe(message: String, throwable: Throwable? = null) {
    log(Level.SEVERE, message, throwable)
}

inline fun <reified T : Module> RavinPlugin.getModule(): T {
    val type = T::class.java
    val m = (this as RavinPluginKotlin).pubModules[type]
        ?: throw IllegalArgumentException("Could not find module of type ${type}. Contact developer! Most likely, #.getModule() has been called from a module's constructor! Please use a lazy intialisation!")
    return m as T
}