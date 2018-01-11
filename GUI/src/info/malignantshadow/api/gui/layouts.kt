@file:Suppress("UNUSED")

package info.malignantshadow.api.gui

import info.malignantshadow.api.util.build
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.FormAttachment
import org.eclipse.swt.layout.FormData
import org.eclipse.swt.layout.FormLayout
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.RowData
import org.eclipse.swt.layout.RowLayout

/**
 * Make a [FormLayout]
 */
inline fun formLayout(init: FormLayout.() -> Unit = {}) = build(FormLayout(), init)

/**
 * Make a [FormData] object
 */
inline fun formData(init: FormData.() -> Unit = {}) = build(FormData(), init)

/**
 * Make a [FormData] object. If a parameter is less than 0, it is ignored.
 *
 * @param top The top numerator
 * @param left The left numerator
 * @param bottom The bottom numerator
 * @param right The right numerator
 */
fun formData(top: Int, left: Int, bottom: Int, right: Int) = formData {
    if (top >= 0)
        this.top = FormAttachment(top)
    if (left >= 0)
        this.left = FormAttachment(left)
    if (bottom >= 0)
        this.bottom = FormAttachment(bottom)
    if (right >= 0)
        this.right = FormAttachment(right)
}

/**
 * Make a [RowLayout]
 */
inline fun rowLayout(init: RowLayout.() -> Unit = {}) = build(RowLayout(), init)

/**
 * Make a [RowLayout]
 *
 * @param type Passed to `RowLayout(type)`
 */
inline fun rowLayout(type: Int, init: RowLayout.() -> Unit = {}) = build(RowLayout(type), init)

/**
 * Make a [RowData] object
 */
inline fun rowData(init: RowData.() -> Unit = {}) = build(RowData(), init)

/**
 * Make a [GridLayout]
 */
inline fun gridLayout(init: GridLayout.() -> Unit = {}) = build(GridLayout(), init)

/**
 * Make a [GridLayout]
 *
 * @param numColumns The number of columns
 * @param makeColumnsEqualWidth Whether the columns should be equal in width
 */
inline fun gridLayout(numColumns: Int, makeColumnsEqualWidth: Boolean, init: GridLayout.() -> Unit = {}) =
        build(GridLayout(numColumns, makeColumnsEqualWidth), init)

/**
 * Make a [GridData] object
 */
inline fun gridData(init: GridData.() -> Unit = {}) = build(GridData(), init)

/**
 * Make a [FillLayout]
 */
inline fun fillLayout(init: FillLayout.() -> Unit = {}) = build(FillLayout(), init)

/**
 * Make a [FillLayout]
 *
 * @param type Passed to `FillLayout(type)`
 */
inline fun fillLayout(type: Int, init: FillLayout.() -> Unit = {}) = build(FillLayout(type), init)
