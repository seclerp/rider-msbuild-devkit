package model.rider

import com.jetbrains.rider.model.nova.ide.SolutionModel
import com.jetbrains.rd.generator.nova.*
import com.jetbrains.rd.generator.nova.csharp.CSharp50Generator
import com.jetbrains.rd.generator.nova.kotlin.Kotlin11Generator
@Suppress("unused")
object PluginBackendNameModel : Ext(SolutionModel.Solution) {
    init {
        setting(CSharp50Generator.Namespace, "RiderBackendGroup.RiderBackendName.Rd")
        setting(Kotlin11Generator.Namespace, "org.jetbrains.plugins.template.rd")
    }
}