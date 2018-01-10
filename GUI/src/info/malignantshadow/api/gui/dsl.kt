@file:Suppress("unused")

package info.malignantshadow.api.gui

import info.malignantshadow.api.util.build
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Canvas
import org.eclipse.swt.widgets.Combo
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.DateTime
import org.eclipse.swt.widgets.Decorations
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Group
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Link
import org.eclipse.swt.widgets.List
import org.eclipse.swt.widgets.Menu
import org.eclipse.swt.widgets.MenuItem
import org.eclipse.swt.widgets.ProgressBar
import org.eclipse.swt.widgets.Sash
import org.eclipse.swt.widgets.Scale
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Slider
import org.eclipse.swt.widgets.Spinner
import org.eclipse.swt.widgets.TabFolder
import org.eclipse.swt.widgets.TabItem
import org.eclipse.swt.widgets.Table
import org.eclipse.swt.widgets.TableColumn
import org.eclipse.swt.widgets.TableItem
import org.eclipse.swt.widgets.Text
import org.eclipse.swt.widgets.ToolBar
import org.eclipse.swt.widgets.ToolItem
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeColumn
import org.eclipse.swt.widgets.TreeItem

@DslMarker
annotation class SwtDsl

@SwtDsl
interface SwtContainer {

    val container: Composite

    fun composite(style: Int = SWT.DEFAULT, init: SwtComposite.() -> Unit = {}) =
            build(SwtComposite(container, style), init)

    fun button(style: Int = SWT.DEFAULT, init: SwtButton.() -> Unit = {}) =
            build(SwtButton(container, style), init)

    fun canvas(style: Int = SWT.DEFAULT, init: SwtCanvas.() -> Unit = {}) =
            build(SwtCanvas(container), init)

    fun combo(style: Int = SWT.DEFAULT, init: SwtCombo.() -> Unit = {}) =
            build(SwtCombo(container, style), init)

    fun dateTime(style: Int = SWT.DEFAULT, init: SwtDateTime.() -> Unit = {}) =
            build(SwtDateTime(container, style), init)

    fun group(style: Int = SWT.DEFAULT, init: SwtGroup.() -> Unit = {}) =
            build(SwtGroup(container, style), init)

    fun label(style: Int = SWT.DEFAULT, init: SwtLabel.() -> Unit = {}) =
            build(SwtLabel(container, style), init)

    fun link(style: Int = SWT.DEFAULT, init: SwtLink.() -> Unit = {}) =
            build(SwtLink(container, style), init)

    fun list(style: Int = SWT.DEFAULT, init: SwtList.() -> Unit = {}) =
            build(SwtList(container, style), init)

    fun progressBar(style: Int = SWT.DEFAULT, init: SwtProgressBar.() -> Unit = {}) =
            build(SwtProgressBar(container, style), init)

    fun sash(style: Int = SWT.DEFAULT, init: SwtSash.() -> Unit = {}) =
            build(SwtSash(container, style), init)

    fun scale(style: Int = SWT.DEFAULT, init: SwtScale.() -> Unit = {}) =
            build(SwtScale(container, style), init)

    fun slider(style: Int = SWT.DEFAULT, init: SwtSlider.() -> Unit = {}) =
            build(SwtSlider(container, style), init)

    fun spinner(style: Int = SWT.DEFAULT, init: SwtSpinner.() -> Unit = {}) =
            build(SwtSpinner(container, style), init)

    fun styledText(style: Int = SWT.DEFAULT, init: SwtStyledText.() -> Unit = {}) =
            build(SwtStyledText(container, style), init)

    fun table(style: Int = SWT.DEFAULT, init: SwtTable.() -> Unit = {}) =
            build(SwtTable(container, style), init)

    fun tabFolder(style: Int = SWT.DEFAULT, init: SwtTabFolder.() -> Unit = {}) =
            build(SwtTabFolder(container, style), init)

    fun text(style: Int = SWT.DEFAULT, init: SwtText.() -> Unit = {}) =
            build(SwtText(container, style), init)

    fun toolbar(style: Int = SWT.DEFAULT, init: SwtToolBar.() -> Unit = {}) =
            build(SwtToolBar(container, style), init)

