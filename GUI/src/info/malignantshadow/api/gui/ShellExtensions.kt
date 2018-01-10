@file:JvmMultifileClass
@file:JvmName("Extensions")
@file:Suppress("UNUSED")

package info.malignantshadow.api.gui

import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Monitor
import org.eclipse.swt.widgets.Shell

fun currentMonitor(): Monitor {
    val display = Display.getDefault()
    val p = display.cursorLocation
    return display.monitors.first { it.bounds.contains(p) }
}

fun Shell.center(monitor: Monitor = getMonitor()) = center(monitor.bounds)
fun Shell.center(relative: Shell) = center(relative.bounds)

fun Shell.center(r: Rectangle) {
    setLocation(((r.width - bounds.width) / 2) + r.x, ((r.height - bounds.height) / 2) + r.y)
}