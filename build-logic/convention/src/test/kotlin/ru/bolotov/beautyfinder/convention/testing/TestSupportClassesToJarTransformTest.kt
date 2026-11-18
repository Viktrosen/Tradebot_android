package ru.bolotov.beautyfinder.convention.testing

import java.nio.file.Files
import java.util.jar.JarFile
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class TestSupportClassesToJarTransformTest {
    @get:Rule
    val temporary = TemporaryFolder()

    @Test
    fun `preserves class bytes Kotlin metadata and portable entry paths`() {
        val input = temporary.newFolder("classes").toPath()
        val classFile = input.resolve("ru/bolotov/Helper.class")
        val metadata = input.resolve("META-INF/testing.kotlin_module")
        Files.createDirectories(classFile.parent)
        Files.createDirectories(metadata.parent)
        Files.write(classFile, byteArrayOf(1, 2, 3))
        Files.write(metadata, byteArrayOf(4, 5))
        val output = temporary.root.toPath().resolve("support.jar")

        TestSupportClassesToJarTransform.writeJar(inputDirectory = input, outputJar = output)

        JarFile(output.toFile()).use { archive ->
            assertEquals(listOf("META-INF/testing.kotlin_module", "ru/bolotov/Helper.class"), archive.entries().toList().map { it.name })
            archive.getInputStream(archive.getJarEntry("ru/bolotov/Helper.class")).use {
                assertArrayEquals(byteArrayOf(1, 2, 3), it.readAllBytes())
            }
            archive.getInputStream(archive.getJarEntry("META-INF/testing.kotlin_module")).use {
                assertArrayEquals(byteArrayOf(4, 5), it.readAllBytes())
            }
        }
        // The writer and reader must release their own handles, including on Windows.
        TestSupportClassesToJarTransform.writeJar(inputDirectory = input, outputJar = output)
    }

    @Test
    fun `unchanged classes produce byte identical archives without source timestamps`() {
        val input = temporary.newFolder("classes").toPath()
        Files.write(input.resolve("Helper.class"), byteArrayOf(1, 2, 3))
        val first = temporary.root.toPath().resolve("first.jar")
        val second = temporary.root.toPath().resolve("second.jar")
        TestSupportClassesToJarTransform.writeJar(inputDirectory = input, outputJar = first)
        TestSupportClassesToJarTransform.writeJar(inputDirectory = input, outputJar = second)
        assertArrayEquals(Files.readAllBytes(first), Files.readAllBytes(second))
    }
}
