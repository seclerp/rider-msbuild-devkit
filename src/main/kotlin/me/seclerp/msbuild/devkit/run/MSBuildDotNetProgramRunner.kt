package me.seclerp.msbuild.devkit.run

import com.intellij.execution.configurations.RunProfile
import com.jetbrains.rider.debugger.DotNetProgramRunner
import me.seclerp.msbuild.devkit.run.configurations.MSBuildRunConfiguration

class MSBuildDotNetProgramRunner : DotNetProgramRunner() {
    override fun canRun(executorId: String, runConfiguration: RunProfile): Boolean {
        return runConfiguration is MSBuildRunConfiguration
    }
}