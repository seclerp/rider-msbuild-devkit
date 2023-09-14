package com.github.seclerp.msbuildrunner.components

import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.observable.util.addDocumentListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.rd.createNestedDisposable
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.platform.backend.workspace.WorkspaceModel
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.EditorTextField
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.util.textCompletion.TextCompletionProvider
import com.intellij.util.textCompletion.TextFieldWithCompletion
import com.jetbrains.rd.swing.addLifetimedItem
import com.jetbrains.rd.util.TlsBoxed
import com.jetbrains.rd.util.forbidReentrancy
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.lifetime.isNotAlive
import com.jetbrains.rd.util.reactive.IProperty
import com.jetbrains.rd.util.reactive.IViewableList
import com.jetbrains.rider.model.RunnableProject
import com.jetbrains.rider.projectView.calculateIcon
import com.jetbrains.rider.projectView.workspace.getProjectModelEntities
import com.jetbrains.rider.projectView.workspace.isProject
import com.jetbrains.rider.projectView.workspace.isUnloadedProject
import com.jetbrains.rider.run.RiderRunBundle
import java.awt.event.ItemListener
import java.io.File
import javax.swing.JList
import javax.swing.event.DocumentEvent

fun Row.textFieldWithCompletion(project: Project, provider: TextCompletionProvider): Cell<TextFieldWithCompletion> {
    val component = TextFieldWithCompletion(
        project,
        provider,
        "suggest",
        true,
        true,
        true,
        true
    )
    return cell(component)
}

fun Row.projectSelector(project: Project): Cell<ComboBox<RunnableProject>> {
    return cell(ComboBox<RunnableProject>()).applyToComponent {
        isSwingPopup = false
        renderer = object : SimpleListCellRenderer<RunnableProject?>() {
            override fun customize(list: JList<out RunnableProject?>,
                                   value: RunnableProject?,
                                   index: Int,
                                   selected: Boolean,
                                   hasFocus: Boolean) {
                if (project.isDisposed) return
                if (value == null) {
                    text = RiderRunBundle.message("ControlViewBuilder.project.selector.cell.without.projects")
                    return
                }
                VfsUtil.findFileByIoFile(File(value.projectFilePath), false)?.let { virtualFile ->
                    WorkspaceModel.getInstance(project)
                        .getProjectModelEntities(virtualFile, project).singleOrNull { it.isProject() || it.isUnloadedProject() }?.calculateIcon(
                            project
                        )
                        ?.let { icon = it }
                }
                text = value.fullName
            }
        }
    }
}

fun <T : Any> Cell<ComboBox<T>>.bindItems(property: IViewableList<T>, lifetime: Lifetime): Cell<ComboBox<T>> {
    property.view(lifetime) { projectLt, _, proj ->
        component.addLifetimedItem(projectLt, proj)
    }
    return this
}

inline fun <reified T : Any> Cell<ComboBox<T>>.bindItem(property: IProperty<T?>, lifetime: Lifetime): Cell<ComboBox<T>> {
    val guard = TlsBoxed(false)
    property.advise(lifetime) {
        if (component.selectedItem != it) {
            guard.forbidReentrancy {
                component.selectedItem = it
            }
        }
    }
    component.addItemListener(ItemListener {
        if (lifetime.isNotAlive) return@ItemListener
        if (guard.value) return@ItemListener
        val selectedItem = component.selectedItem
        if (selectedItem != null && selectedItem is T) {
            property.set(selectedItem)
        }
    })
    return this
}

@JvmName("JBTextField_bindText")
fun Cell<JBTextField>.bindText(property: IProperty<String>, lifetime: Lifetime): Cell<JBTextField> {
    return applyToComponent {
        property.advise(lifetime) {
            text = it
        }
        document.addDocumentListener(lifetime.createNestedDisposable(), object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                property.set(text)
            }
        })
    }
}

@JvmName("EditorTextField_bindText")
fun Cell<EditorTextField>.bindText(property: IProperty<String>, lifetime: Lifetime): Cell<EditorTextField> {
    return applyToComponent {
        property.advise(lifetime) {
            text = it
        }
        document.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: com.intellij.openapi.editor.event.DocumentEvent) {
                property.set(text)
            }
        }, lifetime.createNestedDisposable())
    }
}