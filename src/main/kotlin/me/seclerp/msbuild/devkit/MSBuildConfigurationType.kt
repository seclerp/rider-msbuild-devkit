package me.seclerp.msbuild.devkit

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.NotNullLazyValue


class MSBuildConfigurationType :
    ConfigurationTypeBase(
        ID,
        "MSBuild",
        "MSBuild run configuration type",
        NotNullLazyValue.createValue { AllIcons.Nodes.Console }
) {
    companion object {
        const val ID = "MSBuildConfiguration"
    }

    init {
        addFactory(MSBuildConfigurationFactory(this))
    }
}
