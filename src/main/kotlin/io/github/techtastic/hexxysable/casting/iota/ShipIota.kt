package io.github.techtastic.hexxysable.casting.iota

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer
import dev.ryanhcode.sable.sublevel.ServerSubLevel
import net.minecraft.ChatFormatting
import net.minecraft.core.UUIDUtil
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import java.util.UUID

class ShipIota(val shipId: UUID, val slug: String?) : Iota({ TYPE }) {
    fun getShip(level: ServerLevel): ServerSubLevel? =
        SubLevelContainer.getContainer(level)?.getSubLevel(shipId) as? ServerSubLevel

    override fun isTruthy() = true

    override fun toleratesOther(that: Iota): Boolean = that is ShipIota && that.shipId == shipId

    override fun display(): Component = Component
        .translatable("hexxysable.iota.ship", slug ?: shipId.toString())
        .withStyle(ChatFormatting.GOLD)

    override fun hashCode(): Int = shipId.hashCode()

    companion object {
        @JvmField
        val TYPE: IotaType<ShipIota> = object : IotaType<ShipIota>() {
            override fun codec(): MapCodec<ShipIota> = CODEC
            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, ShipIota> = STREAM_CODEC
            override fun color() = 0xff_cda638.toInt()
        }

        private val CODEC: MapCodec<ShipIota> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("ship_id").forGetter(ShipIota::shipId),
                Codec.STRING.optionalFieldOf("slug", "").forGetter { it.slug ?: "" },
            ).apply(instance) { id, slug -> ShipIota(id, slug.ifEmpty { null }) }
        }

        private val STREAM_CODEC = object : StreamCodec<RegistryFriendlyByteBuf, ShipIota> {
            override fun decode(buffer: RegistryFriendlyByteBuf): ShipIota {
                val id = buffer.readUUID()
                return ShipIota(id, buffer.readUtf().ifEmpty { null })
            }

            override fun encode(buffer: RegistryFriendlyByteBuf, value: ShipIota) {
                buffer.writeUUID(value.shipId)
                buffer.writeUtf(value.slug ?: "")
            }
        }
    }
}