    fun tree(style: Int = SWT.DEFAULT, init: SwtTree.() -> Unit = {}) =
            build(SwtTree(container, style), init)

}

class SwtShell(parent: Display, style: Int) : Shell(parent, style), SwtContainer {
    override val container = this

    fun menuBar(style: Int = SWT.LEFT_TO_RIGHT, init: SwtMenu.() -> Unit = {}) =
            build(SwtMenu(this, SWT.BAR or style), init)

}

class SwtComposite(parent: Composite, style: Int) : Composite(parent, style), SwtContainer {
    override val container: Composite = this
}

@SwtDsl class SwtButton(parent: Composite, style: Int) : Button(parent, style)
@SwtDsl class SwtCanvas(parent: Composite) : Canvas(parent, SWT.NONE)

@SwtDsl
class SwtCombo(parent: Composite, style: Int) : Combo(parent, style) {

    fun String.unaryPlus() {
        setItem(itemCount, this)
    }

    fun String.unaryMinus() {
        remove(this)
    }

    fun IntRange.unaryMinus() {
        remove(this.start, this.endInclusive)
    }

}

@SwtDsl class SwtDateTime(parent: Composite, style: Int) : DateTime(parent, style)

class SwtGroup(parent: Composite, style: Int) : Group(parent, style), SwtContainer {
    override val container = this
}

@SwtDsl class SwtLabel(parent: Composite, style: Int) : Label(parent, style)
@SwtDsl class SwtLink(parent: Composite, style: Int) : Link(parent, style)

@SwtDsl
class SwtList(parent: Composite, style: Int) : List(parent, style) {

    fun String.unaryPlus() {
        setItem(itemCount, this)
    }

    fun String.unaryMinus() {
        remove(this)
    }

    fun IntRange.unaryMinus() {
        remove(this.start, this.endInclusive)
    }

}

@SwtDsl class SwtProgressBar(parent: Composite, style: Int) : ProgressBar(parent, style)
@SwtDsl class SwtSash(parent: Composite, style: Int) : Sash(parent, style)
@SwtDsl class SwtScale(parent: Composite, style: Int) : Scale(parent, style)
@SwtDsl class SwtSlider(parent: Composite, style: Int) : Slider(parent, style)
@SwtDsl class SwtSpinner(parent: Composite, style: Int) : Spinner(parent, style)

@SwtDsl
class SwtText(parent: Composite, style: Int) : Text(parent, style) {

    fun String.unaryPlus() {
        append(this)
    }

}

@SwtDsl
class SwtStyledText(parent: Composite, style: Int) : StyledText(parent, style) {

    fun String.unaryPlus() {
        append(this)
    }

}

@SwtDsl
class SwtMenu : Menu {

    constructor(parent: Control) : super(parent)
    constructor(parent: Decorations, style: Int) : super(parent, style)
    constructor(parent: Menu) : super(parent)
    constructor(parent: MenuItem) : super(parent)

    fun item(style: Int = SWT.DEFAULT, index: Int = itemCount, init: SwtMenuItem.() -> Unit = {}) =
            build(SwtMenuItem(this, style, index), init)

    fun menu(init: SwtMenu.() -> Unit = {}) = build(SwtMenu(this), init)
}

@SwtDsl
class SwtMenuItem(parent: Menu, style: Int, index: Int) : MenuItem(parent, style, index) {
    fun menu(init: SwtMenu.() -> Unit = {}) = build(SwtMenu(this), init)
}

@SwtDsl
class SwtTabFolder(parent: Composite, style: Int) : TabFolder(parent, style) {

    fun tab(style: Int = SWT.DEFAULT, init: SwtTabItem.() -> Unit = {}) =
            build(SwtTabItem(this, style), init)

    fun tab(style: Int = SWT.DEFAULT, index: Int, init: SwtTabItem.() -> Unit = {}) =
            build(SwtTabItem(this, style, index), init)

}

@SwtDsl
class SwtTabItem : TabItem {

    constructor(parent: TabFolder, style: Int) : super(parent, style)
    constructor(parent: TabFolder, style: Int, index: Int) : super(parent, style, index)

}

