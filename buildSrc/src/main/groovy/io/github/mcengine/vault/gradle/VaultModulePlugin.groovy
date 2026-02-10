package io.github.mcengine.vault.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.api.tasks.compile.JavaCompile
import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin

class VaultModulePlugin implements Plugin<Project> {
    void apply(Project project) {
        // Apply standard plugins
        project.pluginManager.apply(JavaPlugin)
        project.pluginManager.apply(ShadowPlugin)

        // 1. Calculate Version
        project.version = VersionCalculator.calculate(project)

        // 2. Java Config
        project.java {
            toolchain { languageVersion = JavaLanguageVersion.of(21) }
        }

        project.tasks.withType(JavaCompile).configureEach {
            options.encoding = 'UTF-8'
        }

        project.tasks.named('jar') {
            enabled = false // Disable standard JAR in favor of Shadow
        }

        // 3. Shadow Jar Config
        project.tasks.named('shadowJar') {
            String propName = "vault-${project.name}"

            // Determine Base Name
            String finalBaseName = project.name
            if (project.hasProperty("${propName}-name")) {
                finalBaseName = project.property("${propName}-name")
            }

            archiveBaseName.set(finalBaseName)
            archiveVersion.set(project.version.toString())
            archiveClassifier.set('')
            archiveFileName.set("${finalBaseName}-${project.version}.jar")

            // --- ENGINE SPECIFIC CLEANUP LOGIC ---
            if (project.path == ':') {
                String pluginBuildPath = System.getenv("MCPLUGIN_BUILD_PATH")
                if (pluginBuildPath != null && !pluginBuildPath.isEmpty()) {
                    def outputDir = project.file(pluginBuildPath)
                    destinationDirectory.set(outputDir)

                    doFirst {
                        if (outputDir.exists()) {
                            String baseName = archiveBaseName.get()
                            outputDir.listFiles()?.each { f ->
                                if (f.name.startsWith("${baseName}-") && f.name.endsWith(".jar")) {
                                    println "[Clean] Deleting old plugin version: ${f.name}"
                                    f.delete()
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Hook Shadow into Lifecycle
        project.tasks.named('assemble') { dependsOn('shadowJar') }

        project.configurations {
            apiElements {
                outgoing.artifacts.clear()
                outgoing.artifact(project.tasks.named('shadowJar'))
            }
            runtimeElements {
                outgoing.artifacts.clear()
                outgoing.artifact(project.tasks.named('shadowJar'))
            }
        }
    }
}
