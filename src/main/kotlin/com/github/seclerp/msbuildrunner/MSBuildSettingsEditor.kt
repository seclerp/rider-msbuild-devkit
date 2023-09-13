package com.github.seclerp.msbuildrunner

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.util.textCompletion.TextFieldWithCompletion
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class MSBuildSettingsEditor(project: Project) : SettingsEditor<MSBuildRunConfiguration>() {
    private val _panel: JPanel = FormBuilder.createFormBuilder().panel
    private val textField: TextFieldWithCompletion
    private val provider = MSBuildTextCompletionProvider(project)

    init {
        textField =
            TextFieldWithCompletion(
                project,
                provider,
                "suggest",
                true,
                true,
                true,
                true
            )

        _panel.add(textField)
    }

    override fun resetEditorFrom(s: MSBuildRunConfiguration) {

    }

    override fun applyEditorTo(s: MSBuildRunConfiguration) {
        s.name = "test"
    }


    override fun createEditor(): JComponent {
        return _panel
    }
}
