package com.ravingarinc.api.module

import java.util.logging.Level

abstract class SuspendingModule(
    identifier: Class<out Module>,
    plugin: RavinPluginKotlin,
    vararg dependsOn: Class<out Module>
) : Module(identifier, plugin, *dependsOn) {
    suspend fun suspendInitialise() {
        for (depend in dependsOn) {
            if (!plugin.getModule(depend).isLoaded) {
                throw ModuleLoadException(this, ModuleLoadException.Reason.DEPENDENCY)
            }
        }
        try {
            suspendLoad()
        } catch (exception: ModuleLoadException) {
            throw exception
        } catch (exception: Exception) {
            throw ModuleLoadException(this, exception)
        }
        log(Level.INFO, "$name has been loaded")
        isLoaded = true
    }

    abstract suspend fun suspendLoad()

    abstract suspend fun suspendCancel()

    override fun load() {
        throw UnsupportedOperationException("load() method should not be used on SuspendingModule! Please use suspendLoad() instead!")
    }

    override fun cancel() {
        throw UnsupportedOperationException("cancel() method should not be used on SuspendingModule! Please use suspendCancel() instead!")
    }

    override fun initialise() {
        throw UnsupportedOperationException("initialise() method should not be used on SuspendingModule! Please use suspendInitialise() instead!")
    }
}