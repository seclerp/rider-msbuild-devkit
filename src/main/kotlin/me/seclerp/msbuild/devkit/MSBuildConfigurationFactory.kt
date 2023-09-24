package me.seclerp.msbuild.devkit

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import com.jetbrains.rider.projectView.solution

class MSBuildConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun getId(): String {
        return MSBuildConfigurationType.ID
    }

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return MSBuildRunConfiguration("MSBuild", project, this, MSBuildConfigurationParameters(
            project = project,
            exePath = project.solution.activeMsBuildPath.value ?: "",
            programParameters = "",
            workingDirectory = "",
            envs = hashMapOf(),
            isPassParentEnvs = true,
            useExternalConsole = false,
            executeAsIs = false,
            assemblyToDebug = null,
            runtimeArguments = "",
            runtimeType = null,

            targetsToExecute = "",
            projectFilePath = ""
        ))
    }
}