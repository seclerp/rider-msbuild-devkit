package me.seclerp.msbuild.devkit.run.configurations

import com.intellij.openapi.options.SettingsEditorGroup
import com.intellij.openapi.project.Project
import com.jetbrains.rider.run.RiderRunBundle

class MSBuildConfigurationEditorGroup(project: Project) : SettingsEditorGroup<MSBuildRunConfiguration>() {
    init {
        addEditor(RiderRunBundle.message("tab.title.configuration"), MSBuildConfigurationEditor(project))
    }
}