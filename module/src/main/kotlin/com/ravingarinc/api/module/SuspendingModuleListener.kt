package com.ravingarinc.api.module

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

class SuspendingModuleListener(
    identifier: Class<out Module>,
    plugin: RavinPluginKotlin,
    vararg dependsOn: Class<out Module>
) : SuspendingModule(identifier, plugin, *dependsOn), Listener {
    override suspend fun suspendLoad() {
        plugin.server.pluginManager.registerSuspendingEvents(this, plugin)
    }

    override suspend fun suspendCancel() {
        HandlerList.unregisterAll(this)
    }
}