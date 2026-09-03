package io.github.techtastic.hexxysable.sablecompat

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class SableCompanionDependencyContractTest {
    @Test
    fun `hard referenced companion API is a required runtime dependency`() {
        val metadata = Files.readString(Path.of("src/main/resources/META-INF/neoforge.mods.toml"))
        val build = Files.readString(Path.of("build.gradle"))
        val blockStart = metadata.indexOf("modId=\"sablecompanion\"")

        assertTrue(blockStart >= 0, "Sable Companion dependency block must exist")
        val blockEnd = metadata.indexOf("[[dependencies.hexxysable]]", blockStart + 1)
            .let { if (it < 0) metadata.length else it }
        val block = metadata.substring(blockStart, blockEnd)
        assertTrue(block.contains("type=\"required\""))
        assertTrue(block.contains("versionRange=\"[1.6.0,1.6.1)\""))
        assertTrue(build.contains(
            "runtimeOnly files(\"libs/sable-companion-common-${'$'}{project.minecraft_version}-1.6.0.jar\")"
        ))
    }
}

