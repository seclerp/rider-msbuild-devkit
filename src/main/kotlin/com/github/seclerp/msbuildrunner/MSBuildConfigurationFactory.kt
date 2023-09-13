package com.github.seclerp.msbuildrunner

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project

class MSBuildConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun getId(): String {
        return MSBuildConfigurationType.ID
    }

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return MSBuildRunConfiguration(project, this, "MSBuild")
    }

    override fun getOptionsClass(): Class<out BaseState> {
        return MSBuildRunConfigurationOptions::class.java
    }
}