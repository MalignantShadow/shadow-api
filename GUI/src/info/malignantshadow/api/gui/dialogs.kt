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

fun Shell.openMessageBox(style: Int, init: MessageBox.() -> Unit = {}) =
        build(MessageBox(this, style), init).open()

fun Shell.openMessageBox(title: String, message: String, style: Int): Int {
    return openMessageBox(style) {
        text = title
        this.message = message
    }
}

fun Shell.openMessageConfirm(title: String = "Confirm", message: String) =
        openMessageBox(title, message, SWT.ICON_QUESTION or SWT.CANCEL or SWT.OK)

fun Shell.openMessageError(title: String = "Error", message: String): Int =
        openMessageBox(title, message, SWT.ICON_ERROR or SWT.OK)

fun Shell.openMessageInfo(title: String = "Info", message: String) =
        openMessageBox(title, message, SWT.ICON_INFORMATION or SWT.OK)

fun Shell.openMessageQuestion(title: String = "Question", message: String) =
        openMessageBox(title, message, SWT.ICON_QUESTION or SWT.YES or SWT.NO)

fun Shell.openMessageWarning(title: String = "Warning", message: String) =
        openMessageBox(title, message, SWT.ICON_WARNING or SWT.OK)

/* Color */

fun Shell.colorDialog(init: ColorDialog.() -> Unit = {}) =
        build(ColorDialog(this), init)

/* Directory */

fun Shell.directoryDialog(init: DirectoryDialog.() -> Unit = {}) =
        build(DirectoryDialog(this), init)

/* File */

fun Shell.fileDialog(style: Int = SWT.DEFAULT, init: FileDialog.() -> Unit = {}) =
        build(FileDialog(this, style), init)

/* Font */

fun Shell.fontDialog(init: FontDialog.() -> Unit = {}) =
        build(FontDialog(this), init)

/* Printer */

fun Shell.printerDialog(init: PrintDialog.() -> Unit = {}) =
        build(PrintDialog(this), init)