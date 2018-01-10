@file:JvmMultifileClass
@file:JvmName("Extensions")
@file:Suppress("UNUSED")

package info.malignantshadow.api.gui

import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Monitor
import org.eclipse.swt.widgets.Shell

/**
 * Get the Monitor where the mouse cursor is located.
 */
fun currentMonitor(): Monitor {
    val display = Display.getDefault()
    val p = display.cursorLocation
    return display.monitors.first { it.bounds.contains(p) }
}

/**
 * Center this Shell on a monitor.
 *
 * @param monitor The monitor to center the shell (by default, the same monitor the shell is located, via
 * `getMonitor()`).
 */
fun Shell.center(monitor: Monitor = getMonitor()) = center(monitor.bounds)

/**
 * Center this Shell relative to another Shell.
 *
 * @param relative The related Shell
 */
fun Shell.center(relative: Shell) = center(relative.bounds)

/**
 * Center this Shell inside the given Rectangle.
 *
 * @param r The rectangle
 */
fun Shell.center(r: Rectangle) {
    setLocation(((r.width - bounds.width) / 2) + r.x, ((r.height - bounds.height) / 2) + r.y)
}