@file:Suppress("UNUSED")

package info.malignantshadow.api.gui

import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell

/**
 * Create a [Shell] that will act as the main shell for the application.
 *
 * @param appName The name of this application, to be given to `Display.setAppName()`
 * @param title The title of the shell
 * @param style The style for the shell, defaults to `SWT.SHELL_TRIM`
 * @author Shad0w (Caleb Downs)
 */
abstract class MainShell(appName: String, val title: String, style: Int = SWT.SHELL_TRIM) {

    /**
     * The display. Equivalent to `Display.getCurrent()`
     */
    val display: Display = Display.getCurrent()

    private var _shell: Shell

    /**
     * The underlying shell
     */
    val shell get() = _shell

    /**
     * The default size of this shell
     */
    abstract val defaultSize: Point

    /**
     * Called when the underlying Shell is first created
     */
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

    /**
     * Show the Shell and start the event loop.
     */
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

    /**
     * Dispose of the underlying the shell, as well as all [org.eclipse.swt.graphics.Resource]s in the
     * [Resources] object.
     */
    fun end() {
        willDispose()
        _shell.disposeRecursively(true)
        display.dispose()
        Resources.dispose()
        didDispose()
    }

    /**
     * Called before the underlying shell is disposed
     */
    fun willDispose() {}

    /**
     * Called after the underlying is disposed
     */
    fun didDispose() {}

}