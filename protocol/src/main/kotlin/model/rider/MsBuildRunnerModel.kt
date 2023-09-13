package model.rider

import com.jetbrains.rider.model.nova.ide.SolutionModel
import com.jetbrains.rd.generator.nova.*
import com.jetbrains.rd.generator.nova.csharp.CSharp50Generator
import com.jetbrains.rd.generator.nova.kotlin.Kotlin11Generator

@Suppress("unused")
object MsBuildRunnerModel : Ext(SolutionModel.Solution) {
    private val MsBuildProjectInfo = structdef {
        field("id", PredefinedType.guid)
    }

    private val MsBuildTargetInfo = structdef {
        field("name", PredefinedType.string)
    }

    init {
        setting(CSharp50Generator.Namespace, "Msbuild.Runner.Rd")
        setting(Kotlin11Generator.Namespace, "com.github.seclerp.msbuildrunner.rd")

        call("getTargets", MsBuildProjectInfo, immutableList(MsBuildTargetInfo))
    }
}