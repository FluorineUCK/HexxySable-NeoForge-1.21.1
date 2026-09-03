package io.github.techtastic.hexxysable.interop

import at.petrak.hexcasting.interop.HexInterop
import io.github.techtastic.hexxysable.HexxySable

/** Reflection preserves Patchouli as an optional dependency while restoring Hex's interop category flag. */
object PatchouliInterop {
    fun enableAnyInteropFlag() {
        runCatching {
            val apiClass = Class.forName("vazkii.patchouli.api.PatchouliAPI")
            val api = apiClass.getMethod("get").invoke(null)
            api.javaClass
                .getMethod("setConfigFlag", String::class.java, Boolean::class.javaPrimitiveType)
                .invoke(api, HexInterop.PATCHOULI_ANY_INTEROP_FLAG, true)
        }.onFailure {
            HexxySable.LOGGER.warn("Could not enable Patchouli's Hex Casting interop flag", it)
        }
    }
}
