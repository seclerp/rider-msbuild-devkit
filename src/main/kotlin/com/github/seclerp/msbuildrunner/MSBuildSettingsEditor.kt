package com.github.seclerp.msbuildrunner

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class MSBuildSettingsEditor: SettingsEditor<MSBuildRunConfiguration>() {
   private val _panel: JPanel
   private val _scriptPathField: TextFieldWithBrowseButton = TextFieldWithBrowseButton()

    init {
        _scriptPathField.addBrowseFolderListener("Select Target File", null, null,
            FileChooserDescriptorFactory.createSingleFileDescriptor())
        _panel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Target file", _scriptPathField)
            .panel
    }

    override fun resetEditorFrom(s: MSBuildRunConfiguration) {
        _scriptPathField.text = s.getScriptName()
    }

    override fun applyEditorTo(s: MSBuildRunConfiguration) {
        s.name = _scriptPathField.getText()
    }

    override fun createEditor(): JComponent {
        return _panel
    }
}
