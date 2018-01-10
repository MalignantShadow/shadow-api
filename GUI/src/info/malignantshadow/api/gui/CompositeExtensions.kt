@file:JvmMultifileClass
@file:JvmName("Extensions")

package info.malignantshadow.api.gui

import org.eclipse.swt.widgets.Composite

/**
 * Dispose of this [Composite]s children as well as this Composite if `selfDispose` is true.
 * If a child is a Composite, the child and its children will disposed of as well.
 *
 * @param selfDispose Whether to dispose of this Composite after the children are disposed of
 */
fun Composite.disposeRecursively(selfDispose: Boolean = false) {
    children.forEach {
        (it as? Composite)?.disposeRecursively(true) ?: it.dispose()
    }

    if(selfDispose)
        dispose()
}