@SwtDsl
class SwtTree(parent: Composite, style: Int) : Tree(parent, style) {

    fun item(style: Int = SWT.DEFAULT, init: SwtTreeItem.() -> Unit = {}) =
            build(SwtTreeItem(this, style), init)

    fun item(style: Int = SWT.DEFAULT, index: Int, init: SwtTreeItem.() -> Unit = {}) =
            build(SwtTreeItem(this, style, index), init)

    fun column(style: Int = SWT.DEFAULT, init: SwtTreeColumn.() -> Unit = {}) =
            build(SwtTreeColumn(this, style), init)

    fun column(style: Int = SWT.DEFAULT, index: Int, init: SwtTreeColumn.() -> Unit = {}) =
            build(SwtTreeColumn(this, style, index), init)

}

@SwtDsl
class SwtTreeItem : TreeItem {

    constructor(parent: Tree, style: Int) : super(parent, style)
    constructor(parent: Tree, style: Int, index: Int) : super(parent, style, index)
    constructor(parent: TreeItem, style: Int) : super(parent, style)
    constructor(parent: TreeItem, style: Int, index: Int) : super(parent, style, index)

    fun item(style: Int = SWT.DEFAULT, init: SwtTreeItem.() -> Unit = {}) =
            build(SwtTreeItem(this, style), init)

    fun item(style: Int = SWT.DEFAULT, index: Int, init: SwtTreeItem.() -> Unit = {}) =
            build(SwtTreeItem(this, style, index), init)

}

@SwtDsl
class SwtTreeColumn : TreeColumn {

    constructor(parent: Tree, style: Int) : super(parent, style)
    constructor(parent: Tree, style: Int, index: Int) : super(parent, style, index)

}

@SwtDsl
class SwtTable(parent: Composite, style: Int) : Table(parent, style) {

    fun column(style: Int = SWT.DEFAULT, init: SwtTableColumn.() -> Unit = {}) =
            build(SwtTableColumn(this, style), init)

    fun column(style: Int = SWT.DEFAULT, index: Int, init: SwtTableColumn.() -> Unit = {}) =
            build(SwtTableColumn(this, style, index), init)

    fun item(style: Int = SWT.DEFAULT, init: SwtTableItem.() -> Unit = {}) =
            build(SwtTableItem(this, style), init)

    fun item(style: Int = SWT.DEFAULT, index: Int, init: SwtTableItem.() -> Unit = {}) =
            build(SwtTableItem(this, style, index), init)
}

@SwtDsl
class SwtTableColumn : TableColumn {

    constructor(parent: Table, style: Int) : super(parent, style)
    constructor(parent: Table, style: Int, index: Int) : super(parent, style, index)

}

@SwtDsl
class SwtTableItem : TableItem {

    constructor(parent: Table, style: Int) : super(parent, style)
    constructor(parent: Table, style: Int, index: Int) : super(parent, style, index)

}

@SwtDsl
class SwtToolBar(parent: Composite, style: Int) : ToolBar(parent, style) {

    fun item(style: Int = SWT.DEFAULT, init: SwtToolItem.() -> Unit = {}) =
            build(SwtToolItem(this, style), init)

    fun item(style: Int = SWT.DEFAULT, index: Int, init: SwtToolItem.() -> Unit = {}) =
            build(SwtToolItem(this, style, index), init)

}

@SwtDsl
class SwtToolItem : ToolItem {

    constructor(parent: ToolBar, style: Int): super(parent, style)
    constructor(parent: ToolBar, style: Int, index: Int): super(parent, style, index)

}

fun shell(display: Display = Display.getCurrent(), style: Int = SWT.SHELL_TRIM, init: SwtShell.() -> Unit = {}) =
        build(SwtShell(display, style), init)

fun composite(parent: Composite, style: Int = SWT.DEFAULT, init: SwtComposite.() -> Unit = {}) =
        build(SwtComposite(parent, style), init)

fun menu(parent: Decorations, style: Int = SWT.DEFAULT, init: SwtMenu.() -> Unit = {}) =
        build(SwtMenu(parent, style), init)

fun menu(parent: Control, init: SwtMenu.() -> Unit = {}) =
        build(SwtMenu(parent), init)
