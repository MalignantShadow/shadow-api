@file:Suppress("UNUSED")

package info.malignantshadow.api.gui

import info.malignantshadow.api.util.build
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.FormData
import org.eclipse.swt.layout.FormLayout
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.RowData
import org.eclipse.swt.layout.RowLayout

inline fun formLayout(init: FormLayout.() -> Unit = {}) = build(FormLayout(), init)
inline fun formData(init: FormData.() -> Unit = {}) = build(FormData(), init)

inline fun rowLayout(init: RowLayout.() -> Unit = {}) = build(RowLayout(), init)
inline fun rowLayout(type: Int, init: RowLayout.() -> Unit = {}) = build(RowLayout(type), init)
inline fun rowData(init: RowData.() -> Unit = {}) = build(RowData(), init)

inline fun gridLayout(init: GridLayout.() -> Unit = {}) = build(GridLayout(), init)
inline fun gridLayout(numColumns: Int, makeColumnsEqualWidth: Boolean, init: GridLayout.() -> Unit = {}) =
        build(GridLayout(numColumns, makeColumnsEqualWidth), init)
inline fun gridData(init: GridData.() -> Unit = {}) = build(GridData(), init)

inline fun fillLayout(init: FillLayout.() -> Unit = {}) = build(FillLayout(), init)
inline fun fillLayout(type: Int, init: FillLayout.() -> Unit = {}) = build(FillLayout(type), init)
