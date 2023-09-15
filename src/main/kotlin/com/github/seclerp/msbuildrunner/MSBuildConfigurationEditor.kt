package com.github.seclerp.msbuildrunner

import com.github.seclerp.msbuildrunner.components.*
import com.github.seclerp.msbuildrunner.rd.MsBuildProjectInfo
import com.github.seclerp.msbuildrunner.rd.msBuildRunnerModel
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.platform.backend.workspace.WorkspaceModel
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.jetbrains.rd.framework.impl.startAndAdviseSuccess
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.IProperty
import com.jetbrains.rd.util.reactive.Property
import com.jetbrains.rd.util.reactive.ViewableList
import com.jetbrains.rd.util.reactive.adviseOnce
import com.jetbrains.rdclient.util.idea.toVirtualFile
import com.jetbrains.rider.model.RdProjectDescriptor
import com.jetbrains.rider.model.RunnableProject
import com.jetbrains.rider.model.runnableProjectsModel
import com.jetbrains.rider.projectView.solution
import com.jetbrains.rider.projectView.workspace.getProjectModelEntities
import com.jetbrains.rider.projectView.workspace.isProject
import com.jetbrains.rider.run.RiderRunBundle
import com.jetbrains.rider.run.configurations.LifetimedSettingsEditor
import com.jetbrains.rider.run.configurations.controls.runtimeSelection.RuntimeSelector
import javax.swing.JComponent

class MSBuildConfigurationEditor(private val project: Project) : LifetimedSettingsEditor<MSBuildRunConfiguration>() {
    private val targetsToExecute = Property("")
    private val targetProject = Property<RunnableProject?>(null)
    private val programArguments = Property("")
    private val envs: IProperty<Map<String, String>> = Property(hashMapOf())
    private val runtimeSelector = RuntimeSelector(RiderRunBundle.message("label.runtime"), "Runtime", project, project.lifetime)



    private val targetsCompletionProvider = MSBuildTargetsCompletionProvider()
    private val runnableProjects = ViewableList<RunnableProject>()
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

        targetProject.advise(project.lifetime) { runnableProject ->
            when (runnableProject) {
                null -> targetsCompletionProvider.setItems(emptyList())
                else -> {
                    val fileUrl = runnableProject.projectFilePath.toVirtualFile(true) ?: return@advise
                    val descriptor = WorkspaceModel.getInstance(project).getProjectModelEntities(fileUrl, project).firstOrNull { it.isProject() }?.descriptor as? RdProjectDescriptor ?: return@advise
                    project.solution.msBuildRunnerModel.getTargets.startAndAdviseSuccess(MsBuildProjectInfo(descriptor.originalGuid)) {
                        targetsCompletionProvider.setItems(it)
                    }
                }
            }
        }
    }

    override fun resetEditorFrom(configuration: MSBuildRunConfiguration) {
        targetsToExecute.value = configuration.parameters.targetsToExecute
        targetProject.value = getProjectByPath(configuration.parameters.projectFilePath)
        programArguments.value = configuration.parameters.programParameters
        envs.value = configuration.parameters.envs
        runtimeSelector.runtime.value = configuration.parameters.runtimeType
    }

    override fun applyEditorTo(configuration: MSBuildRunConfiguration) {
        panels.forEach { it.apply() }
        configuration.parameters.targetsToExecute = targetsToExecute.value
        configuration.parameters.projectFilePath = targetProject.value?.projectFilePath ?: ""
        configuration.parameters.envs = envs.value
        configuration.parameters.programParameters = programArguments.value
        configuration.parameters.runtimeType = runtimeSelector.runtime.value
    }

    override fun createEditor(lifetime: Lifetime): JComponent {
        val panel = panel {
            row("MSBuild Targets") {
                textFieldWithCompletion(project, targetsCompletionProvider)
                    .bindText(targetsToExecute, lifetime)
                    .comment("When multiple targets should be executed, use Space as separator.")
                    .align(AlignX.FILL)
            }
            row("Project") {
                projectSelector(project)
                    .bindItem(targetProject, lifetime)
                    .bindItems(runnableProjects, lifetime)
                    .align(AlignX.FILL)
            }
            row("Program arguments") {
                commandLineArgsEditor()
                    .bindText(programArguments, lifetime)
                    .align(AlignX.FILL)
            }
            row("Environment variables") {
                envVarsEditor()
                    .bindItems(envs, lifetime)
                    .align(AlignX.FILL)
            }
            row("Runtime") {
                runtimeSelector(project, runtimeSelector, lifetime)
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
