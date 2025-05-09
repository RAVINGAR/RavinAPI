//@file:Suppress("deprecation", "ClassName")

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
                .writeSafely(2, yaw)
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

        private val allPlayerInfoActions = setOf(
            PlayerInfoAction.ADD_PLAYER,
            //PlayerInfoAction.UPDATE_LATENCY,
            //PlayerInfoAction.UPDATE_DISPLAY_NAME,
            //PlayerInfoAction.UPDATE_GAME_MODE,
            //PlayerInfoAction.UPDATE_LISTED,
            //PlayerInfoAction.INITIALIZE_CHAT
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

    open class V1_20_3(
        protocol: Int = 765,
        packFormat: Int = 22,
        names: Array<String> = arrayOf("1.20.3", "1.20.4")
    ) : V1_20_2(protocol, packFormat, names) {
        companion object : VersionCreator<V1_20_3>(::V1_20_3, 1, 20, 3..4)
    }

    open class V1_20_5(
        protocol: Int = 766,
        packFormat: Int = 23,
        names: Array<String> = arrayOf("1.20.5", "1.20.6")
    ) : V1_20_3(protocol, packFormat, names) {
        companion object : VersionCreator<V1_20_5>(::V1_20_5, 1, 20, 5..6)
    }

    open class V1_21(
        protocol: Int = 767,
        packFormat: Int = 24,
        names: Array<String> = arrayOf("1.21", "1.21.1")
    ) : V1_20_5(protocol, packFormat, names) {
        companion object : VersionCreator<V1_21>(::V1_21, 1, 21, 0..1)
    }

    open class V1_21_2(
        protocol: Int = 768,
        packFormat: Int = 24,
        names: Array<String> = arrayOf("1.21.2", "1.21.3")
    ) : V1_21(protocol, packFormat, names) {
        companion object : VersionCreator<V1_21_2>(::V1_21_2, 1, 21, 2..3)
    }

    open class V1_21_4(
        protocol: Int = 769,
        packFormat: Int = 24,
        names: Array<String> = arrayOf("1.21.4")
    ) : V1_21_2(protocol, packFormat, names) {
        companion object : VersionCreator<V1_21_4>(::V1_21_4, 1, 21, 4..4)
    }

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

        fun sendPackets(player: Player, vararg packets: PacketContainer) {
            Versions.version.sendPackets(player, *packets)
        }

        fun sendPacket(player: Player, packet: PacketContainer) {
            Versions.version.sendPackets(player, packet)
        }
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
            if (it.major > latest.major || it.minor > latest.minor || it.patch.first > latest.patch.last) {
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

/**
 * Convenience method for short for sending an array of packets as a bundle. This ensures that the client will receive
 * all the packets and process them on the same tick. This is the same as @see {Versions.version.sendPackets}
 */
fun Player.sendPacket(packets: PacketContainer) {
    Versions.version.sendPackets(this, packets)
}

/**
 * Convenience method for short for sending an array of packets as a bundle. This ensures that the client will receive
 * all the packets and process them on the same tick. This is the same as @see {Versions.version.sendPackets}
 */
fun Player.sendPackets(vararg packets: PacketContainer) {
    Versions.version.sendPackets(this, *packets)
}