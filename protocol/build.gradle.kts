import com.jetbrains.rd.generator.gradle.RdGenTask

plugins {
    alias(libs.plugins.kotlin)
    id("com.jetbrains.rdgen") version libs.versions.rdGen
}

repositories {
    maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
    maven("https://cache-redirector.jetbrains.com/maven-central")
}

val repoRoot: File = projectDir.parentFile

sourceSets {
    main {
        kotlin {
            srcDir(repoRoot.resolve("protocol/src/main/kotlin/model"))
        }
    }
}

rdgen {
    verbose = true
    packages = "model"

    generator {
        language = "kotlin"
        transform = "asis"
        root = "com.jetbrains.rider.model.nova.ide.IdeRoot"
        namespace = "me.seclerp.msbuild.devkit.rd"
        directory = file("$repoRoot/src/main/kotlin/me/seclerp/msbuild/devkit/rd").absolutePath
    }

    generator {
        language = "csharp"
        transform = "reversed"
        root = "com.jetbrains.rider.model.nova.ide.IdeRoot"
        namespace = "MSBuild.DevKit.Rd"
        directory = file("$repoRoot/src/dotnet/MSBuild.DevKit/Rd").absolutePath
    }
}

tasks.withType<RdGenTask> {
    dependsOn(sourceSets["main"].runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
}

dependencies {
    implementation(libs.rdGen)
    implementation(libs.kotlinStdLib)
    implementation(
        project(
            mapOf(
                "path" to ":",
                "configuration" to "riderModel"
            )
        )
    )
}