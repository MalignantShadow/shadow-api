@file:Suppress("UNUSED")

package info.malignantshadow.api.gui

import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Font
import org.eclipse.swt.graphics.FontData
import org.eclipse.swt.graphics.Resource
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Widget

/**
 * A helper object for [Resource]s
 */
object Resources {

    internal val resToDispose = ArrayList<Resource>()
    internal val widgetsToDispose = ArrayList<Widget>()

    /**
     * Dispose of all [Resource]s stored in this object.
     */
    fun dispose() {
        resToDispose.forEach { it.dispose() }
        widgetsToDispose.forEach { it.dispose() }
    }

}

/**
 * Dispose of this resource later. The [Resource] is guaranteed to be disposed of
 * when a [MainShell] is disposed.
 */
fun Resource.disposeLater() {
    Resources.resToDispose.add(this)
}

/**
 * Dispose of this widget later. The [Widget] is guaranteed to be disposed of
 * when a [MainShell] is disposed.
 */
fun Widget.disposeLater() {
    Resources.widgetsToDispose.add(this)
}

/**
 * Get an installed font with the given size and style.
 *
 * @param base The base font, defaults to the system default
 * @param size The size of font
 * @param style The style of the font, which is an Int with font styles that are bitwise OR'd
 */
fun getFont(base: Font = Display.getCurrent().systemFont, size: Int, style: Int = SWT.NORMAL): Font {
    val fd = base.fontData[0]
    return getFont(fd.name, if(size <= 0) fd.getHeight() else size, style)
}

/**
 * Get an installed font by name.
 *
 * @param name The name of the font
 * @param size The size of font
 * @param style The style of the font, which is an Int with font styles that are bitwise OR'd
 */
fun getFont(name: String, size: Int, style: Int = SWT.NORMAL): Font {
    val fd = FontData(name, size, style)
    val f = Font(Display.getCurrent(), fd)
    f.disposeLater()
    return f
}