package me.seclerp.msbuild.devkit.interactive

import com.intellij.execution.console.LanguageConsoleView
import com.intellij.execution.console.ProcessBackedConsoleExecuteActionHandler
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory
import com.intellij.openapi.project.Project

class MsBuildInteractiveConsole(
    project: Project
) : AbstractConsoleRunnerWithHistory<LanguageConsoleView>(project, "MSBuild Interactive", null) {
    override fun createConsoleView(): LanguageConsoleView {
        TODO("Not yet implemented")
    }

    override fun createProcess() = null

    override fun createProcessHandler(p0: Process?): OSProcessHandler {
        TODO("Not yet implemented")
    }

    override fun createExecuteActionHandler(): ProcessBackedConsoleExecuteActionHandler {
        TODO("Not yet implemented")
    }
}