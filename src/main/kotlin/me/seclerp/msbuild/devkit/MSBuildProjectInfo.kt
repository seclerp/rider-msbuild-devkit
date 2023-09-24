package me.seclerp.msbuild.devkit

import javax.swing.Icon

data class MSBuildProjectInfo(
    val name: String,
    val filePath: String,
    val directory: String,
    val icon: Icon
)
