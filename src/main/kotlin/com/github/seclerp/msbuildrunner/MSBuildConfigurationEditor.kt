package com.github.seclerp.msbuildrunner

import com.github.seclerp.msbuildrunner.components.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rd.swing.addLifetimedItem
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.Property
import com.jetbrains.rd.util.reactive.ViewableList
import com.jetbrains.rd.util.reactive.adviseOnce
import com.jetbrains.rider.model.RunnableProject
import com.jetbrains.rider.model.RunnableProjectsModel
import com.jetbrains.rider.model.runnableProjectsModel
import com.jetbrains.rider.projectView.solution
import com.jetbrains.rider.run.configurations.LifetimedSettingsEditor
import com.jetbrains.rider.run.configurations.controls.bindTo
import com.jetbrains.rider.run.configurations.runnableProjectsModelIfAvailable
import javax.swing.JComponent

class MSBuildConfigurationEditor(private val project: Project) : LifetimedSettingsEditor<MSBuildRunConfiguration>() {
    private val targetsToExecute = Property("")
    private val runnableProjects = ViewableList<RunnableProject>()
    private val targetProject = Property<RunnableProject?>(null)
    private val panels = mutableSetOf<DialogPanel>()

    init {
        val runnableProjectsModel = project.solution.runnableProjectsModel
        runnableProjectsModel.projects.adviseOnce(project.lifetime) {
            runnableProjectsModel.projects.view(project.lifetime) { projectListLt, projectList ->
                val items = projectList.filter { MSBuildConfigurationType.isTypeApplicable(it.kind) }.sortedBy { p -> p.fullName }
                runnableProjects.addAll(items)
                projectListLt.onTermination {
                    runnableProjects.clear()
                }
            }
        }
    }

    override fun resetEditorFrom(configuration: MSBuildRunConfiguration) {
        targetsToExecute.value = configuration.parameters.targetsToExecute
        targetProject.value = getProjectByPath(configuration.parameters.projectFilePath)
    }

    override fun applyEditorTo(configuration: MSBuildRunConfiguration) {
        panels.forEach { it.apply() }
        configuration.parameters.targetsToExecute = targetsToExecute.value
        configuration.parameters.projectFilePath = targetProject.value?.projectFilePath ?: ""
    }

    override fun createEditor(lifetime: Lifetime): JComponent {
        val panel = panel {
            row("MSBuild Targets") {
                targetsTextField(project)
                    .bindText(targetsToExecute, lifetime)
                    .align(AlignX.FILL)
            }
            row("Project") {
                projectSelector(project)
                    .bindItem(targetProject, lifetime)
                    .bindItems(runnableProjects, lifetime)
                    .align(AlignX.FILL)
            }
        }
        lifetime.bracketIfAlive({ panels.add(panel) }, { panels.remove(panel) })
        return panel
    }

    private fun getProjectByPath(path: String): RunnableProject? {
        return project.solution.runnableProjectsModel.projects.valueOrNull?.firstOrNull { it.projectFilePath == path }
    }
}
