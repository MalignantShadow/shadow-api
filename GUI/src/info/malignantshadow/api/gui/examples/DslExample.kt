import info.malignantshadow.api.gui.composite
import info.malignantshadow.api.gui.formData
import info.malignantshadow.api.gui.formLayout
import info.malignantshadow.api.gui.view.MainShell
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.FormAttachment

class ExampleShell : MainShell("example", "Example Shell") {

    override val defaultSize = Point(500, 300)

    override fun createContents() {
        composite(this.shell) {
            layout = formLayout {
                spacing = 5
                marginHeight = 5
                marginWidth = 5
            }

            label(SWT.CENTER) {
                text = "Welcome!"
                layoutData = formData {
                    top = FormAttachment(0)
                    left = FormAttachment(0)
                    right = FormAttachment(100)
                }
            }

            button(SWT.PUSH) {
                text = "Close Window"
                layoutData = formData {
                    bottom = FormAttachment(100)
                    right = FormAttachment(100)
                }
                addListener(SWT.Selection, { super.end() })
            }
        }
    }

}

fun main(args: Array<String>) {
    ExampleShell().start()
}