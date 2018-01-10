package info.malignantshadow.api.gui

import info.malignantshadow.api.util.build
import org.eclipse.swt.SWT
import org.eclipse.swt.printing.PrintDialog
import org.eclipse.swt.widgets.ColorDialog
import org.eclipse.swt.widgets.DirectoryDialog
import org.eclipse.swt.widgets.FileDialog
import org.eclipse.swt.widgets.FontDialog
import org.eclipse.swt.widgets.MessageBox
import org.eclipse.swt.widgets.Shell

/* MessageBox */

/**
 * Open a message box.
 *
 * @param style The style of the MessageBox, which is an Int of bitwise OR'd
 * SWT constants
 * @return the return code of the MessageBox
 */
fun Shell.openMessageBox(style: Int, init: MessageBox.() -> Unit = {}) =
        build(MessageBox(this, style), init).open()

/**
 * Open a message box.
 *
 * @param title The title of the MessageBox
 * @param message The message in the MessageBox
 * @param style The style of the MessageBox, which is an Int of bitwise OR'd
 * SWT constants
 * @return the return code of the MessageBox
 */
fun Shell.openMessageBox(title: String, message: String, style: Int): Int {
    return openMessageBox(style) {
        text = title
        this.message = message
    }
}

/**
 * Open a message box with a question icon as well as "OK" and "Cancel" buttons.
 *
 * @param title The title of the MessageBox, defaults to `"Confirm"`
 * @param message The message in the MessageBox
 * @return the return code of the MessageBox
 */
fun Shell.openMessageConfirm(title: String = "Confirm", message: String) =
        openMessageBox(title, message, SWT.ICON_QUESTION or SWT.CANCEL or SWT.OK)

/**
 * Open a message box with an error icon as well as an "OK" button.
 *
 * @param title The title of the MessageBox, defaults to `"Confirm"`
 * @param message The message in the MessageBox
 * @return the return code of the MessageBox
 */
fun Shell.openMessageError(title: String = "Error", message: String): Int =
        openMessageBox(title, message, SWT.ICON_ERROR or SWT.OK)

/**
 * Open a message box with an info icon as well as an "OK" button.
 *
 * @param title The title of the MessageBox, defaults to `"Confirm"`
 * @param message The message in the MessageBox
 * @return the return code of the MessageBox
 */
fun Shell.openMessageInfo(title: String = "Info", message: String) =
        openMessageBox(title, message, SWT.ICON_INFORMATION or SWT.OK)

/**
 * Open a message box with a question icon as well as "Yes" and "No" buttons.
 *
 * @param title The title of the MessageBox, defaults to `"Confirm"`
 * @param message The message in the MessageBox
 * @return the return code of the MessageBox
 */
fun Shell.openMessageQuestion(title: String = "Question", message: String) =
        openMessageBox(title, message, SWT.ICON_QUESTION or SWT.YES or SWT.NO)

/**
 * Open a message box with a warning icon as well as an "OK" button.
 *
 * @param title The title of the MessageBox, defaults to `"Confirm"`
 * @param message The message in the MessageBox
 * @return the return code of the MessageBox
 */
fun Shell.openMessageWarning(title: String = "Warning", message: String) =
        openMessageBox(title, message, SWT.ICON_WARNING or SWT.OK)

/* Color */

/**
 * Create a color selection dialog
 *
 * @return the dialog
 */
fun Shell.colorDialog(init: ColorDialog.() -> Unit = {}) =
        build(ColorDialog(this), init)

/* Directory */

/**
 * Create a directory selection dialog.
 *
 * @return the dialog
 */
fun Shell.directoryDialog(init: DirectoryDialog.() -> Unit = {}) =
        build(DirectoryDialog(this), init)

/* File */

/**
 * Create a file selection dialog.
 *
 * @return the dialog
 */
fun Shell.fileDialog(style: Int = SWT.DEFAULT, init: FileDialog.() -> Unit = {}) =
        build(FileDialog(this, style), init)

/* Font */

/**
 * Create a font selection dialog.
 *
 * @return the dialog
 */
fun Shell.fontDialog(init: FontDialog.() -> Unit = {}) =
        build(FontDialog(this), init)

/* Printer */

/**
 * Create a print dialog.
 *
 * @return the dialog
 */
fun Shell.printerDialog(init: PrintDialog.() -> Unit = {}) =
        build(PrintDialog(this), init)