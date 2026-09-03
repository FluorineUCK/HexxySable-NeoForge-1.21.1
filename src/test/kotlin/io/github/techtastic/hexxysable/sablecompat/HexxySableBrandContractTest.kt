package io.github.techtastic.hexxysable.sablecompat

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class HexxySableBrandContractTest {
    @Test
    fun `canonical metadata has branding without unsupported compatibility claims`() {
        val properties = Files.readString(Path.of("gradle.properties"))
        val settings = Files.readString(Path.of("settings.gradle"))
        val metadata = Files.readString(Path.of("src/main/resources/META-INF/neoforge.mods.toml"))
        val entrypoint = Files.readString(Path.of(
            "src/main/kotlin/io/github/techtastic/hexxysable/neo/HexxySableNeo.kt"
        ))

        assertTrue(properties.contains("archives_base_name=hexxysable-neoforge"))
        assertTrue(properties.contains("mod_version=2.0.2+1.21.1-neoforge-pre39"))
        assertTrue(settings.contains("rootProject.name = \"hexxysable-neoforge-port\""))
        assertTrue(metadata.contains("modId=\"hexxysable\""))
        assertTrue(metadata.contains("[[dependencies.hexxysable]]"))
        assertTrue(metadata.contains("displayName=\"HexxySable\""))
        assertTrue(metadata.contains("config=\"hexxysable.mixins.json\""))
        assertTrue(metadata.contains("description='''Hex Casting navimancy actions ported to NeoForge 1.21.1 and adapted to Sable sub-level physics.'''"))
        assertTrue(metadata.contains("versionRange=\"[0.12.0-devel-pre-39,)\""))
        assertFalse(metadata.contains("issueTrackerURL="))
        assertFalse(metadata.contains("displayURL="))
        assertFalse(metadata.contains("save compatibility", ignoreCase = true))
        assertTrue(entrypoint.contains("HexxySable NeoForge compatibility port initialized"))
    }
}
