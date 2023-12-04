@file:Suppress("deprecation", "ClassName")

package com.ravingarinc.api

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.Converters
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.*
import java.util.function.Consumer
import java.util.logging.Level
import kotlin.experimental.ExperimentalTypeInference


/**
 * Versions representing usages of different protocols
 * See https://wiki.vg/Protocol_version_numbers
 *
 * // todo fill these out with appropriate values after restructuring the project such that there are different modules
 */
sealed class Version(
    val protocol: Int,
    val packFormat: Int,
    val names: Array<String>
) {
    /**
     * See protocol - https://wiki.vg/index.php?title=Protocol&oldid=16681
     * See metadata - https://wiki.vg/index.php?title=Entity_metadata&oldid=16539
     */
    open class V1_16_5(
        protocol: Int = 754,
        packFormat: Int = 6,
        names: Array<String> = arrayOf("1.16.4", "1.16.5")
    ) :
        Version(protocol, packFormat, names) {

        companion object : VersionCreator<V1_16_5>(::V1_16_5, 1, 16, 4..5)

        //<editor-fold desc="Entity IDs" defaultstate="collapsed">
        override val indexedEntities: Map<EntityType, Int> = buildMap {
            // So 1.16 has a different way of spawning entities which is interesting
            // TODO add entities here!
        }
        //</editor-fold>

        override fun updateMetadata(
            entity: Entity,
            data: List<Triple<Int, WrappedDataWatcher.Serializer, Any>>
        ): PacketContainer {
            val packet = Version.protocol.createPacket(PacketType.Play.Server.ENTITY_METADATA)
            packet.integers.write(0, entity.entityId)
            val watcher = WrappedDataWatcher()
            watcher.entity = entity
            data.forEach {
                watcher.setObject(if (it.first > 7) it.first - 1 else it.first, it.second, it.third)
            }
            packet.watchableCollectionModifier.write(0, watcher.watchableObjects)
            return packet
        }

        override fun playerInfo(action: PlayerInfoAction, data: PlayerInfoData): PacketContainer {
            val packet = Version.protocol.createPacket(PacketType.Play.Server.PLAYER_INFO)
            packet.playerInfoAction.write(0, action)
            packet.playerInfoDataLists.write(0, listOf(data))
            return packet
        }

        override fun spawnMob(
            id: Int,
            uuid: UUID,
            entityType: EntityType,
            x: Double,
            y: Double,
            z: Double,
            pitch: Byte,
            yaw: Byte,
            data: Int
        ): PacketContainer {
            val packet =
                if (entityType.isAlive) Version.protocol.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING)
                else Version.protocol.createPacket(PacketType.Play.Server.SPAWN_ENTITY)
            packet.integers
                .write(0, id)
                .write(1, data)
            packet.entityTypeModifier.write(0, entityType)
            packet.uuiDs.write(0, uuid)
            packet.doubles
                .write(0, x)
                .write(1, y)
                .write(2, z)
            packet.bytes
                .write(0, pitch)
                .write(1, yaw)
            packet.shorts
                .write(0, 0)
                .write(1, 0)
                .write(2, 0)
            if (entityType.isAlive) {
                packet.bytes.write(2, pitch)
            }
            return packet
        }

        override fun spawnPlayer(
            id: Int,
            uuid: UUID,
            x: Double,
            y: Double,
            z: Double,
            pitch: Byte,
            yaw: Byte
        ): PacketContainer {
            val packet = Version.protocol.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN)
            packet.integers
                .write(0, id)
            packet.uuiDs.write(0, uuid)
            packet.doubles
                .write(0, x)
                .write(1, y)
                .write(2, z)
            packet.bytes
                .write(0, pitch)
                .write(1, yaw)
            return packet
        }
    }

    /**
     * See Protocol - https://wiki.vg/index.php?title=Protocol&oldid=16866#
     * See Metadata -
     */
    open class V1_17(
        protocol: Int = 755,
        packFormat: Int = 7,
        names: Array<String> = arrayOf("1.17")
    ) :
        V1_16_5(protocol, packFormat, names) {
        //<editor-fold desc="Entity IDs" defaultstate="collapsed">
        override val indexedEntities: Map<EntityType, Int> = buildMap {
            this[EntityType.AREA_EFFECT_CLOUD] = 0
            this[EntityType.ARMOR_STAND] = 1
            this[EntityType.ARROW] = 2
            this[EntityType.AXOLOTL] = 3
            this[EntityType.BAT] = 4
            this[EntityType.BEE] = 5
            this[EntityType.BLAZE] = 6
            this[EntityType.BOAT] = 7
            this[EntityType.CAT] = 8
            this[EntityType.CAVE_SPIDER] = 9
            this[EntityType.CHICKEN] = 10
            this[EntityType.COD] = 11
            this[EntityType.COW] = 12
            this[EntityType.CREEPER] = 13
            this[EntityType.DOLPHIN] = 14
            this[EntityType.DONKEY] = 15
            this[EntityType.DRAGON_FIREBALL] = 16
            this[EntityType.DROWNED] = 17
            this[EntityType.ELDER_GUARDIAN] = 18
            this[EntityType.ENDER_CRYSTAL] = 19
            this[EntityType.ENDER_DRAGON] = 20
            this[EntityType.ENDERMAN] = 21
            this[EntityType.ENDERMITE] = 22
            this[EntityType.EVOKER] = 23
            this[EntityType.EVOKER_FANGS] = 24
            this[EntityType.EXPERIENCE_ORB] = 25
            this[EntityType.ENDER_SIGNAL] = 26
            this[EntityType.FALLING_BLOCK] = 27
            this[EntityType.FIREWORK] = 28
            this[EntityType.FOX] = 29
            this[EntityType.GHAST] = 30
            this[EntityType.GIANT] = 31
            this[EntityType.GLOW_ITEM_FRAME] = 32
            this[EntityType.GLOW_SQUID] = 33
            this[EntityType.GOAT] = 34
            this[EntityType.GUARDIAN] = 35
            this[EntityType.HOGLIN] = 36
            this[EntityType.HORSE] = 37
            this[EntityType.HUSK] = 38
            this[EntityType.ILLUSIONER] = 39
            this[EntityType.IRON_GOLEM] = 40
            this[EntityType.DROPPED_ITEM] = 41
            this[EntityType.ITEM_FRAME] = 42
            this[EntityType.FIREBALL] = 43
            this[EntityType.LEASH_HITCH] = 44
            this[EntityType.LIGHTNING] = 45
            this[EntityType.LLAMA] = 46
            this[EntityType.LLAMA_SPIT] = 47
            this[EntityType.MAGMA_CUBE] = 48
            this[EntityType.MARKER] = 49
            this[EntityType.MINECART] = 50
            this[EntityType.MINECART_CHEST] = 51
            this[EntityType.MINECART_COMMAND] = 52
            this[EntityType.MINECART_FURNACE] = 53
            this[EntityType.MINECART_HOPPER] = 54
            this[EntityType.MINECART_MOB_SPAWNER] = 55
            this[EntityType.MINECART_TNT] = 56
            this[EntityType.MULE] = 57
            this[EntityType.MUSHROOM_COW] = 58
            this[EntityType.OCELOT] = 59
            this[EntityType.PAINTING] = 60
            this[EntityType.PANDA] = 61
            this[EntityType.PARROT] = 62
            this[EntityType.PHANTOM] = 63
            this[EntityType.PIG] = 64
            this[EntityType.PIGLIN] = 65
            this[EntityType.PIGLIN_BRUTE] = 66
            this[EntityType.PILLAGER] = 67
            this[EntityType.POLAR_BEAR] = 68
            this[EntityType.PRIMED_TNT] = 69
            this[EntityType.PUFFERFISH] = 70
            this[EntityType.RABBIT] = 71
            this[EntityType.RAVAGER] = 72
            this[EntityType.SALMON] = 73
            this[EntityType.SHEEP] = 74
            this[EntityType.SHULKER] = 75
            this[EntityType.SHULKER_BULLET] = 76
            this[EntityType.SILVERFISH] = 77
            this[EntityType.SKELETON] = 78
            this[EntityType.SKELETON_HORSE] = 79
            this[EntityType.SLIME] = 80
            this[EntityType.SMALL_FIREBALL] = 81
            this[EntityType.SNOWMAN] = 82
            this[EntityType.SNOWBALL] = 83
            this[EntityType.SPECTRAL_ARROW] = 84
            this[EntityType.SPIDER] = 85
            this[EntityType.SQUID] = 86
            this[EntityType.STRAY] = 87
            this[EntityType.STRIDER] = 88
            this[EntityType.EGG] = 89
            this[EntityType.ENDER_PEARL] = 90
            this[EntityType.THROWN_EXP_BOTTLE] = 91
            this[EntityType.SPLASH_POTION] = 92
            this[EntityType.TRIDENT] = 93
            this[EntityType.TRADER_LLAMA] = 94
            this[EntityType.TROPICAL_FISH] = 95
            this[EntityType.TURTLE] = 96
            this[EntityType.VEX] = 97
            this[EntityType.VILLAGER] = 98
            this[EntityType.VINDICATOR] = 99
            this[EntityType.WANDERING_TRADER] = 100
            this[EntityType.WITCH] = 101
            this[EntityType.WITHER] = 102
            this[EntityType.WITHER_SKELETON] = 103
            this[EntityType.WITHER_SKULL] = 104
            this[EntityType.WOLF] = 105
            this[EntityType.ZOGLIN] = 106
            this[EntityType.ZOMBIE] = 107
            this[EntityType.ZOMBIE_HORSE] = 108
            this[EntityType.ZOMBIE_VILLAGER] = 109
            this[EntityType.ZOMBIFIED_PIGLIN] = 110
            this[EntityType.PLAYER] = 111
            this[EntityType.FISHING_HOOK] = 112
        }

        //</editor-fold>

        override fun updateMetadata(
            entity: Entity,
            data: List<Triple<Int, WrappedDataWatcher.Serializer, Any>>
        ): PacketContainer {
            val packet = Version.protocol.createPacket(PacketType.Play.Server.ENTITY_METADATA)
            packet.integers.write(0, entity.entityId)
            val watcher = WrappedDataWatcher()
            watcher.entity = entity
            data.forEach {
                watcher.setObject(it.first, it.second, it.third)
            }
            packet.watchableCollectionModifier.write(0, watcher.watchableObjects)
            return packet
        }

        companion object : VersionCreator<V1_17>(::V1_17, 1, 17, 0..0)
    }

    open class V1_17_1(
        protocol: Int = 756,
        packFormat: Int = 7,
        names: Array<String> = arrayOf("1.17.1")
    ) :
        V1_17(protocol, packFormat, names) {

        companion object : VersionCreator<V1_17_1>(::V1_17_1, 1, 17, 1..1)
    }

    /**
     * https://wiki.vg/index.php?title=Protocol&oldid=17341
     */
    open class V1_18(
        protocol: Int = 757,
        packFormat: Int = 8,
        names: Array<String> = arrayOf("1.18", "1.18.1")
    ) :
        V1_17_1(protocol, packFormat, names) {

        companion object : VersionCreator<V1_18>(::V1_18, 1, 18, 0..1)
    }

    open class V1_18_2(
        protocol: Int = 758,
        packFormat: Int = 8,
        names: Array<String> = arrayOf("1.18.2")
    ) :
        V1_18(protocol, packFormat, names) {

        companion object : VersionCreator<V1_18_2>(::V1_18_2, 1, 18, 2..2)
    }

    /**
     * https://wiki.vg/index.php?title=Entity_metadata&direction=next&oldid=17521
     */
    open class V1_19(
        protocol: Int = 759,
        packFormat: Int = 9,
        names: Array<String> = arrayOf("1.19")
    ) :
        V1_18_2(protocol, packFormat, names) {

        companion object : VersionCreator<V1_19>(::V1_19, 1, 19, 0..0)

        //<editor-fold desc="Entity IDs" defaultstate="collapsed">
        override val indexedEntities: Map<EntityType, Int> = buildMap {
            this[EntityType.ALLAY] = 0
            this[EntityType.AREA_EFFECT_CLOUD] = 1
            this[EntityType.ARMOR_STAND] = 2
            this[EntityType.ARROW] = 3
            this[EntityType.AXOLOTL] = 4
            this[EntityType.BAT] = 5
            this[EntityType.BEE] = 6
            this[EntityType.BLAZE] = 7
            this[EntityType.BOAT] = 8
            this[EntityType.CAT] = 10
            this[EntityType.CAVE_SPIDER] = 11
            this[EntityType.CHEST_BOAT] = 9
            this[EntityType.MINECART_CHEST] = 54
            this[EntityType.CHICKEN] = 12
            this[EntityType.COD] = 13
            this[EntityType.MINECART_COMMAND] = 55
            this[EntityType.COW] = 14
            this[EntityType.CREEPER] = 15
            this[EntityType.DOLPHIN] = 16
            this[EntityType.DONKEY] = 17
            this[EntityType.DRAGON_FIREBALL] = 18
            this[EntityType.DROWNED] = 19
            this[EntityType.EGG] = 93
            this[EntityType.ELDER_GUARDIAN] = 20
            this[EntityType.ENDER_CRYSTAL] = 21
            this[EntityType.ENDER_DRAGON] = 22
            this[EntityType.ENDER_PEARL] = 94
            this[EntityType.ENDERMAN] = 23
            this[EntityType.ENDERMITE] = 24
            this[EntityType.EVOKER] = 25
            this[EntityType.EVOKER_FANGS] = 26
            this[EntityType.THROWN_EXP_BOTTLE] = 95
            this[EntityType.EXPERIENCE_ORB] = 27
            this[EntityType.ENDER_SIGNAL] = 28
            this[EntityType.FALLING_BLOCK] = 29
            this[EntityType.FIREBALL] = 46
            this[EntityType.FIREWORK] = 30
            this[EntityType.FISHING_HOOK] = 117
            this[EntityType.FOX] = 31
            this[EntityType.FROG] = 32
            this[EntityType.MINECART_FURNACE] = 56
            this[EntityType.GHAST] = 33
            this[EntityType.GIANT] = 34
            this[EntityType.GLOW_ITEM_FRAME] = 35
            this[EntityType.GLOW_SQUID] = 36
            this[EntityType.GOAT] = 37
            this[EntityType.GUARDIAN] = 38
            this[EntityType.HOGLIN] = 39
            this[EntityType.MINECART_HOPPER] = 57
            this[EntityType.HORSE] = 40
            this[EntityType.HUSK] = 41
            this[EntityType.ILLUSIONER] = 42
            this[EntityType.IRON_GOLEM] = 43
            this[EntityType.DROPPED_ITEM] = 44
            this[EntityType.ITEM_FRAME] = 45
            this[EntityType.LEASH_HITCH] = 47
            this[EntityType.LIGHTNING] = 48
            this[EntityType.LLAMA] = 49
            this[EntityType.LLAMA_SPIT] = 50
            this[EntityType.MAGMA_CUBE] = 51
            this[EntityType.MARKER] = 52
            this[EntityType.MINECART] = 53
            this[EntityType.MUSHROOM_COW] = 61
            this[EntityType.MULE] = 60
            this[EntityType.OCELOT] = 62
            this[EntityType.PAINTING] = 63
            this[EntityType.PANDA] = 64
            this[EntityType.PARROT] = 65
            this[EntityType.PHANTOM] = 66
            this[EntityType.PIG] = 67
            this[EntityType.PIGLIN] = 68
            this[EntityType.PIGLIN_BRUTE] = 69
            this[EntityType.PILLAGER] = 70
            this[EntityType.PLAYER] = 116
            this[EntityType.POLAR_BEAR] = 71
            this[EntityType.SPLASH_POTION] = 96
            this[EntityType.PUFFERFISH] = 73
            this[EntityType.RABBIT] = 74
            this[EntityType.RAVAGER] = 75
            this[EntityType.SALMON] = 76
            this[EntityType.SHEEP] = 77
            this[EntityType.SHULKER] = 78
            this[EntityType.SHULKER_BULLET] = 79
            this[EntityType.SILVERFISH] = 80
            this[EntityType.SKELETON] = 81
            this[EntityType.SKELETON_HORSE] = 82
            this[EntityType.SLIME] = 83
            this[EntityType.SMALL_FIREBALL] = 84
            this[EntityType.SNOWMAN] = 85
            this[EntityType.SNOWBALL] = 86
            this[EntityType.MINECART_MOB_SPAWNER] = 58
            this[EntityType.SPECTRAL_ARROW] = 87
            this[EntityType.SPIDER] = 88
            this[EntityType.SQUID] = 89
            this[EntityType.STRAY] = 90
            this[EntityType.STRIDER] = 91
            this[EntityType.TADPOLE] = 92
            this[EntityType.PRIMED_TNT] = 72
            this[EntityType.MINECART_TNT] = 59
            this[EntityType.TRADER_LLAMA] = 98
            this[EntityType.TRIDENT] = 97
            this[EntityType.TROPICAL_FISH] = 99
            this[EntityType.TURTLE] = 100
            this[EntityType.VEX] = 101
            this[EntityType.VILLAGER] = 102
            this[EntityType.VINDICATOR] = 103
            this[EntityType.WANDERING_TRADER] = 104
            this[EntityType.WARDEN] = 105
            this[EntityType.WITCH] = 106
            this[EntityType.WITHER] = 107
            this[EntityType.WITHER_SKELETON] = 108
            this[EntityType.WITHER_SKULL] = 109
            this[EntityType.WOLF] = 110
            this[EntityType.ZOGLIN] = 111
            this[EntityType.ZOMBIE] = 112
            this[EntityType.ZOMBIE_HORSE] = 113
            this[EntityType.ZOMBIE_VILLAGER] = 114
            this[EntityType.ZOMBIFIED_PIGLIN] = 115
        }

        //</editor-fold>
        override fun spawnMob(
            id: Int,
            uuid: UUID,
            entityType: EntityType,
            x: Double,
            y: Double,
            z: Double,
            pitch: Byte,
            yaw: Byte,
            data: Int
        ): PacketContainer {
            val packet = Version.protocol.createPacket(PacketType.Play.Server.SPAWN_ENTITY)
            packet.integers
                .write(0, id)
            //.write(1, indexedEntities[entityType])
            if (data != -1) {
                packet.integers.write(1, data)
            }
            packet.entityTypeModifier.write(0, entityType)
            packet.uuiDs.write(0, uuid)
            packet.doubles
                .write(0, x)
                .write(1, y)
                .write(2, z)
            packet.bytes
                .write(0, pitch)
                .write(1, yaw)
                .writeSafely(2, pitch)
            packet.shorts
                .writeSafely(0, 0)
                .writeSafely(1, 0)
                .writeSafely(2, 0)
            return packet
        }
    }

    /**
     * Protocol - https://wiki.vg/index.php?title=Protocol&oldid=17873
     */
    open class V1_19_2(
        protocol: Int = 760,
        packFormat: Int = 9,
        names: Array<String> = arrayOf("1.19.1", "1.19.2")
    ) :
        V1_19(protocol, packFormat, names) {

        companion object : VersionCreator<V1_19_2>(::V1_19_2, 1, 19, 1..2)
    }

    /**
     * Protocol Articles
     *
     * https://wiki.vg/index.php?title=Protocol&oldid=18067
     * Changes here, Player Info Update, was separated in terms of its functionality
     */
    open class V1_19_3(
        protocol: Int = 761,
        packFormat: Int = 12,
        names: Array<String> = arrayOf("1.19.3")
    ) :
        V1_19_2(protocol, packFormat, names) {
        //<editor-fold desc="Entity IDs" defaultstate="collapsed">
        override val indexedEntities: Map<EntityType, Int> = buildMap {
            this[EntityType.ALLAY] = 0
            this[EntityType.AREA_EFFECT_CLOUD] = 1
            this[EntityType.ARMOR_STAND] = 2
            this[EntityType.ARROW] = 3
            this[EntityType.AXOLOTL] = 4
            this[EntityType.BAT] = 5
            this[EntityType.BEE] = 6
            this[EntityType.BLAZE] = 7
            this[EntityType.BOAT] = 8
            this[EntityType.CAMEL] = 11
            this[EntityType.CAT] = 10
            this[EntityType.CAVE_SPIDER] = 12
            this[EntityType.CHEST_BOAT] = 9
            this[EntityType.MINECART_CHEST] = 55
            this[EntityType.CHICKEN] = 13
            this[EntityType.COD] = 14
            this[EntityType.MINECART_COMMAND] = 56
            this[EntityType.COW] = 15
            this[EntityType.CREEPER] = 16
            this[EntityType.DOLPHIN] = 17
            this[EntityType.DONKEY] = 18
            this[EntityType.DRAGON_FIREBALL] = 19
            this[EntityType.DROWNED] = 20
            this[EntityType.EGG] = 94
            this[EntityType.ELDER_GUARDIAN] = 21
            this[EntityType.ENDER_CRYSTAL] = 22
            this[EntityType.ENDER_DRAGON] = 23
            this[EntityType.ENDER_PEARL] = 95
            this[EntityType.ENDERMAN] = 24
            this[EntityType.ENDERMITE] = 25
            this[EntityType.EVOKER] = 26
            this[EntityType.EVOKER_FANGS] = 27
            this[EntityType.THROWN_EXP_BOTTLE] = 96
            this[EntityType.EXPERIENCE_ORB] = 28
            this[EntityType.ENDER_SIGNAL] = 29
            this[EntityType.FALLING_BLOCK] = 30
            this[EntityType.FIREBALL] = 47
            this[EntityType.FIREWORK] = 31
            this[EntityType.FISHING_HOOK] = 118
            this[EntityType.FOX] = 32
            this[EntityType.FROG] = 33
            this[EntityType.MINECART_FURNACE] = 57
            this[EntityType.GHAST] = 34
            this[EntityType.GIANT] = 35
            this[EntityType.GLOW_ITEM_FRAME] = 36
            this[EntityType.GLOW_SQUID] = 37
            this[EntityType.GOAT] = 38
            this[EntityType.GUARDIAN] = 39
            this[EntityType.HOGLIN] = 40
            this[EntityType.MINECART_HOPPER] = 58
            this[EntityType.HORSE] = 41
            this[EntityType.HUSK] = 42
            this[EntityType.ILLUSIONER] = 43
            this[EntityType.IRON_GOLEM] = 44
            this[EntityType.DROPPED_ITEM] = 45
            this[EntityType.ITEM_FRAME] = 46
            this[EntityType.LEASH_HITCH] = 48
            this[EntityType.LIGHTNING] = 49
            this[EntityType.LLAMA] = 50
            this[EntityType.LLAMA_SPIT] = 51
            this[EntityType.MAGMA_CUBE] = 52
            this[EntityType.MARKER] = 53
            this[EntityType.MINECART] = 54
            this[EntityType.MUSHROOM_COW] = 62
            this[EntityType.MULE] = 61
            this[EntityType.OCELOT] = 63
            this[EntityType.PAINTING] = 64
            this[EntityType.PANDA] = 65
            this[EntityType.PARROT] = 66
            this[EntityType.PHANTOM] = 67
            this[EntityType.PIG] = 68
            this[EntityType.PIGLIN] = 69
            this[EntityType.PIGLIN_BRUTE] = 70
            this[EntityType.PILLAGER] = 71
            this[EntityType.PLAYER] = 117
            this[EntityType.POLAR_BEAR] = 72
            this[EntityType.SPLASH_POTION] = 97
            this[EntityType.PUFFERFISH] = 74
            this[EntityType.RABBIT] = 75
            this[EntityType.RAVAGER] = 76
            this[EntityType.SALMON] = 77
            this[EntityType.SHEEP] = 78
            this[EntityType.SHULKER] = 79
            this[EntityType.SHULKER_BULLET] = 80
            this[EntityType.SILVERFISH] = 81
            this[EntityType.SKELETON] = 82
            this[EntityType.SKELETON_HORSE] = 83
            this[EntityType.SLIME] = 84
            this[EntityType.SMALL_FIREBALL] = 85
            this[EntityType.SNOWMAN] = 86
            this[EntityType.SNOWBALL] = 87
            this[EntityType.MINECART_MOB_SPAWNER] = 59
            this[EntityType.SPECTRAL_ARROW] = 88
            this[EntityType.SPIDER] = 89
            this[EntityType.SQUID] = 90
            this[EntityType.STRAY] = 91
            this[EntityType.STRIDER] = 92
            this[EntityType.TADPOLE] = 93
            this[EntityType.PRIMED_TNT] = 73
            this[EntityType.MINECART_TNT] = 60
            this[EntityType.TRADER_LLAMA] = 99
            this[EntityType.TRIDENT] = 98
            this[EntityType.TROPICAL_FISH] = 100
            this[EntityType.TURTLE] = 101
            this[EntityType.VEX] = 102
            this[EntityType.VILLAGER] = 103
            this[EntityType.VINDICATOR] = 104
            this[EntityType.WANDERING_TRADER] = 105
            this[EntityType.WARDEN] = 106
            this[EntityType.WITCH] = 107
            this[EntityType.WITHER] = 108
            this[EntityType.WITHER_SKELETON] = 109
            this[EntityType.WITHER_SKULL] = 110
            this[EntityType.WOLF] = 111
            this[EntityType.ZOGLIN] = 112
            this[EntityType.ZOMBIE] = 113
            this[EntityType.ZOMBIE_HORSE] = 114
            this[EntityType.ZOMBIE_VILLAGER] = 115
            this[EntityType.ZOMBIFIED_PIGLIN] = 116
        }
        //</editor-fold>

        private val allPlayerInfoActions = setOf(
            PlayerInfoAction.ADD_PLAYER,
            PlayerInfoAction.UPDATE_LATENCY,
            PlayerInfoAction.UPDATE_DISPLAY_NAME,
            PlayerInfoAction.UPDATE_GAME_MODE,
            PlayerInfoAction.UPDATE_LISTED,
            PlayerInfoAction.INITIALIZE_CHAT
        )

        companion object : VersionCreator<V1_19_3>(::V1_19_3, 1, 19, 3..3)

        override fun updateMetadata(
            entity: Entity,
            data: List<Triple<Int, WrappedDataWatcher.Serializer, Any>>
        ): PacketContainer {
            val packet = Version.protocol.createPacket(PacketType.Play.Server.ENTITY_METADATA)
            packet.integers.write(0, entity.entityId)
            packet.dataValueCollectionModifier.write(0, data.map { WrappedDataValue(it.first, it.second, it.third) })
            return packet
        }

        override fun playerInfo(action: PlayerInfoAction, data: PlayerInfoData): PacketContainer {
            when (action) {
                PlayerInfoAction.ADD_PLAYER -> {
                    val packet = Version.protocol.createPacket(PacketType.Play.Server.PLAYER_INFO)
                    packet.playerInfoActions.write(0, allPlayerInfoActions)
                    packet.playerInfoDataLists.write(1, listOf(data))
                    return packet
                }
                PlayerInfoAction.REMOVE_PLAYER -> {
                    val packet = Version.protocol.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE)
                    packet.getLists(Converters.passthrough(UUID::class.java)).write(0, listOf(data.profileId))
                    return packet
                }

                else -> {
                    throw IllegalArgumentException("Only ADD_PLAYER and REMOVE_PLAYER are supported for PlayerInfoPacket at this time!")
                }
            }
        }
    }

    /**
     * Protocol Articles
     *
     * https://wiki.vg/index.php?title=Entity_metadata&oldid=18191
     */
    open class V1_19_4(
        protocol: Int = 762,
        packFormat: Int = 13,
        names: Array<String> = arrayOf("1.19.4")
    ) : V1_19_3(protocol, packFormat, names) {

        override fun sendPackets(player: Player, vararg packets: PacketContainer) {
            if (packets.isEmpty()) return
            if (packets.size > 1) {
                val packet = Version.protocol.createPacket(PacketType.Play.Server.BUNDLE)
                packet.packetBundles.write(0, packets.asIterable())
                Version.protocol.sendServerPacket(player, packet)
            } else {
                Version.protocol.sendServerPacket(player, packets[0])
            }
        }
        //<editor-fold desc="Entity IDs" defaultstate="collapsed">
        override val indexedEntities: Map<EntityType, Int> = buildMap {
            this[EntityType.ALLAY] = 0
            this[EntityType.AREA_EFFECT_CLOUD] = 1
            this[EntityType.ARMOR_STAND] = 2
            this[EntityType.ARROW] = 3
            this[EntityType.AXOLOTL] = 4
            this[EntityType.BAT] = 5
            this[EntityType.BEE] = 6
            this[EntityType.BLAZE] = 7
            this[EntityType.BLOCK_DISPLAY] = 8
            this[EntityType.BOAT] = 9
            this[EntityType.CAMEL] = 10
            this[EntityType.CAT] = 11
            this[EntityType.CAVE_SPIDER] = 12
            this[EntityType.CHEST_BOAT] = 13
            this[EntityType.MINECART_CHEST] = 14
            this[EntityType.CHICKEN] = 15
            this[EntityType.COD] = 16
            this[EntityType.MINECART_COMMAND] = 17
            this[EntityType.COW] = 18
            this[EntityType.CREEPER] = 19
            this[EntityType.DOLPHIN] = 20
            this[EntityType.DONKEY] = 21
            this[EntityType.DRAGON_FIREBALL] = 22
            this[EntityType.DROWNED] = 23
            this[EntityType.EGG] = 24
            this[EntityType.ELDER_GUARDIAN] = 25
            this[EntityType.ENDER_CRYSTAL] = 26
            this[EntityType.ENDER_DRAGON] = 27
            this[EntityType.ENDER_PEARL] = 28
            this[EntityType.ENDERMAN] = 29
            this[EntityType.ENDERMITE] = 30
            this[EntityType.EVOKER] = 31
            this[EntityType.EVOKER_FANGS] = 32
            this[EntityType.THROWN_EXP_BOTTLE] = 33
            this[EntityType.EXPERIENCE_ORB] = 34
            this[EntityType.ENDER_SIGNAL] = 35
            this[EntityType.FALLING_BLOCK] = 36
            this[EntityType.FIREBALL] = 57
            this[EntityType.FIREWORK] = 37
            this[EntityType.FISHING_HOOK] = 123
            this[EntityType.FOX] = 38
            this[EntityType.FROG] = 39
            this[EntityType.MINECART_FURNACE] = 40
            this[EntityType.GHAST] = 41
            this[EntityType.GIANT] = 42
            this[EntityType.GLOW_ITEM_FRAME] = 43
            this[EntityType.GLOW_SQUID] = 44
            this[EntityType.GOAT] = 45
            this[EntityType.GUARDIAN] = 46
            this[EntityType.HOGLIN] = 47
            this[EntityType.MINECART_HOPPER] = 48
            this[EntityType.HORSE] = 49
            this[EntityType.HUSK] = 50
            this[EntityType.ILLUSIONER] = 51
            this[EntityType.INTERACTION] = 52
            this[EntityType.IRON_GOLEM] = 53
            this[EntityType.DROPPED_ITEM] = 54
            this[EntityType.ITEM_DISPLAY] = 55
            this[EntityType.ITEM_FRAME] = 56
            this[EntityType.LEASH_HITCH] = 58
            this[EntityType.LIGHTNING] = 59
            this[EntityType.LLAMA] = 60
            this[EntityType.LLAMA_SPIT] = 61
            this[EntityType.MAGMA_CUBE] = 62
            this[EntityType.MARKER] = 63
            this[EntityType.MINECART] = 64
            this[EntityType.MUSHROOM_COW] = 65
            this[EntityType.MULE] = 66
            this[EntityType.OCELOT] = 67
            this[EntityType.PAINTING] = 68
            this[EntityType.PANDA] = 69
            this[EntityType.PARROT] = 70
            this[EntityType.PHANTOM] = 71
            this[EntityType.PIG] = 72
            this[EntityType.PIGLIN] = 73
            this[EntityType.PIGLIN_BRUTE] = 74
            this[EntityType.PILLAGER] = 75
            this[EntityType.PLAYER] = 122
            this[EntityType.POLAR_BEAR] = 76
            this[EntityType.SPLASH_POTION] = 77
            this[EntityType.PUFFERFISH] = 78
            this[EntityType.RABBIT] = 79
            this[EntityType.RAVAGER] = 80
            this[EntityType.SALMON] = 81
            this[EntityType.SHEEP] = 82
            this[EntityType.SHULKER] = 83
            this[EntityType.SHULKER_BULLET] = 84
            this[EntityType.SILVERFISH] = 85
            this[EntityType.SKELETON] = 86
            this[EntityType.SKELETON_HORSE] = 87
            this[EntityType.SLIME] = 88
            this[EntityType.SMALL_FIREBALL] = 89
            this[EntityType.SNIFFER] = 90
            this[EntityType.SNOWMAN] = 91
            this[EntityType.SNOWBALL] = 92
            this[EntityType.MINECART_MOB_SPAWNER] = 93
            this[EntityType.SPECTRAL_ARROW] = 94
            this[EntityType.SPIDER] = 95
            this[EntityType.SQUID] = 96
            this[EntityType.STRAY] = 97
            this[EntityType.STRIDER] = 98
            this[EntityType.TADPOLE] = 99
            this[EntityType.TEXT_DISPLAY] = 100
            this[EntityType.PRIMED_TNT] = 101
            this[EntityType.MINECART_TNT] = 102
            this[EntityType.TRADER_LLAMA] = 103
            this[EntityType.TRIDENT] = 104
            this[EntityType.TROPICAL_FISH] = 105
            this[EntityType.TURTLE] = 106
            this[EntityType.VEX] = 107
            this[EntityType.VILLAGER] = 108
            this[EntityType.VINDICATOR] = 109
            this[EntityType.WANDERING_TRADER] = 110
            this[EntityType.WARDEN] = 111
            this[EntityType.WITCH] = 112
            this[EntityType.WITHER] = 113
            this[EntityType.WITHER_SKELETON] = 114
            this[EntityType.WITHER_SKULL] = 115
            this[EntityType.WOLF] = 116
            this[EntityType.ZOGLIN] = 117
            this[EntityType.ZOMBIE] = 118
            this[EntityType.ZOMBIE_HORSE] = 119
            this[EntityType.ZOMBIE_VILLAGER] = 120
            this[EntityType.ZOMBIFIED_PIGLIN] = 121
        }

        //</editor-fold>
        companion object : VersionCreator<V1_19_4>(::V1_19_4, 1, 19, 4..4) {
            val vectorSerializer by lazy { WrappedDataWatcher.Registry.get(Vector3f::class.java) }
            val axisAngleSerializer by lazy { WrappedDataWatcher.Registry.get(AxisAngle4f::class.java) }
            val quaternionSerializer by lazy { WrappedDataWatcher.Registry.get(Quaternionf::class.java) }
        }
    }

    open class V1_20(
        protocol: Int = 763,
        packFormat: Int = 15,
        names: Array<String> = arrayOf("1.20", "1.20.1")
    ) : V1_19_4(protocol, packFormat, names) {
        companion object : VersionCreator<V1_20>(::V1_20, 1, 20, 0..1)
    }

    open class V1_20_2(
        protocol: Int = 764,
        packFormat: Int = 18,
        names: Array<String> = arrayOf("1.20.2")
    ) : V1_20(protocol, packFormat, names) {

        override fun spawnPlayer(
            id: Int,
            uuid: UUID,
            x: Double,
            y: Double,
            z: Double,
            pitch: Byte,
            yaw: Byte
        ): PacketContainer {
            val packet = Version.protocol.createPacket(PacketType.Play.Server.SPAWN_ENTITY)
            packet.integers
                .write(0, id)
            packet.entityTypeModifier.write(0, EntityType.PLAYER)
            packet.uuiDs.write(0, uuid)
            packet.doubles
                .write(0, x)
                .write(1, y)
                .write(2, z)
            packet.bytes
                .write(0, pitch)
                .write(1, yaw)
            //.writeSafely(2, pitch)
            return packet
        }

        companion object : VersionCreator<V1_20_2>(::V1_20_2, 1, 20, 2..2)
    }

    protected abstract val indexedEntities: Map<EntityType, Int>

    /**
     * Create a version specific packet for entity metadata. Note that in 1.17, an additional index was added to the
     * base entity class, meaning any indices at 7 or above before 1.17 will have their index subtracted by 1. When
     * constructing this metadata packet, please make it for the latest version!
     */
    fun updateMetadata(
        entity: Entity,
        consumer: Consumer<MutableList<Triple<Int, WrappedDataWatcher.Serializer, Any>>>
    ): PacketContainer {
        return updateMetadata(entity) {
            consumer.accept(this)
        }
    }

    /**
     * Create a version specific packet for entity metadata. Note that in 1.17, an additional index was added to the
     * base entity class, meaning any indices at 7 or above before 1.17 will have their index subtracted by 1. When
     * constructing this metadata packet, please make it for the latest version!
     */
    @OptIn(ExperimentalTypeInference::class)
    fun updateMetadata(
        entity: Entity,
        @BuilderInference builder: MutableList<Triple<Int, WrappedDataWatcher.Serializer, Any>>.() -> Unit
    ): PacketContainer {
        val list = ArrayList<Triple<Int, WrappedDataWatcher.Serializer, Any>>()
        builder.invoke(list)
        return updateMetadata(entity, list)
    }

    /**
     * Create a version specific packet for entity metadata. Note that in 1.17, an additional index was added to the
     * base entity class, meaning any indices at 7 or above before 1.17 will have their index subtracted by 1. When
     * constructing this metadata packet, please make it for the latest version!
     */
    abstract fun updateMetadata(
        entity: Entity,
        data: List<Triple<Int, WrappedDataWatcher.Serializer, Any>>
    ): PacketContainer

    fun spawnEntity(
        id: Int,
        uuid: UUID,
        entityType: EntityType,
        x: Double,
        y: Double,
        z: Double,
        pitch: Byte = 0,
        yaw: Byte = 0,
        data: Int = -1
    ): PacketContainer {
        return if (entityType == EntityType.PLAYER) spawnPlayer(id, uuid, x, y, z, pitch, yaw) else spawnMob(
            id,
            uuid,
            entityType,
            x,
            y,
            z,
            pitch,
            yaw,
            data
        )
    }

    abstract fun spawnPlayer(
        id: Int,
        uuid: UUID,
        x: Double,
        y: Double,
        z: Double,
        pitch: Byte = 0,
        yaw: Byte = 0
    ): PacketContainer

    abstract fun spawnMob(
        id: Int,
        uuid: UUID,
        entityType: EntityType,
        x: Double,
        y: Double,
        z: Double,
        pitch: Byte,
        yaw: Byte,
        data: Int
    ): PacketContainer

    abstract fun playerInfo(action: PlayerInfoAction, data: PlayerInfoData): PacketContainer

    open fun sendPackets(player: Player, vararg packets: PacketContainer) {
        for (packet in packets) {
            Version.protocol.sendServerPacket(player, packet)
        }
    }

    fun removeEntity(entityId: Int): PacketContainer {
        return removeEntities(listOf(entityId))
    }

    fun removeEntities(entityIds: List<Int>): PacketContainer {
        val packet = Version.protocol.createPacket(PacketType.Play.Server.ENTITY_DESTROY)
        packet.intLists.write(0, entityIds)
        return packet
    }

    fun getVersionName(): String {
        return names[names.size - 1]
    }

    companion object {
        val protocol: ProtocolManager = ProtocolLibrary.getProtocolManager()
        val byteSerializer = WrappedDataWatcher.Registry.get(java.lang.Byte::class.java)
        val boolSerializer = WrappedDataWatcher.Registry.get(java.lang.Boolean::class.java)
        val integerSerializer = WrappedDataWatcher.Registry.get(java.lang.Integer::class.java)
        val floatSerializer = WrappedDataWatcher.Registry.get(java.lang.Float::class.java)
        val itemSerializer = WrappedDataWatcher.Registry.getItemStackSerializer(false)
    }

    open class VersionCreator<T : Version>(
        private var creator: () -> T,
        val major: Int,
        val minor: Int,
        val patch: IntRange
    ) {
        private lateinit var module: T
        fun create(): T {
            module = creator.invoke()
            return module
        }
    }
}

