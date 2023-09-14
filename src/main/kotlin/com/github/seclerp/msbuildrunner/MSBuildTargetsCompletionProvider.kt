package com.github.seclerp.msbuildrunner

import com.github.seclerp.msbuildrunner.rd.MsBuildTargetInfo
import com.intellij.ui.TextFieldWithAutoCompletionListProvider

class MSBuildTargetsCompletionProvider : TextFieldWithAutoCompletionListProvider<MsBuildTargetInfo>(mutableListOf()) {
    override fun getLookupString(item: MsBuildTargetInfo): String {
        return item.name
    }
}