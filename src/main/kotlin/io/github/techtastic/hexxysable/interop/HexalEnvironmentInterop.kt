package io.github.techtastic.hexxysable.interop

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import io.github.techtastic.hexxysable.casting.HexalWispAmbitComponent
import ram.talia.hexal.api.casting.eval.env.WispCastEnv

/** Optional Hexal class boundary; call only after checking ModList. */
object HexalEnvironmentInterop {
    fun isWispEnvironment(env: CastingEnvironment): Boolean = env is WispCastEnv

    fun addWispAmbit(env: CastingEnvironment) {
        if (env is WispCastEnv) env.addExtension(HexalWispAmbitComponent(env))
    }
}
