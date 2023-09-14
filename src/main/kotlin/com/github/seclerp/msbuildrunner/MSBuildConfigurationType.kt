package com.github.seclerp.msbuildrunner

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.NotNullLazyValue
import com.jetbrains.rider.model.RunnableProjectKind
import com.jetbrains.rider.run.configurations.RunnableProjectKinds


class MSBuildConfigurationType :
    ConfigurationTypeBase(
        ID,
        "MSBuild",
        "MSBuild run configuration type",
        NotNullLazyValue.createValue { AllIcons.Nodes.Console }
) {
    companion object {
        const val ID = "MSBuildConfiguration"
        fun isTypeApplicable(kind: RunnableProjectKind) =
            kind == RunnableProjectKinds.Console ||
            kind == RunnableProjectKinds.DotNetCore ||
            kind == RunnableProjectKinds.Web ||
            kind == RunnableProjectKinds.WcfService
    }

    init {
        addFactory(MSBuildConfigurationFactory(this))
    }
}
