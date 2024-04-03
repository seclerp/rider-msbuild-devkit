package me.seclerp.msbuild.devkit.psi

import com.intellij.openapi.fileTypes.LanguageFileType
import me.seclerp.msbuild.devkit.MSBuildDevKitBundle

class MSBuildFileType : LanguageFileType(MSBuildLanguage) {
    override fun getName() = MSBuildDevKitBundle.message("msbuild.language.filename")

    override fun getDescription() = ""

    override fun getDefaultExtension() = ""

    override fun getIcon() = null
}