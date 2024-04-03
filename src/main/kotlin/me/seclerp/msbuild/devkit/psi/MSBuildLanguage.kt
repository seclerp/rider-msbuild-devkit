package me.seclerp.msbuild.devkit.psi

import com.intellij.lang.Language
import me.seclerp.msbuild.devkit.MSBuildDevKitBundle

object MSBuildLanguage : Language("MSBuildDevKitLanguage") {
    override fun getDisplayName() = MSBuildDevKitBundle.message("msbuild.language.name")
}