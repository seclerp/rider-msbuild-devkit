package com.github.seclerp.msbuildrunner

import com.github.seclerp.msbuildrunner.components.*
import com.github.seclerp.msbuildrunner.rd.MsBuildProjectInfo
import com.github.seclerp.msbuildrunner.rd.msBuildRunnerModel
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.platform.backend.workspace.WorkspaceModel
import com.intellij.platform.backend.workspace.virtualFile
import com.intellij.platform.workspace.storage.url.VirtualFileUrlManager
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.EmptyIcon
import com.intellij.workspaceModel.ide.getInstance
import com.jetbrains.rd.framework.impl.startAndAdviseSuccess
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.IProperty
import com.jetbrains.rd.util.reactive.Property
import com.jetbrains.rd.util.reactive.ViewableList
import com.jetbrains.rdclient.util.idea.toVirtualFile
import com.jetbrains.rider.model.RdProjectDescriptor
import com.jetbrains.rider.projectView.calculateIcon
import com.jetbrains.rider.projectView.solution
import com.jetbrains.rider.projectView.workspace.*
import com.jetbrains.rider.run.RiderRunBundle
import com.jetbrains.rider.run.configurations.LifetimedSettingsEditor
import com.jetbrains.rider.run.configurations.controls.runtimeSelection.RuntimeSelector
import javax.swing.JComponent

class MSBuildConfigurationEditor(private val project: Project) : LifetimedSettingsEditor<MSBuildRunConfiguration>() {
    private val targetsToExecute = Property("")
    private val targetProject = Property<MSBuildProjectInfo?>(null)
    private val programArguments = Property("")
    private val envs: IProperty<Map<String, String>> = Property(hashMapOf())
    private val runtimeSelector = RuntimeSelector(RiderRunBundle.message("label.runtime"), "Runtime", project, project.lifetime)

    private val msbuildPath by lazy { project.solution.activeMsBuildPath.value }
    private val targetsCompletionProvider = MSBuildTargetsCompletionProvider()
    private val runnableProjects = ViewableList<MSBuildProjectInfo>()
    private val panels = mutableSetOf<DialogPanel>()

    init {
        val projects = WorkspaceModel.getInstance(project).findProjects().filter { it.isProject() }.map { it.toProjectInfo() }
        runnableProjects.clear()
        runnableProjects.addAll(projects)

        targetProject.advise(project.lifetime) { proj ->
            when (proj) {
                null -> targetsCompletionProvider.setItems(emptyList())
                else -> {
                    val fileUrl = proj.filePath.toVirtualFile(true) ?: return@advise
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
        configuration.parameters.exePath = msbuildPath ?: ""
        configuration.parameters.workingDirectory = targetProject.value?.directory ?: ""
        configuration.parameters.assemblyToDebug = project.solution.activeMsBuildPath.value ?: ""
        configuration.parameters.targetsToExecute = targetsToExecute.value
        configuration.parameters.projectFilePath = targetProject.value?.filePath ?: ""
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

    private fun getProjectByPath(path: String): MSBuildProjectInfo? {
        val virtualFile = path.toVirtualFile(true) ?: return null
        val proj = WorkspaceModel.getInstance(project).findProjectsByPath(virtualFile).firstOrNull { it.isProject() } ?: return null
        return proj.toProjectInfo()
    }

    private fun ProjectModelEntity.toProjectInfo(): MSBuildProjectInfo {
        val icon = calculateIcon(project) ?: EmptyIcon.ICON_16
        return MSBuildProjectInfo(name, getFile()?.path ?: "", this.getContentRootUrl(VirtualFileUrlManager.getInstance(project))?.virtualFile?.path ?: "", icon)
    }
}
