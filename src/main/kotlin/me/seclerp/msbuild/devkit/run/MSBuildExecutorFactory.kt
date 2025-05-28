package me.seclerp.msbuild.devkit.run

import com.intellij.execution.CantRunException
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.jetbrains.rd.platform.util.getLogger
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rider.run.RiderRunBundle
import com.jetbrains.rider.run.configurations.AsyncExecutorFactory
import com.jetbrains.rider.run.configurations.dotNetExe.DotNetExeConfigurationExtension
import com.jetbrains.rider.run.configurations.exe.ProcessExecutionDetails
import com.jetbrains.rider.run.configurations.project.DotNetProjectExecutorFactory
import com.jetbrains.rider.run.configurations.tryCreateReSharperHostSelfDebugState
import com.jetbrains.rider.run.environment.withDetectedExecutableType
import com.jetbrains.rider.runtime.DotNetRuntime
import com.jetbrains.rider.runtime.RiderDotNetActiveRuntimeHost
import me.seclerp.msbuild.devkit.run.configurations.MSBuildConfigurationParameters

class MSBuildExecutorFactory(private val parameters: MSBuildConfigurationParameters) : AsyncExecutorFactory {
    private val logger = getLogger<DotNetProjectExecutorFactory>()

    override suspend fun create(executorId: String, environment: ExecutionEnvironment, lifetime: Lifetime): RunProfileState {
        val dotNetExecutable = parameters.toDotNetExecutableSuspending(ProcessExecutionDetails.Default).withDetectedExecutableType()
        val project = environment.project
        val runtimeToExecute = DotNetRuntime.detectRuntimeForExeOrThrow(
            project,
            RiderDotNetActiveRuntimeHost.getInstance(project),
            dotNetExecutable.exePath,
            dotNetExecutable.runtimeType,
            dotNetExecutable.projectTfm
        )
        logger.info("Configuration will be executed on ${runtimeToExecute.javaClass.name}")
        return when (executorId) {
            DefaultRunExecutor.EXECUTOR_ID -> runtimeToExecute.createRunState(dotNetExecutable, environment)
            DefaultDebugExecutor.EXECUTOR_ID -> {
                tryCreateReSharperHostSelfDebugState(environment, dotNetExecutable, runtimeToExecute)
                    ?: runtimeToExecute.createDebugState(dotNetExecutable, environment)
            }
            else -> {
                for (ext in DotNetExeConfigurationExtension.EP_NAME.getExtensions(project)) {
                    if (ext.canExecute(executorId)) {
                        return ext.executor(parameters, environment)
                    }
                }
                throw CantRunException(RiderRunBundle.message("dialog.message.unsupported.executor.error", executorId))
            }
        }
    }
}