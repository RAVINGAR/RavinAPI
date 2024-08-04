package com.ravingarinc.api

import org.bukkit.Location
import org.bukkit.NamespacedKey

interface VersionWrapper {

    /**
     * Get the namespaced key of the biome at the given location. Considers x and z coordinates (y as well in
     * certain Minecraft versions) and the world of the given location.
     */
    fun getBiomeNamespacedKey(location: Location): NamespacedKey
}