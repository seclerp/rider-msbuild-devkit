package me.seclerp.msbuild.devkit.run.configurations

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMExternalizerUtil
import com.jetbrains.rider.run.configurations.RunConfigurationHelper
import com.jetbrains.rider.run.configurations.dotNetExe.DotNetExeConfigurationParameters
import com.jetbrains.rider.run.configurations.exe.ProcessExecutionDetails
import com.jetbrains.rider.runtime.DotNetExecutable
import com.jetbrains.rider.runtime.DotNetRuntimeType
import org.jdom.Element

class MSBuildConfigurationParameters(
    project: Project,
    exePath: String,
    programParameters: String,
    workingDirectory: String,
    envs: Map<String, String>,
    isPassParentEnvs: Boolean,
    useExternalConsole: Boolean,
    runtimeArguments: String,
    runtimeType: DotNetRuntimeType?,
    var executeAsIs: Boolean,
    var assemblyToDebug: String?,
    var projectFilePath: String,
    var targetsToExecute: String
) : DotNetExeConfigurationParameters(project,
    exePath,
    programParameters,
    workingDirectory,
    envs,
    isPassParentEnvs,
    useExternalConsole,
    executeAsIs,
    assemblyToDebug,
    runtimeArguments,
    runtimeType) {

    companion object {
        private const val MSBUILD_TARGETS = "MSBUILD_TARGETS"
        private const val PROJECT_FILE_PATH = "PROJECT_FILE_PATH"
    }

    override suspend fun toDotNetExecutableSuspending(details: ProcessExecutionDetails): DotNetExecutable {
        val base = super.toDotNetExecutableSuspending(details)
        // TODO: Generate MSBuild executable
        val parameters = buildList {
            add(projectFilePath)
            add(buildString {
                append("-t:")
                append(targetsToExecute.replace(" ", ";"))
            })
            add(base.programParameterString)
        }.joinToString(" ")

        return base.copy(
            programParameterString = parameters,
            executeAsIs = false
        )
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        targetsToExecute = JDOMExternalizerUtil.readField(element, MSBUILD_TARGETS) ?: ""
        projectFilePath = JDOMExternalizerUtil.readField(element, PROJECT_FILE_PATH) ?: ""
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        JDOMExternalizerUtil.writeField(element, MSBUILD_TARGETS, targetsToExecute)
        JDOMExternalizerUtil.writeField(element, PROJECT_FILE_PATH, projectFilePath)
    }

    override fun copy(): MSBuildConfigurationParameters {
        return MSBuildConfigurationParameters(
            project = project,
            exePath = exePath,
            programParameters = programParameters,
            workingDirectory = workingDirectory,
            envs = RunConfigurationHelper.copyEnvs(envs),
            isPassParentEnvs = isPassParentEnvs,
            useExternalConsole = useExternalConsole,
            executeAsIs = executeAsIs,
            assemblyToDebug = assemblyToDebug,
            runtimeArguments = runtimeArguments,
            runtimeType = runtimeType,
            //
            targetsToExecute = targetsToExecute,
            projectFilePath = projectFilePath
        )
    }

}