package com.github.seclerp.msbuildrunner

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import com.jetbrains.rider.debugger.IRiderDebuggable
import com.jetbrains.rider.run.ICanRunFromBackend
import com.jetbrains.rider.run.configurations.*
import com.jetbrains.rider.run.postStartupActivities.IPostStartupActivity
import com.jetbrains.rider.runtime.RiderDotNetActiveRuntimeHost
import org.jdom.Element


open class MSBuildRunConfiguration(
    name: String,
    project: Project,
    factory: ConfigurationFactory,
    val parameters: MSBuildConfigurationParameters
) : RiderAsyncRunConfiguration(
    name,
    project,
    factory,
    { MSBuildConfigurationEditorGroup(it) },
    MSBuildExecutorFactory(project, parameters)
), IRiderDebuggable, ICanRunFromBackend, IProjectBasedRunConfiguration, IDotNetRunConfigurationWithPostStartupActivitiesSupport {
    override fun getTypeId(): String {
        return type.id
    }

    override fun getProjectFilePath(): String {
        return parameters.projectFilePath
    }

    override fun setProjectFilePath(path: String) {
        parameters.projectFilePath = path
    }

    override fun checkConfiguration() {
        super.checkConfiguration()
        parameters.validate(RiderDotNetActiveRuntimeHost.getInstance(project))
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        parameters.readExternal(element)
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        parameters.writeExternal(element)
    }

    override fun isNative() = false

    override fun acceptsPostStartupActivity(activityClass: Class<out IPostStartupActivity>): Boolean = acceptsDefaultPostStartupActivity(
        activityClass)

    override suspend fun createPostStartupActivity(runProfileState: RunProfileState,
                                                   environment: ExecutionEnvironment): IPostStartupActivity? = createDefaultPostStartupActivity(
        runProfileState, environment)

    override fun clone(): RunConfiguration {
        val newConfiguration = MSBuildRunConfiguration(name, project, factory!!, parameters.copy())
        newConfiguration.doCopyOptionsFrom(this)
        copyCopyableDataTo(newConfiguration)
        return newConfiguration
    }
}

