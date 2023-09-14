package com.github.seclerp.msbuildrunner

import com.intellij.execution.CantRunException
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import com.jetbrains.rd.platform.util.getLogger
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rider.model.runnableProjectsModel
import com.jetbrains.rider.projectView.solution
import com.jetbrains.rider.run.RiderRunBundle
import com.jetbrains.rider.run.configurations.AsyncExecutorFactory
import com.jetbrains.rider.run.configurations.RiderConfigurationExecutorExtension
import com.jetbrains.rider.run.configurations.RuntimeHotReloadRunConfigurationInfo
import com.jetbrains.rider.run.configurations.exe.ExeExecutorFactory
import com.jetbrains.rider.run.configurations.project.DotNetProjectConfigurationExtension
import com.jetbrains.rider.run.configurations.project.DotNetProjectConfigurationParameters
import com.jetbrains.rider.run.configurations.project.DotNetProjectConfigurationType
import com.jetbrains.rider.run.configurations.project.DotNetProjectExecutorFactory
import com.jetbrains.rider.run.configurations.tryCreateReSharperHostSelfDebugState
import com.jetbrains.rider.run.environment.withDetectedExecutableType
import com.jetbrains.rider.runtime.DotNetExecutable
import com.jetbrains.rider.runtime.DotNetRuntime
import com.jetbrains.rider.runtime.RiderDotNetActiveRuntimeHost

class MSBuildExecutorFactory(private val project: Project, private val parameters: MSBuildConfigurationParameters) : AsyncExecutorFactory {
    private val logger = getLogger<DotNetProjectExecutorFactory>()

    override suspend fun create(executorId: String, environment: ExecutionEnvironment, lifetime: Lifetime): RunProfileState {
        val projectKind = parameters.projectKind
        logger.info("project kind is $projectKind")

        if (parameters.isNative) {
            val nativeParameters = parameters.getActualExeConfigurationParameters()
            val exeFactory = ExeExecutorFactory(nativeParameters)
            return exeFactory.create(executorId, environment, lifetime)
        }

        val projects = project.solution.runnableProjectsModel.projects.valueOrNull ?: throw CantRunException(
            DotNetProjectConfigurationParameters.SOLUTION_IS_LOADING
        )
        val runnableProject = projects.singleOrNull {
            MSBuildConfigurationType.isTypeApplicable(it.kind) && it.projectFilePath == parameters.projectFilePath
        } ?: throw CantRunException(DotNetProjectConfigurationParameters.PROJECT_NOT_SPECIFIED)

        val output = parameters.tryGetProjectOutput(runnableProject)
        val hotReloadRunInfo = RuntimeHotReloadRunConfigurationInfo(executorId, project, runnableProject, parameters.getActualTfm(), output)
        val dotNetExecutable = getDotNetExecutable(lifetime, hotReloadRunInfo, environment).withDetectedExecutableType()

        val runtimeToExecute = DotNetRuntime.detectRuntimeForProjectOrThrow(
            projectKind,
            RiderDotNetActiveRuntimeHost.getInstance(project),
            dotNetExecutable.runtimeType,
            dotNetExecutable.exePath,
            dotNetExecutable.projectTfm
        )
        logger.info("Configuration will be executed on ${runtimeToExecute.javaClass.name}")
        return when (executorId) {
            DefaultRunExecutor.EXECUTOR_ID -> runtimeToExecute.createRunState(dotNetExecutable, environment)
            DefaultDebugExecutor.EXECUTOR_ID -> {
                tryCreateReSharperHostSelfDebugState(environment, parameters, runtimeToExecute, dotNetExecutable)
                    ?: runtimeToExecute.createDebugState(dotNetExecutable, environment)
            }
            else -> throw CantRunException(RiderRunBundle.message("dialog.message.unsupported.executor.error", executorId))
        }
    }

    private suspend fun getDotNetExecutable(lifetime: Lifetime,
                                            hotReloadRunInfo: RuntimeHotReloadRunConfigurationInfo,
                                            environment: ExecutionEnvironment): DotNetExecutable {
        for (ext in RiderConfigurationExecutorExtension.EP_NAME.getExtensions(project)) {
            if (ext.canExecute(lifetime, hotReloadRunInfo)) {
                return ext.executor(project, environment, parameters)
            }
        }

        return parameters.toDotNetExecutable()
    }
}