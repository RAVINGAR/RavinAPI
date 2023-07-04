package com.ravingarinc.api.module

import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

open class SuspendingModuleListener(
    identifier: Class<out Module>,
    plugin: RavinPlugin,
    vararg dependsOn: Class<out Module>
) : SuspendingModule(identifier, plugin, *dependsOn), Listener {
    override suspend fun suspendLoad() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override suspend fun suspendCancel() {
        HandlerList.unregisterAll(this)
    }
}