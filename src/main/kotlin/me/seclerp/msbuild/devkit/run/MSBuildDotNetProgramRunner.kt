package me.seclerp.msbuild.devkit.run

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.RunContentDescriptor
import com.jetbrains.rider.debugger.DotNetRunnerBase
import com.jetbrains.rider.run.RiderAsyncProgramRunner
import com.jetbrains.rider.run.configurations.RequiresPreparationRunProfileState
import com.jetbrains.rider.run.configurations.RiderAsyncRunProfileState
import me.seclerp.msbuild.devkit.run.configurations.MSBuildRunConfiguration

class MSBuildDotNetProgramRunner : RiderAsyncProgramRunner<RunnerSettings>(), DotNetRunnerBase {
    override fun canRun(executorId: String, runConfiguration: RunProfile): Boolean {
        return runConfiguration is MSBuildRunConfiguration
    }

    /**
     * It is a polyfill for a [executeAsync] method from a class [com.jetbrains.rider.debugger.DotNetProgramRunner]
     * originally from the Rider SDK. The [DotNetProgramRunner] class has been removed in 2026.2 SDK.
     */
    override suspend fun executeAsync(environment: ExecutionEnvironment, state: RunProfileState): RunContentDescriptor? {
        if (state is RequiresPreparationRunProfileState) {
            state.prepareExecution(environment)
        }
        val executionResult = if(state is RiderAsyncRunProfileState) state.executeAsync(environment.executor, this)
        else state.execute(environment.executor, this)

        return com.intellij.execution.runners.showRunContent(executionResult, environment)
    }
}