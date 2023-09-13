package com.github.seclerp.msbuildrunner.executors

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.jetbrains.rider.model.dotNetActiveRuntimeModel
import com.jetbrains.rider.projectView.solution
import com.jetbrains.rider.projectView.solutionDirectoryPath
import com.jetbrains.rider.run.FormatPreservingCommandLine
import org.jetbrains.annotations.NonNls
import java.io.File
import java.nio.charset.Charset

class MSBuildCommandBuilder(private val project: Project) {
    private val activeRuntime by lazy { project.solution.dotNetActiveRuntimeModel.activeRuntime.valueOrNull }
    protected val solutionDirectory = project.solutionDirectoryPath.toString()

    private fun getDotnetExePath() =
        activeRuntime?.dotNetCliExePath
            ?: throw Exception(".NET / .NET Core is not configured, unable to run commands.")

    private fun getDotnetRootPath() = File(getDotnetExePath()).parent

    @NonNls
    private var generalCommandLine: GeneralCommandLine =
        FormatPreservingCommandLine()
            .withExePath(getDotnetExePath())
            .withCharset(Charset.forName("UTF-8"))
            .withWorkDirectory(solutionDirectory)
            .withEnvironment("DOTNET_ROOT", getDotnetRootPath())

    fun build() = generalCommandLine

    fun add(value: String) {
        generalCommandLine = generalCommandLine.withParameters(value)
    }
}


