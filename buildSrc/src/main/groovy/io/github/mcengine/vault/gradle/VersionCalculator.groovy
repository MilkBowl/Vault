package io.github.mcengine.vault.gradle

import org.gradle.api.Project

class VersionCalculator {
    static String calculate(Project project) {
        String propName = "vault-${project.name}"

        // 1. Detect Build Environment
        String buildNum = System.getenv("BUILD_NUMBER")
        String devRelease = System.getenv("DEV_RELEASE_VERSION")
        String releaseTag = System.getenv("RELEASE_VERSION")
        boolean isDevBuild = (buildNum != null && !buildNum.isEmpty())
        boolean isDevReleaseBuild = (devRelease != null && !devRelease.isEmpty())

        // 2. Calculate
        String calculatedVersion = "unspecified"

        if (releaseTag != null && !releaseTag.isEmpty()) {
            // RELEASE: yyyy.m.m
            calculatedVersion = releaseTag.replace("v", "")
        } else if (project.hasProperty("${propName}-version")) {
            def baseVersion = project.property("${propName}-version")
            def iteration = project.findProperty('project-iteration') ?: "1"

            if (isDevBuild || isDevReleaseBuild) {
                // CI: yyyy.m.m-build.{number}
                calculatedVersion = "${baseVersion}-build.${buildNum}"
            } else {
                // LOCAL: yyyy.m.m-{iteration}
                calculatedVersion = "${baseVersion}-${iteration}"
            }
        }

        // Debug Log
        println "[${project.name}] Build detected: " + (isDevBuild ? "CI" : "Local")
        println "[${project.name}] Final Version: ${calculatedVersion}"

        return calculatedVersion
    }
}
