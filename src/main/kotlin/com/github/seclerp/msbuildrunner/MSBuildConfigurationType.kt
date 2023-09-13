package com.github.seclerp.msbuildrunner

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.NotNullLazyValue


class MSBuildConfigurationType() :
    ConfigurationTypeBase(
        ID,
        "MSBuild",
        "MSBuild run configuration type",
        NotNullLazyValue.createValue { AllIcons.Nodes.Console }) {

        init {
            addFactory(MSBuildConfigurationFactory(this))
        }
    companion object {
        const val ID = "MSBuildConfiguration"
    }
}
