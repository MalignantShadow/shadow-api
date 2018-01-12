package info.malignantshadow.api.gui.view

import info.malignantshadow.api.gui.SwtComposite
import info.malignantshadow.api.gui.SwtContainer
import info.malignantshadow.api.gui.SwtDsl
import info.malignantshadow.api.gui.center
import info.malignantshadow.api.gui.composite
import info.malignantshadow.api.gui.formData
import info.malignantshadow.api.gui.formLayout
import info.malignantshadow.api.util.build
import info.malignantshadow.api.util.equalsAny
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.FormAttachment
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Dialog
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell

/**
 * Represents the prototype of a dialog.
 *
 * @param parent The parent Shell. The dialog will be positioned relative to this shell when opened
 * @param title The title of the dialog
 * @param modal The modal of the dialog, can be one of `0`
 * , [APPLICATION_MODAL][SWT.APPLICATION_MODAL]
 * , [PRIMARY_MODAL][SWT.PRIMARY_MODAL]
 * , or [SYSTEM_MODAL][SWT.SYSTEM_MODAL]
 */
@SwtDsl
class SwtDialog(parent: Shell, val title: String = "", val modal: Int) : Dialog(parent, 0) {

    private lateinit var _shell: Shell
    private lateinit var _clientArea: SwtComposite
    private lateinit var _buttonArea: SwtComposite
    private var clientAreaInit: (SwtComposite.() -> Unit)? = null
    private var buttonAreaInit: (SwtComposite.() -> Unit)? = null

    /**
     * The last result of this dialog
     */
    var result: Any? = null

    /**
     * The top part of the dialog, above the buttons
     */
    val clientArea: Composite get() = _clientArea

    /**
     * The buttons at the bottom of the dialog
     */
    val buttonArea: Composite get() = _buttonArea

    /**
     * The size of the dialog when it is opened.
     */
    var size: Point = parent.size

    init {
        require(modal.equalsAny(0, SWT.APPLICATION_MODAL, SWT.PRIMARY_MODAL, SWT.SYSTEM_MODAL)) {
            "modal must be 0 or an SWT constant representing a MODAL type"
        }
    }

    /**
     * Create and open the dialog
     */
    fun open() {
        val display = Display.getCurrent()
        _shell = Shell(display, SWT.DIALOG_TRIM or modal)
        with(_shell) {
            text = title
            layout = formLayout {
                spacing = 5
                marginWidth = 5
                marginHeight = 5
            }

                size = this@SwtDialog.size

        }

        _buttonArea = composite(_shell, 0) {
            layoutData = formData(-1, 0, 100, 100)
            this@SwtDialog.buttonAreaInit?.invoke(this)
        }

        _clientArea = composite(_shell, 0) {
            layoutData = formData {
                top = FormAttachment(0)
                left = FormAttachment(0)
                bottom = FormAttachment(this@SwtDialog._buttonArea)
                right = FormAttachment(100)
            }
            this@SwtDialog.clientAreaInit?.invoke(this)
        }

        _shell.center(parent)
        _shell.visible = true

        while (!_shell.isDisposed) {
            if (!display.readAndDispatch()) display.sleep()
        }
    }

    /**
     * Create the client area of the dialog
     */
    fun clientArea(init: SwtComposite.() -> Unit) {
        clientAreaInit = init
    }

    /**
     * Create the buttons of the dialog
     */
    fun buttons(init: SwtComposite.() -> Unit) {
        buttonAreaInit = init
    }

}

/**
 * Create a new dialog prototype
 *
 * @param parent The parent Shell
 * @param title The title of the dialog
 * @param modal The modal of the dialog
 */
fun dialog(parent: Shell, title: String, modal: Int = SWT.SYSTEM_MODAL, init: SwtDialog.() -> Unit) =
        build(SwtDialog(parent, title, modal), init)

/**
 * Create a new dialog prototype using this container's shell as the parent
 *
 * @param title The title of the dialog
 * @param modal The modal of the dialog
 */
fun SwtContainer.dialog(title: String, modal: Int = SWT.SYSTEM_MODAL, init: SwtDialog.() -> Unit) =
        dialog(container.shell, title, modal, init)