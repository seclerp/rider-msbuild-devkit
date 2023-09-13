package com.github.seclerp.msbuildrunner.runConfigurations

import com.github.seclerp.msbuildrunner.MSBuildConfigurationType
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.impl.DefaultNewRunConfigurationTreePopupFactory
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.jetbrains.rider.run.configurations.RiderNewRunConfigurationTreeGroupingProvider
import com.jetbrains.rider.run.configurations.RiderNewRunConfigurationTreePopupFactory
import icons.RiderIcons
import javax.swing.Icon

class PatchedRiderNewRunConfigurationTreePopupFactory : DefaultNewRunConfigurationTreePopupFactory() {
    private val riderFactory = RiderNewRunConfigurationTreePopupFactory()

    override fun createChildElements(project: Project, nodeDescriptor: NodeDescriptor<*>): Array<NodeDescriptor<*>> {
        return when {
            nodeDescriptor is GroupDescriptor && nodeDescriptor.element == ".NET" -> {
                buildList {
                    addAll(riderFactory.createChildElements(project, nodeDescriptor))
                    val msbuildConfigType = ConfigurationType.CONFIGURATION_TYPE_EP.extensionList.firstOrNull { it.id == MSBuildConfigurationType.ID }
                    if (msbuildConfigType != null) {
                        add(createDescriptor(project, msbuildConfigType, nodeDescriptor))
                    }
                }.toTypedArray()
            }
            else -> riderFactory.createChildElements(project, nodeDescriptor)
        }
    }

    override fun getRootElement(): NodeDescriptor<*> {
        return riderFactory.rootElement
    }

    override fun createDescriptor(project: Project, element: Any, parentDescriptor: NodeDescriptor<*>?, weight: Int): NodeDescriptor<*> {
        return riderFactory.createDescriptor(project, element, parentDescriptor, weight)
    }

    override fun createIconAndText(element: Any): Pair<Icon, String> {
        return riderFactory.createIconAndText(element)
    }

    override fun initStructure(project: Project) {
        riderFactory.initStructure(project)
    }
}