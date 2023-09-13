package com.github.seclerp.msbuildrunner

import com.intellij.openapi.options.SettingsEditor
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class MSBuildSettingsEditor: SettingsEditor<MSBuildRunConfiguration>() {
   private val _panel: JPanel = FormBuilder.createFormBuilder().panel

    override fun resetEditorFrom(s: MSBuildRunConfiguration) {

    }

    override fun applyEditorTo(s: MSBuildRunConfiguration) {
        s.name = "test"
    }


    override fun createEditor(): JComponent {
        return _panel
    }
}
