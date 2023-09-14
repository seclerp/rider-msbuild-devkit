package com.github.seclerp.msbuildrunner

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project
import com.jetbrains.rider.run.configurations.RunnableProjectKinds
import com.jetbrains.rider.run.configurations.project.DotNetStartBrowserParameters

class MSBuildConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun getId(): String {
        return MSBuildConfigurationType.ID
    }

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return MSBuildRunConfiguration("MSBuild", project, this, MSBuildConfigurationParameters(
            project = project,
            exePath = "",
            programParameters = "",
            workingDirectory = "",
            envs = hashMapOf(),
            isPassParentEnvs = true,
            useExternalConsole = false,
            runtimeArguments = "",
            projectFilePath = "",
            trackProjectExePath = true,
            trackProjectArguments = true,
            trackProjectWorkingDirectory = true,
            projectKind = RunnableProjectKinds.None,
            projectTfm = "",
            startBrowserParameters = DotNetStartBrowserParameters(),
            runtimeType = null,
            targetsToExecute = ""
        ))
    }
}