object Versions {

    val major: Int
        get() = companion?.major
            ?: throw IllegalStateException("Cannot get major version as Version.initialise() has not been called yet!")
    val minor: Int
        get() = companion?.minor
            ?: throw IllegalStateException("Cannot get minor version as Version.initialise() has not been called yet!")
    val patch: IntRange
        get() = companion?.patch
            ?: throw IllegalStateException("Cannot get patch version as Version.initialise() has not been called yet!")

    val version: Version
        get() = innerVersion
            ?: throw IllegalStateException("Cannot get version as Version.initialise() has not been called yet!")

    private var innerVersion: Version? = null
    private var companion: Version.VersionCreator<*>? = null

    fun initialise(validVersions: Array<Version.VersionCreator<out Version>>) {
        if (validVersions.isEmpty()) {
            throw IllegalStateException("Parameter 'validVersions' for method Versions.initialise() must contain at least 1 version!")
        }
        val bukkitVersion = Bukkit.getServer().bukkitVersion // expecting Format of 1.18.2-R0.1-SNAPSHOT
        val parts = bukkitVersion.substring(0, bukkitVersion.indexOf('-')).split(".")

        val major = parts[0].toIntOrNull()
            ?: throw IllegalStateException("Could not parse version major from version $bukkitVersion!")
        val minor = parts[1].toIntOrNull()
            ?: throw IllegalStateException("Could not parse version minor from version $bukkitVersion!")
        val patch = (if (parts.size > 2) parts[2].toIntOrNull() else 0)
            ?: throw IllegalStateException("Could not parse version patch from version $bukkitVersion!")

        var creator: Version.VersionCreator<*>? = null
        var latest = validVersions[0]
        for (it in validVersions) {
            if (it.major > latest.major && it.minor > latest.minor && it.patch.first > latest.patch.last) {
                latest = it
            }
            if (it.major == major && it.minor == minor && it.patch.contains(patch)) {
                creator = it
                break
            }
        }
        if (creator == null) {
            I.log(
                Level.SEVERE, "Could not find handler for version '$bukkitVersion'! Using the latest " +
                        "available handler for version '${latest.major}.${latest.minor}.${latest.patch.last}' as a fallback! " +
                        "There will most likely be issues so please update this plugin or use an older server version! Use at your own risk!"
            )
            creator = latest
        }
        companion = creator
        innerVersion = creator.create()
    }
}

fun MutableList<Triple<Int, WrappedDataWatcher.Serializer, Any>>.build(
    index: Int,
    serializer: WrappedDataWatcher.Serializer,
    obj: Any
) {
    this.add(Triple(index, serializer, obj))
}

@Deprecated(
    "Version is now reponsible for handling sending packets due to 1.19 changes.",
    ReplaceWith("com.ravingarinc.api.Version.sendPackets(player,packets)")
)
fun Player.sendPacket(vararg packets: PacketContainer) {
    Versions.version.sendPackets(this, *packets)
}