package me.seclerp.msbuild.devkit.interactive

import com.intellij.execution.console.BaseConsoleExecuteActionHandler
import com.intellij.execution.console.LanguageConsoleBuilder
import com.intellij.execution.console.LanguageConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowFactory
import me.seclerp.msbuild.devkit.MSBuildDevKitBundle
import me.seclerp.msbuild.devkit.psi.MSBuildLanguage

class MSBuildConsoleToolWindowFactory : ToolWindowFactory {
    override val anchor = ToolWindowAnchor.BOTTOM

    override fun init(toolWindow: ToolWindow) {
        toolWindow.title = MSBuildDevKitBundle.message("msbuild.interactive.toolwindow.name")
        toolWindow.stripeTitle = MSBuildDevKitBundle.message("msbuild.interactive.toolwindow.name")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentManager = toolWindow.contentManager
        val factory = contentManager.factory
        val console = createConsole(project)
        val content = factory.createContent(console.component,
            MSBuildDevKitBundle.message("msbuild.interactive.tab.name"), true)
        contentManager.addContent(content)
        toolWindow.activate {
            contentManager.setSelectedContent(content)
        }
    }

    @Suppress("UnstableApiUsage")
    private fun createConsole(project: Project): LanguageConsoleView =
        LanguageConsoleBuilder()
            .oneLineInput()
            .executionEnabled { true }
            .initActions(object : BaseConsoleExecuteActionHandler(true) {
                override fun execute(text: String, console: LanguageConsoleView) {
                    console.print("Reply\n", ConsoleViewContentType.NORMAL_OUTPUT)
                }
            }, "msbuild-evaluate")
            .build(project, MSBuildLanguage)
}

