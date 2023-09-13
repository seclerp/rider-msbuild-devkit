package com.github.seclerp.msbuildrunner

import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.openapi.components.StoredProperty


class MSBuildRunConfigurationOptions: RunConfigurationOptions() {
    private val _scriptName: StoredProperty<String?> = string("").provideDelegate(this, "scriptName")
    var scriptName: String
        get() = _scriptName.getValue(this)!!
        set(scriptName) {
            _scriptName.setValue(this, scriptName)
        }

}