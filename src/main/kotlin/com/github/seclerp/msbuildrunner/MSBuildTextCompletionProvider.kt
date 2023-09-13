package com.github.seclerp.msbuildrunner

import com.github.seclerp.msbuildrunner.rd.MsBuildProjectInfo
import com.github.seclerp.msbuildrunner.rd.msBuildRunnerModel
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.CharFilter
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.platform.backend.workspace.WorkspaceModel
import com.intellij.util.textCompletion.TextCompletionProvider
import com.jetbrains.rider.model.RdProjectDescriptor
import com.jetbrains.rider.projectView.solution
import com.jetbrains.rider.projectView.workspace.findProjects

class MSBuildTextCompletionProvider(val project: Project) : TextCompletionProvider {

    override fun getAdvertisement(): String? {
        return null
    }

    override fun getPrefix(text: String, offset: Int): String {
        return getPrefix(text.substring(0, offset))
    }

    private fun getPrefix(currentTextPrefix: String): String {
        return currentTextPrefix
    }

    override fun applyPrefixMatcher(
        result: CompletionResultSet,
        prefix: String
    ): CompletionResultSet {
        var activeResult = result
        if (activeResult.prefixMatcher.prefix != prefix) {
            activeResult = activeResult.withPrefixMatcher(prefix)
        }

        return activeResult
    }


    override fun acceptChar(c: Char): CharFilter.Result? {
        return null
    }


    override fun fillCompletionVariants(parameters: CompletionParameters, prefix: String, result: CompletionResultSet) {
        val projectId = WorkspaceModel.getInstance(project).findProjects().mapNotNull { it.descriptor as? RdProjectDescriptor }.map { it.originalGuid }.first()
        val projectInfo = MsBuildProjectInfo(projectId)
        val targets = project.solution.msBuildRunnerModel.getTargets.sync(projectInfo)
        val lookUpElements = targets.map {
            LookupElementBuilder.create(it).withPresentableText(it.name)
        }
        result.addAllElements(lookUpElements)
    }


}