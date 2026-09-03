package io.github.techtastic.hexxysable.sablecompat

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class AssemblyBlacklistContractTest {
    private val root: Path = Path.of(System.getProperty("user.dir"))

    @Test
    fun `assembly uses a data driven blacklist with upstream defaults`() {
        val source = requiredText(
            "src/main/kotlin/io/github/techtastic/hexxysable/casting/patterns/spells/OpAssemble.kt",
        )
        assertTrue(source.contains("HexxySableBlockTags.ASSEMBLE_BLACKLIST"))

        val tag = requiredText(
            "src/main/resources/data/hexxysable/tags/block/assemble_blacklist.json",
        )
        listOf(
            "minecraft:air",
            "minecraft:bedrock",
            "minecraft:barrier",
            "minecraft:end_gateway",
            "minecraft:end_portal",
            "minecraft:end_portal_frame",
            "minecraft:command_block",
            "minecraft:chain_command_block",
            "minecraft:repeating_command_block",
            "minecraft:structure_block",
            "minecraft:structure_void",
            "computercraft:computer_command",
        ).forEach { id ->
            assertTrue(tag.contains(id), "assembly blacklist must contain $id")
        }
        assertTrue(tag.contains("\"required\": false"))
    }

    private fun requiredText(relative: String): String {
        val path = root.resolve(relative)
        assertTrue(Files.isRegularFile(path), "$relative must exist")
        return Files.readString(path)
    }
}
