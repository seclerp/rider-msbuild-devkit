package me.seclerp.msbuild.devkit.interactive

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.jetbrains.rider.run.environment.MSBuildEvaluator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import org.jetbrains.concurrency.await

@Service(Service.Level.PROJECT)
class MSBuildInteractiveHost(private val project: Project, private val scope: CoroutineScope) {
    companion object {
        fun getInstance(project: Project) = project.service<MSBuildInteractiveHost>()
    }

    private val outputChannel = Channel<Pair<String, OutputKind>>()
    val output: ReceiveChannel<Pair<String, OutputKind>> get() = outputChannel

    private val evaluator by lazy { MSBuildEvaluator.getInstance(project) }

    fun evaluate(projectFile: String, input: String) {
        val request = MSBuildEvaluator.PropertyRequest(projectFile, null, listOf(input))
        scope.launch {
            val result = evaluator.evaluateProperties(request).await()
            result.values.forEach {
                outputChannel.send(it to OutputKind.NORMAL)
            }
        }
    }
}

enum class OutputKind {
    NORMAL,
    WARNING,
    ERROR
}