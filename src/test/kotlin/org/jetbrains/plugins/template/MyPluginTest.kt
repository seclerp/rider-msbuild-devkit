package org.jetbrains.plugins.template

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.components.service
import com.jetbrains.rider.test.actions.TestRenameAction
import com.jetbrains.rider.test.annotations.TestEnvironment
import com.jetbrains.rider.test.base.TypingAssistTestBase
import com.jetbrains.rider.test.env.enums.BuildTool
import com.jetbrains.rider.test.env.enums.SdkVersion
import com.jetbrains.rider.test.scriptingApi.renameElement
import com.jetbrains.rider.test.scriptingApi.typeWithLatency
import com.jetbrains.rider.test.scriptingApi.withOpenedEditor
import org.jetbrains.plugins.template.services.MyProjectService
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import kotlin.test.assertNotEquals

@TestEnvironment(sdkVersion = SdkVersion.AUTODETECT, buildTool = BuildTool.AUTODETECT)
class MyPluginTest : TypingAssistTestBase() {
    private val actionManager by lazy { ActionManager.getInstance() }

    @BeforeTest
    fun prepareActions() {
        // This is required for `renameElement` to work properly.
        actionManager.registerAction("TestRename", TestRenameAction())
    }

    @AfterTest
    fun disposeActions() {
        actionManager.unregisterAction("TestRename")
    }

    @Test
    fun testRename() {
        withOpenedEditor("Program.cs", "Program.cs") {
            renameElement()
            typeWithLatency("SpaceShipName")
        }
    }

    @Test
    fun testProjectService() {
        val projectService = project.service<MyProjectService>()

        assertNotEquals(projectService.getRandomNumber(), projectService.getRandomNumber())
    }

    override fun getSolutionDirectoryName() = "MyProjectTestSln"
}
