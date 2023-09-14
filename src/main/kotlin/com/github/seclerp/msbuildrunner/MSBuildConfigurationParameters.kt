package com.github.seclerp.msbuildrunner

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMExternalizerUtil
import com.jetbrains.rider.model.RunnableProjectKind
import com.jetbrains.rider.run.configurations.RunConfigurationHelper
import com.jetbrains.rider.run.configurations.project.DotNetProjectConfigurationParameters
import com.jetbrains.rider.run.configurations.project.DotNetStartBrowserParameters
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
    projectFilePath: String,
    trackProjectExePath: Boolean,
    trackProjectArguments: Boolean,
    trackProjectWorkingDirectory: Boolean,
    projectKind: RunnableProjectKind,
    projectTfm: String,
    startBrowserParameters: DotNetStartBrowserParameters,
    runtimeType: DotNetRuntimeType?,
    var targetsToExecute: String,
) : DotNetProjectConfigurationParameters(project,
    exePath,
    programParameters,
    workingDirectory,
    envs,
    isPassParentEnvs,
    useExternalConsole,
    runtimeArguments,
    projectFilePath,
    trackProjectExePath,
    trackProjectArguments,
    trackProjectWorkingDirectory,
    projectKind,
    projectTfm,
    startBrowserParameters,
    runtimeType) {

    companion object {
        private const val MSBUILD_TARGETS = "MSBUILD_TARGETS"
    }

    override fun toDotNetExecutable(): DotNetExecutable {
        // TODO: Generate MSBuild executable
        return super.toDotNetExecutable()
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        targetsToExecute = JDOMExternalizerUtil.readField(element, MSBUILD_TARGETS) ?: ""
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        JDOMExternalizerUtil.writeField(element, MSBUILD_TARGETS, targetsToExecute)
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
            runtimeArguments = runtimeArguments,
            projectFilePath = projectFilePath,
            trackProjectExePath = trackProjectExePath,
            trackProjectArguments = trackProjectArguments,
            trackProjectWorkingDirectory = trackProjectWorkingDirectory,
            projectKind = projectKind,
            projectTfm = projectTfm,
            startBrowserParameters = startBrowserParameters.copy(),
            runtimeType = runtimeType,
            //
            targetsToExecute = targetsToExecute
        )
    }

}