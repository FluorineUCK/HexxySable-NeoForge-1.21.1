package io.github.techtastic.hexxysable.neo

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.common.lib.HexRegistries
import io.github.techtastic.hexxysable.HexxySable
import io.github.techtastic.hexxysable.casting.iota.ShipIota
import io.github.techtastic.hexxysable.registry.HexxySablePatterns
import io.github.techtastic.hexxysable.registry.HexalPatterns
import io.github.techtastic.hexxysable.interop.PatchouliInterop
import io.github.techtastic.hexxysable.interop.HexalEnvironmentInterop
import io.github.techtastic.hexxysable.sablecompat.SableAmbitComponent
import io.github.techtastic.hexxysable.sablecompat.SableAssemblyBridge
import io.github.techtastic.hexxysable.sablecompat.SableMotionBridge
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.ModList
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.registries.RegisterEvent

@Mod(HexxySable.MOD_ID)
class HexxySableNeo(modBus: IEventBus) {
    init {
        modBus.addListener(::registerHexContent)
        NeoForge.EVENT_BUS.addListener(SableMotionBridge::onPrePhysicsTick)
        NeoForge.EVENT_BUS.addListener(SableAssemblyBridge::onLevelTick)
        NeoForge.EVENT_BUS.addListener(SableMotionBridge::onServerStopped)
        NeoForge.EVENT_BUS.addListener(SableAssemblyBridge::onServerStopped)
        at.petrak.hexcasting.api.casting.eval.CastingEnvironment.addCreateEventListener { env, _ ->
            val isWispEnvironment =
                ModList.get().isLoaded("hexal") && HexalEnvironmentInterop.isWispEnvironment(env)
            env.addExtension(SableAmbitComponent(env, remapRange = !isWispEnvironment))
            if (isWispEnvironment) HexalEnvironmentInterop.addWispAmbit(env)
        }
        if (
            ModList.get().isLoaded("patchouli") &&
            listOf("moreiotas", "hexal", "hexical").any(ModList.get()::isLoaded)
        ) {
            PatchouliInterop.enableAnyInteropFlag()
        }
        HexxySable.LOGGER.info("HexxySable NeoForge compatibility port initialized")
    }

    private fun registerHexContent(event: RegisterEvent) {
        when (event.registryKey) {
            HexRegistries.ACTION -> {
                val registrar = { id: net.minecraft.resources.ResourceLocation, entry: ActionRegistryEntry ->
                    event.register(HexRegistries.ACTION, id) { entry }
                }
                HexxySablePatterns.registerAll(registrar)
                if (ModList.get().isLoaded("moreiotas")) HexxySablePatterns.registerMoreIotas(registrar)
                if (ModList.get().isLoaded("hexal")) HexalPatterns.register(registrar)
            }
            HexRegistries.IOTA_TYPE -> event.register(HexRegistries.IOTA_TYPE, HexxySable.id("ship")) { ShipIota.TYPE }
        }
    }
}
