package com.github.seclerp.msbuildrunner.components

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextField

// TODO: Implement with completion
object TargetsTextFieldFactory {
    fun create(project: Project): JBTextField {
        return JBTextField()
    }
}