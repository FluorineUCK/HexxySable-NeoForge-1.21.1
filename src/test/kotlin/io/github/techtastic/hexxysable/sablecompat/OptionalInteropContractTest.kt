package io.github.techtastic.hexxysable.sablecompat

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class OptionalInteropContractTest {
    private val root: Path = Path.of(System.getProperty("user.dir"))

    @Test
    fun `Hexal wisps are retained inside Sable sublevels`() {
        val tag = requiredText("src/main/resources/data/sable/tags/entity_type/retain_in_sub_level.json")
        listOf("hexal:wisp/wandering", "hexal:wisp/projectile", "hexal:wisp/ticking").forEach {
            assertTrue(tag.contains(it), "Sable retention tag must contain $it")
        }
    }

    @Test
    fun `Hexal embark and disembark retain upstream patterns`() {
        val patterns = requiredText(
            "src/main/kotlin/io/github/techtastic/hexxysable/registry/HexalPatterns.kt",
        )
        assertTrue(patterns.contains("\"embark\""))
        assertTrue(patterns.contains("\"wdewwedwawwdewdwewd\""))
        assertTrue(patterns.contains("\"disembark\""))
        assertTrue(patterns.contains("\"wdewwedwawwqawqwawq\""))
    }

    @Test
    fun `Hexal entity transfer avoids Sable temporary paired local-space helpers`() {
        val transfer = requiredText(
            "src/main/kotlin/io/github/techtastic/hexxysable/sablecompat/SableEntityTransfer.kt",
        )
        assertTrue(transfer.contains("EntityCoordinateTransfer.embark"))
        assertTrue(transfer.contains("EntityCoordinateTransfer.disembark"))
        assertTrue(!transfer.contains("SubLevelHelper.pushEntityLocal"))
        assertTrue(!transfer.contains("SubLevelHelper.popEntityLocal"))
    }

    @Test
    fun `Hexical optional mixins are declared`() {
        val metadata = requiredText("src/main/resources/META-INF/neoforge.mods.toml")
        val config = requiredText("src/main/resources/hexxysable-hexical.mixins.json")
        assertTrue(metadata.contains("config=\"hexxysable-hexical.mixins.json\""))
        assertTrue(metadata.contains("requiredMods=[\"hexical\"]"))
        assertTrue(config.contains("MixinPedestalBlockEntity"))
        assertTrue(config.contains("MixinLesserSentinelRenderer"))
    }

    @Test
    fun `MoreIotas optional mixin is loader gated`() {
        val metadata = requiredText("src/main/resources/META-INF/neoforge.mods.toml")
        val config = requiredText("src/main/resources/hexxysable-moreiotas.mixins.json")
        assertTrue(metadata.contains("config=\"hexxysable-moreiotas.mixins.json\""))
        assertTrue(metadata.contains("requiredMods=[\"moreiotas\"]"))
        assertTrue(config.contains("MixinOpNameGet"))
    }

    @Test
    fun `Hexal and Hexical Patchouli entries are restored`() {
        val base = "src/main/resources/assets/hexcasting/patchouli_books/thehexbook/en_us/entries/interop"
        val hexal = requiredText("$base/hexal_navimancy.json")
        val hexical = requiredText("$base/hexical_navimancy.json")
        assertTrue(hexal.contains("hexxysable:embark"))
        assertTrue(hexal.contains("hexxysable:disembark"))
        assertTrue(hexical.contains("hexxysable:assemble"))
    }

    @Test
    fun `Hexal remains an optional declared dependency`() {
        val metadata = requiredText("src/main/resources/META-INF/neoforge.mods.toml")
        val start = metadata.indexOf("modId=\"hexal\"")
        assertTrue(start >= 0, "Hexal dependency block must exist")
        val block = metadata.substring(start, minOf(metadata.length, start + 180))
        assertTrue(block.contains("type=\"optional\""), "Hexal dependency must remain optional")
    }

    private fun requiredText(relative: String): String {
        val path = root.resolve(relative)
        assertTrue(Files.isRegularFile(path), "$relative must exist")
        return Files.readString(path)
    }
}
