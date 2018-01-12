package info.malignantshadow.api.gui.examples

import info.malignantshadow.api.gui.composite
import info.malignantshadow.api.gui.formData
import info.malignantshadow.api.gui.formLayout
import info.malignantshadow.api.gui.view.MainShell
import info.malignantshadow.api.gui.view.dialog
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Point

class DialogExample : MainShell("example", "Example Window") {

    override val defaultSize = Point(700, 450)

    override fun createContents() {
        val layout = formLayout {
            spacing = 5
            marginWidth = 5
            marginHeight = 5
        }

        val dialog = dialog(shell, "Custom Dialog") {
            var called = 0

            size = Point(300, 100)
            clientArea {
                this.layout = layout
                label {
                    text = "I am a custom dialog!!"
                    layoutData = formData(0, 0, -1, -1)
                }
            }

            buttons {
                this.layout = layout
                button {
                    text = "OK"
                    layoutData = formData(-1, -1, 100, 100)
                    addListener(SWT.Selection) {
                        this@dialog.result = "I have been closed ${++called} times"
                        shell.close()
                    }
                }
            }
        }

        composite(shell) {
            this.layout = layout
            button(SWT.PUSH) {
                text = "Open custom dialog"
                layoutData = formData(0, 0, -1, -1)
                addListener(SWT.Selection) {
                    dialog.open()
                    println(dialog.result)
                }
            }
        }
    }

}

fun main(args: Array<String>) {
    DialogExample().start()
}