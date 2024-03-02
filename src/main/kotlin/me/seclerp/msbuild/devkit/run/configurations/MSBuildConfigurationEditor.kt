package me.seclerp.msbuild.devkit.run.configurations

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.platform.backend.workspace.WorkspaceModel
import com.intellij.platform.backend.workspace.virtualFile
import com.intellij.platform.workspace.storage.url.VirtualFileUrlManager
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.EmptyIcon
import com.intellij.workspaceModel.ide.getInstance
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.IProperty
import com.jetbrains.rd.util.reactive.Property
import com.jetbrains.rd.util.reactive.ViewableList
import com.jetbrains.rdclient.util.idea.toVirtualFile
import com.jetbrains.rider.projectView.calculateIcon
import com.jetbrains.rider.projectView.solution
import com.jetbrains.rider.projectView.workspace.*
import com.jetbrains.rider.run.RiderRunBundle
import com.jetbrains.rider.run.configurations.LifetimedSettingsEditor
import com.jetbrains.rider.run.configurations.controls.runtimeSelection.RuntimeSelector
import me.seclerp.msbuild.devkit.MSBuildProjectInfo
import me.seclerp.msbuild.devkit.components.*
import me.seclerp.msbuild.devkit.editor.MSBuildTargetsCompletionProvider
import javax.swing.JComponent
import kotlin.jvm.internal.Intrinsics

class MSBuildConfigurationEditor(private val project: Project) : LifetimedSettingsEditor<MSBuildRunConfiguration>() {
    private val targetsToExecute = Property("")
    private val targetProject = Property<MSBuildProjectInfo?>(null)
    private val programArguments = Property("")
    private val envs: IProperty<Map<String, String>> = Property(hashMapOf())
    private val runtimeSelector = RuntimeSelector(RiderRunBundle.message("label.runtime"), "Runtime", project, project.lifetime)

    private val msbuildPath by lazy { project.solution.activeMsBuildPath.value }
    private val targetsCompletionProvider = MSBuildTargetsCompletionProvider(project)
    private val runnableProjects = ViewableList<MSBuildProjectInfo>()
    private val panels = mutableSetOf<DialogPanel>()

    init {
        val model = WorkspaceModel.getInstance(project);

        val projects = listOfNotNull(model.getSolutionEntity())
            .plus(model.findProjects().filter { it.isProject() })
            .map { it.toProjectInfo() }

        runnableProjects.clear()
        runnableProjects.addAll(projects)

        targetProject.advise(project.lifetime, targetsCompletionProvider::setProject)
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
        configuration.parameters.apply {
            exePath = msbuildPath ?: ""
            workingDirectory = targetProject.value?.directory ?: ""
            assemblyToDebug = project.solution.activeMsBuildPath.value ?: ""
            envs = this@MSBuildConfigurationEditor.envs.value
            programParameters = programArguments.value
            runtimeType = runtimeSelector.runtime.value

            targetsToExecute = this@MSBuildConfigurationEditor.targetsToExecute.value
            projectFilePath = targetProject.value?.filePath ?: ""
        }
    }

    override fun createEditor(lifetime: Lifetime): JComponent {
        val panel = panel {
            row("MSBuild Targets") {
                textFieldWithCompletion(project, targetsCompletionProvider)
                    .bindText(targetsToExecute, lifetime)
                    .comment("When multiple targets should be executed, use Space as separator.")
                    .align(AlignX.FILL)
            }
            row("Project / Solution") {
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
        val model = WorkspaceModel.getInstance(project)
        val virtualFile = path.toVirtualFile(true) ?: return null

        val solutionEntity = model.getSolutionEntity()
        if (Intrinsics.areEqual(virtualFile, solutionEntity?.url?.virtualFile)) {
            return solutionEntity?.toProjectInfo();
        }

        val proj = model.findProjectsByPath(virtualFile).firstOrNull { it.isProject() } ?: return null
        return proj.toProjectInfo()
    }

    private fun ProjectModelEntity.toProjectInfo(): MSBuildProjectInfo {
        val icon = calculateIcon(project) ?: EmptyIcon.ICON_16
        return MSBuildProjectInfo(name, getFile()?.path ?: "", this.getContentRootUrl(VirtualFileUrlManager.getInstance(project))?.virtualFile?.path ?: "", icon)
    }
}
