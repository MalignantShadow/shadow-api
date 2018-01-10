@file:JvmMultifileClass
@file:JvmName("Extensions")

package info.malignantshadow.api.gui

import org.eclipse.swt.widgets.Composite

fun Composite.disposeRecursively(selfDispose: Boolean = false) {
    children.forEach {
        (it as? Composite)?.disposeRecursively(true) ?: it.dispose()
    }

    if(selfDispose)
        dispose()
}