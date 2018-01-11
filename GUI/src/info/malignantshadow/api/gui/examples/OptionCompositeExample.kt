package info.malignantshadow.api.gui.examples

import info.malignantshadow.api.gui.formData
import info.malignantshadow.api.gui.formLayout
import info.malignantshadow.api.gui.openMessageInfo
import info.malignantshadow.api.gui.view.MainShell
import info.malignantshadow.api.gui.view.optionComposite
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.GridLayout

class OptionCompositeExample : MainShell("example", "Example Window") {

    override val defaultSize = Point(750, 500)

    override fun createContents() {
        val formLayout = formLayout {
            spacing = 5
            marginWidth = 5
            marginHeight = 5
        }

        optionComposite(shell, 15, SWT.FULL_SELECTION) {
            item("Item 1") {
                with(container) { layout = formLayout }
                button(SWT.PUSH) {
                    text = "Click me?"
                    layoutData = formData(0, 0, -1, 100)
                    addListener(SWT.Selection) { shell.openMessageInfo("Why?", "Why would you click me ;_;") }
                }

                item("Item 1.1") {
                    with(container) { layout = formLayout }
                    label(SWT.BOLD) {
                        text = "I am the 1.1 item"
                        layoutData = formData(0, 0, -1, -1)
                    }
                }

            }.item.expanded = true

            item("2") {
                with(container) { layout = GridLayout(1, false) }
                for (i in 0..100) {
                    label {
                        text = "Item $i "
                    }
                }
            }

            select(0)
        }
    }

}

fun main(args: Array<String>) {
    OptionCompositeExample().start()
}