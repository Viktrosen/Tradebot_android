package ru.bolotov.beautyfinder.convention.testing

import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import org.gradle.api.artifacts.transform.CacheableTransform
import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * AGP needs JARs for compilation. Materialize test-support classes in Gradle's
 * content-addressed transform cache, not in a mutable project build/libs JAR
 * that an IDE coverage classloader can keep mapped on Windows.
 */
@CacheableTransform
abstract class TestSupportClassesToJarTransform : TransformAction<TransformParameters.None> {
    @get:InputArtifact
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputArtifact: Provider<FileSystemLocation>

    override fun transform(outputs: TransformOutputs) {
        writeJar(
            inputDirectory = inputArtifact.get().asFile.toPath(),
            outputJar = outputs.file("test-support.jar").toPath()
        )
    }

    companion object {
        const val ARTIFACT_TYPE = "tradebot-test-support-classes"

        internal fun writeJar(inputDirectory: Path, outputJar: Path) {
            JarOutputStream(Files.newOutputStream(outputJar)).use { archive ->
                Files.walk(inputDirectory).use { files ->
                    files.filter { Files.isRegularFile(it) }.sorted().forEach { file ->
                        val relativePath = inputDirectory.relativize(file).toString().replace('\\', '/')
                        archive.putNextEntry(JarEntry(relativePath).apply { time = 0L })
                        Files.newInputStream(file).use { it.copyTo(archive) }
                        archive.closeEntry()
                    }
                }
            }
        }
    }
}
