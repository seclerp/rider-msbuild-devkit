package me.seclerp.msbuild.devkit.editor

import com.intellij.openapi.project.Project
import com.intellij.platform.backend.workspace.WorkspaceModel
import me.seclerp.msbuild.devkit.rd.MsBuildTargetInfo
import com.intellij.ui.TextFieldWithAutoCompletionListProvider
import com.jetbrains.rd.framework.impl.startAndAdviseSuccess
import com.jetbrains.rdclient.util.idea.toVirtualFile
import com.jetbrains.rider.model.RdProjectDescriptor
import com.jetbrains.rider.projectView.solution
import com.jetbrains.rider.projectView.workspace.getProjectModelEntities
import com.jetbrains.rider.projectView.workspace.isProject
import me.seclerp.msbuild.devkit.MSBuildProjectInfo
import me.seclerp.msbuild.devkit.rd.MsBuildProjectInfo
import me.seclerp.msbuild.devkit.rd.msBuildRunnerModel

class MSBuildTargetsCompletionProvider(private val project: Project) : TextFieldWithAutoCompletionListProvider<MsBuildTargetInfo>(mutableListOf()) {
    fun setProject(info: MSBuildProjectInfo?) {
        when (info) {
            null -> setItems(emptyList())
            else -> {
                val fileUrl = info.filePath.toVirtualFile(true) ?: return
                val dotnetProjectEntity = WorkspaceModel.getInstance(project).getProjectModelEntities(fileUrl, project).firstOrNull { it.isProject() } ?: return
                val dotnetProjectDescriptor = dotnetProjectEntity.descriptor as? RdProjectDescriptor ?: return
                project.solution.msBuildRunnerModel.getTargets.startAndAdviseSuccess(MsBuildProjectInfo(dotnetProjectDescriptor.originalGuid)) {
                    setItems(it)
                }
            }
        }
    }

    override fun getLookupString(item: MsBuildTargetInfo): String {
        return item.name
    }
}