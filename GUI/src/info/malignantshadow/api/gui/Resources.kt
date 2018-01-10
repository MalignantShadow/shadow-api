@file:Suppress("UNUSED")

package info.malignantshadow.api.gui

import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Font
import org.eclipse.swt.graphics.FontData
import org.eclipse.swt.graphics.Resource
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Widget

object Resources {

    internal val resToDispose = ArrayList<Resource>()
    internal val widgetsToDispose = ArrayList<Widget>()

    fun dispose() {
        resToDispose.forEach { it.dispose() }
        widgetsToDispose.forEach { it.dispose() }
    }

}

fun Resource.disposeLater() {
    Resources.resToDispose.add(this)
}

fun Widget.disposeLater() {
    Resources.widgetsToDispose.add(this)
}

fun getFont(base: Font = Display.getCurrent().systemFont, size: Int, style: Int = SWT.NORMAL): Font {
    val fd = base.fontData[0]
    return getFont(fd.name, if(size <= 0) fd.getHeight() else size, style)
}

fun getFont(name: String, size: Int, style: Int = SWT.NORMAL): Font {
    val fd = FontData(name, size, style)
    val f = Font(Display.getCurrent(), fd)
    f.disposeLater()
    return f
}