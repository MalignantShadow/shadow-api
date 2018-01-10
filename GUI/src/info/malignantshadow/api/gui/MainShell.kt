@file:Suppress("UNUSED")

package info.malignantshadow.api.gui

import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell

abstract class MainShell(appName: String, val title: String, style: Int = SWT.SHELL_TRIM) {

    val display: Display = Display.getCurrent()

    private var _shell: Shell
    val shell get() = _shell

    abstract val defaultSize: Point
    abstract fun createContents()

    init {
        Display.setAppName(appName)
        _shell = Shell(display, style)
        with(_shell) {
            text = title
            layout = FillLayout()
            addListener(SWT.Close) { end() }
            createContents()
        }
    }

    fun start() {
        with(_shell) {
            size = defaultSize
            center()
            visible = true
            while (!_shell.isDisposed) {
                if (!display.readAndDispatch())
                    display.sleep()
            }
        }
    }

    fun end() {
        willDispose()
        _shell.disposeRecursively(true)
        display.dispose()
        Resources.dispose()
        didDispose()
    }

    fun willDispose() {}
    fun didDispose() {}

}