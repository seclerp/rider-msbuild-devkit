package me.seclerp.msbuild.devkit

import me.seclerp.msbuild.devkit.rd.MsBuildTargetInfo
import com.intellij.ui.TextFieldWithAutoCompletionListProvider

class MSBuildTargetsCompletionProvider : TextFieldWithAutoCompletionListProvider<MsBuildTargetInfo>(mutableListOf()) {
    override fun getLookupString(item: MsBuildTargetInfo): String {
        return item.name
    }
}