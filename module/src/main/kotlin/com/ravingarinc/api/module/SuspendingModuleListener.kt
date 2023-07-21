package com.ravingarinc.api.module

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

open class SuspendingModuleListener(
    identifier: Class<out Module>,
    plugin: RavinPlugin,
    isRequired: Boolean = true,
    vararg dependsOn: Class<out Module>
) : SuspendingModule(identifier, plugin, isRequired, *dependsOn), Listener {
    override suspend fun suspendLoad() {
        plugin.server.pluginManager.registerSuspendingEvents(this, plugin)
    }

    override suspend fun suspendCancel() {
        HandlerList.unregisterAll(this)
    }
}