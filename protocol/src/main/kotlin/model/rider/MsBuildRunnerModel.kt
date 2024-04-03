package model.rider

import com.jetbrains.rider.model.nova.ide.SolutionModel
import com.jetbrains.rd.generator.nova.*
import com.jetbrains.rd.generator.nova.csharp.CSharp50Generator
import com.jetbrains.rd.generator.nova.kotlin.Kotlin11Generator
import com.jetbrains.rd.generator.nova.PredefinedType.*

@Suppress("unused")
object MsBuildRunnerModel : Ext(SolutionModel.Solution) {
    private val MsBuildProjectInfo = structdef {
        field("id", guid)
    }

    private val MsBuildTargetInfo = structdef {
        field("name", string)
    }

    private val MsBuildEvaluateRequest = structdef {
        field("projectId", guid)
        field("expression", string)
    }

    private val MsBuildEvaluationResult = structdef {
        field("status", bool)
        field("output", string)
        field("diagnostics", immutableList(string))
    }

    init {
        setting(CSharp50Generator.Namespace, "MSBuild.DevKit.Rd")
        setting(Kotlin11Generator.Namespace, "me.seclerp.msbuild.devkit.rd")

        call("getTargets", MsBuildProjectInfo, immutableList(MsBuildTargetInfo))
        call("evaluate", MsBuildEvaluateRequest, MsBuildEvaluationResult)
    }
}