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

/*
 * Note - Each class that extends an SWT Widget in this file overrides the
 * checkSubclass method, which would otherwise throw an exception if these classes were used.
 *
 * These classes do not alter their parent, they are only used to provide a DSL to make it easier
 * to create views. Some functions are added to the classes, but they simply act as aliases to other
 * functions of their parent.
 *
 * Put simply, the SWT dev team allows the overriding of 'checkSubclass' as long as the implementer
 * knows what they're doing.
 */

/**
 * The DSL marker. Add this annotation to a class to expand the SWT DSL.
 */
@DslMarker
@Target(AnnotationTarget.CLASS)
annotation class SwtDsl

/**
 * Represents a container object, one that can act as a parent for widgets and controls.
 *
 * Examples: Composite (via [composite], [SwtContainer.composite]), Shell (via [shell]]),
 * Group (via [group]).
 */
@SwtDsl
interface SwtContainer {

    /**
     * The Composite that widgets/controls will be added to.
     */
    val container: Composite

    /**
     * Create a [Composite] with the given style.
     *
     * @param style The style of the Composite
     * @return the Composite
     */
    fun composite(style: Int = SWT.NONE, init: SwtComposite.() -> Unit = {}) =
            build(SwtComposite(container, style), init)

    /**
     * Create a [Button] with the given style.
     *
     * @param style The style of the Composite
     * @return the Button
     */
    fun button(style: Int = SWT.DEFAULT, init: SwtButton.() -> Unit = {}) =
            build(SwtButton(container, style), init)

    /**
     * Create a [Canvas] with the given style.
     *
     * @param style The style of the Composite
     * @return the Canvas
     */
    fun canvas(style: Int = SWT.DEFAULT, init: SwtCanvas.() -> Unit = {}) =
            build(SwtCanvas(container), init)

    /**
     * Create a [Combo] with the given style.
     *
     * @param style The style of the Composite
     * @return the Combo
     */
    fun combo(style: Int = SWT.DEFAULT, init: SwtCombo.() -> Unit = {}) =
            build(SwtCombo(container, style), init)

    /**
     * Create a [DateTime] with the given style.
     *
     * @param style The style of the Composite
     * @return the DateTime
     */
    fun dateTime(style: Int = SWT.DEFAULT, init: SwtDateTime.() -> Unit = {}) =
            build(SwtDateTime(container, style), init)

    /**
     * Create a [Group] with the given style.
     *
     * @param style The style of the Composite
     * @return the Group
     */
    fun group(style: Int = SWT.DEFAULT, init: SwtGroup.() -> Unit = {}) =
            build(SwtGroup(container, style), init)

    /**
     * Create a [Label] with the given style.
     *
     * @param style The style of the Composite
     * @return the Label
     */
    fun label(style: Int = SWT.DEFAULT, init: SwtLabel.() -> Unit = {}) =
            build(SwtLabel(container, style), init)

    /**
     * Create a [Link] with the given style.
     *
     * @param style The style of the Composite
     * @return the Link
     */
    fun link(style: Int = SWT.DEFAULT, init: SwtLink.() -> Unit = {}) =
            build(SwtLink(container, style), init)

    /**
     * Create a [Link] with the given style.
     *
     * @param style The style of the Composite
     * @return the Link
     */
    fun list(style: Int = SWT.DEFAULT, init: SwtList.() -> Unit = {}) =
            build(SwtList(container, style), init)

    /**
     * Create a [ProgressBar] with the given style.
     *
     * @param style The style of the Composite
     * @return the ProgressBar
     */
    fun progressBar(style: Int = SWT.DEFAULT, init: SwtProgressBar.() -> Unit = {}) =
            build(SwtProgressBar(container, style), init)

    /**
     * Create a [Sash] with the given style.
     *
     * @param style The style of the Composite
     * @return the Sash
     */
    fun sash(style: Int = SWT.DEFAULT, init: SwtSash.() -> Unit = {}) =
            build(SwtSash(container, style), init)

    /**
     * Create a [Scale] with the given style.
     *
     * @param style The style of the Composite
     * @return the Scale
     */
    fun scale(style: Int = SWT.DEFAULT, init: SwtScale.() -> Unit = {}) =
            build(SwtScale(container, style), init)

    /**
     * Create a [Slider] with the given style.
     *
     * @param style The style of the Composite
     * @return the Slider
     */
    fun slider(style: Int = SWT.DEFAULT, init: SwtSlider.() -> Unit = {}) =
            build(SwtSlider(container, style), init)

    /**
     * Create a [Spinner] with the given style.
     *
     * @param style The style of the Composite
     * @return the Spinner
     */
    fun spinner(style: Int = SWT.DEFAULT, init: SwtSpinner.() -> Unit = {}) =
            build(SwtSpinner(container, style), init)

    /**
     * Create a [StyledText] with the given style.
     *
     * @param style The style of the Composite
     * @return the StyledText
     */
    fun styledText(style: Int = SWT.DEFAULT, init: SwtStyledText.() -> Unit = {}) =
            build(SwtStyledText(container, style), init)

    /**
     * Create a [Table] with the given style.
     *
     * @param style The style of the Composite
     * @return the Table
     */
    fun table(style: Int = SWT.DEFAULT, init: SwtTable.() -> Unit = {}) =
            build(SwtTable(container, style), init)

    /**
     * Create a [TabFolder] with the given style.
     *
     * @param style The style of the Composite
     * @return the TabFolder
     */
    fun tabFolder(style: Int = SWT.DEFAULT, init: SwtTabFolder.() -> Unit = {}) =
            build(SwtTabFolder(container, style), init)

    /**
     * Create a [Text] with the given style.
     *
     * @param style The style of the Composite
     * @return the Text
     */
    fun text(style: Int = SWT.DEFAULT, init: SwtText.() -> Unit = {}) =
            build(SwtText(container, style), init)

    /**
     * Create a [ToolBar] with the given style.
     *
     * @param style The style of the Composite
     * @return the ToolBar
     */
    fun toolbar(style: Int = SWT.DEFAULT, init: SwtToolBar.() -> Unit = {}) =
            build(SwtToolBar(container, style), init)

    /**
     * Create a [Tree] with the given style.
     *
     * @param style The style of the Composite
     * @return the Tree
     */
    fun tree(style: Int = SWT.DEFAULT, init: SwtTree.() -> Unit = {}) =
            build(SwtTree(container, style), init)

}

class SwtShell(parent: Display, style: Int) : Shell(parent, style), SwtContainer {
    override val container = this
    override fun checkSubclass() {}

    /**
     * Create and set the menu bar of this shell
     *
     * @param style The style of the Menu
     * @return the Menu
     */
    fun menuBar(style: Int = SWT.LEFT_TO_RIGHT, init: SwtMenu.() -> Unit = {}): SwtMenu {
        val menu = build(SwtMenu(this, SWT.BAR or style), init)
        menuBar = menu
        return menu
    }

}

class SwtComposite(parent: Composite, style: Int) : Composite(parent, style), SwtContainer {
    override val container: Composite = this
    override fun checkSubclass() {}
}

@SwtDsl class SwtButton(parent: Composite, style: Int) : Button(parent, style) {
    override fun checkSubclass() {}
}

@SwtDsl class SwtCanvas(parent: Composite) : Canvas(parent, SWT.NONE) {
    override fun checkSubclass() {}
}

@SwtDsl
class SwtCombo(parent: Composite, style: Int) : Combo(parent, style) {

    override fun checkSubclass() {}

    /**
     * Add an item to this Combo
     */
    fun String.unaryPlus() {
        setItem(itemCount, this)
    }

    /**
     * Remove an item from this Combo
     */
    fun String.unaryMinus() {
        remove(this)
    }

    /**
     * Remove items from this Combo
     */
    fun IntRange.unaryMinus() {
        remove(this.start, this.endInclusive)
    }

}

@SwtDsl class SwtDateTime(parent: Composite, style: Int) : DateTime(parent, style) {
    override fun checkSubclass() {}
}

class SwtGroup(parent: Composite, style: Int) : Group(parent, style), SwtContainer {
    override val container = this
    override fun checkSubclass() {}
}

@SwtDsl class SwtLabel(parent: Composite, style: Int) : Label(parent, style) {
    override fun checkSubclass() {}
}

@SwtDsl class SwtLink(parent: Composite, style: Int) : Link(parent, style) {
    override fun checkSubclass() {}
}

@SwtDsl
class SwtList(parent: Composite, style: Int) : List(parent, style) {

    override fun checkSubclass() {}

    /**
     * Add an item to this List
     */
    fun String.unaryPlus() {
        setItem(itemCount, this)
    }

    /**
     * Remove an item from this List
     */
    fun String.unaryMinus() {
        remove(this)
    }

    /**
     * Remove items from this List
     */
    fun IntRange.unaryMinus() {
        remove(this.start, this.endInclusive)
    }

}

@SwtDsl class SwtProgressBar(parent: Composite, style: Int) : ProgressBar(parent, style) {
    override fun checkSubclass() {}
}

@SwtDsl class SwtSash(parent: Composite, style: Int) : Sash(parent, style) {
    override fun checkSubclass() {}
}

@SwtDsl class SwtScale(parent: Composite, style: Int) : Scale(parent, style) {
    override fun checkSubclass() {}
}

@SwtDsl class SwtSlider(parent: Composite, style: Int) : Slider(parent, style) {
    override fun checkSubclass() {}
}

@SwtDsl class SwtSpinner(parent: Composite, style: Int) : Spinner(parent, style) {
    override fun checkSubclass() {}
}

@SwtDsl
class SwtText(parent: Composite, style: Int) : Text(parent, style) {

    override fun checkSubclass() {}

    /**
     * Append the given text
     */
    fun String.unaryPlus() {
        append(this)
    }

}

@SwtDsl
class SwtStyledText(parent: Composite, style: Int) : StyledText(parent, style) {

    override fun checkSubclass() {}

    /**
     * Append the given text
     */
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

    override fun checkSubclass() {}

    /**
     * Create a menu item with the given style
     *
     * @param style The style of the menu item
     * @return the menu item
     */
    fun item(style: Int = SWT.DEFAULT, index: Int = itemCount, init: SwtMenuItem.() -> Unit = {}) =
            build(SwtMenuItem(this, style, index), init)

    /**
     * Create a sub-menu
     *
     * @return the menu
     */
    fun menu(init: SwtMenu.() -> Unit = {}) = build(SwtMenu(this), init)
}

@SwtDsl
class SwtMenuItem(parent: Menu, style: Int, index: Int) : MenuItem(parent, style, index) {

    override fun checkSubclass() {}

    /**
     * Create a sub-menu
     *
     * @return the menu
     */
    fun menu(init: SwtMenu.() -> Unit = {}) = build(SwtMenu(this), init)

}

@SwtDsl
class SwtTabFolder(parent: Composite, style: Int) : TabFolder(parent, style) {

    override fun checkSubclass() {}

    /**
     * Add a tab with the given style.
     *
     * @param style The style of the tab item
     * @return the tab item
     */
    fun tab(style: Int = SWT.DEFAULT, init: SwtTabItem.() -> Unit = {}) =
            build(SwtTabItem(this, style), init)

    /**
     * Add a tab with the given style a the specified index.
     *
     * @param style The style of the tab item
     * @param index The index of the tab item
     * @return the tab item
     */
    fun tab(style: Int = SWT.DEFAULT, index: Int, init: SwtTabItem.() -> Unit = {}) =
            build(SwtTabItem(this, style, index), init)

}

@SwtDsl
class SwtTabItem : TabItem {

    override fun checkSubclass() {}

    constructor(parent: TabFolder, style: Int) : super(parent, style)
    constructor(parent: TabFolder, style: Int, index: Int) : super(parent, style, index)

}

@SwtDsl
class SwtTree(parent: Composite, style: Int) : Tree(parent, style) {

    override fun checkSubclass() {}

    /**
     * Add a tree item with the given style
     *
     * @return the tree item
     */
    fun item(init: SwtTreeItem.() -> Unit = {}) =
            build(SwtTreeItem(this, SWT.NONE), init)

    /**
     * Add a tree item with the given style at the specified index
     *
     * @param index The index of the tree item
     * @return the tree item
     */
    fun item(index: Int, init: SwtTreeItem.() -> Unit = {}) =
            build(SwtTreeItem(this, SWT.NONE, index), init)

    /**
     * Add a tree column with the given style
     *
     * @param style The style of the column
     * @return the tree item
     */
    fun column(style: Int = SWT.DEFAULT, init: SwtTreeColumn.() -> Unit = {}) =
            build(SwtTreeColumn(this, style), init)

    /**
     * Add a tree column with the given style at the specified index
     *
     * @param style The style of the column
     * @param index The index of the tree column
     * @return the tree item
     */
    fun column(style: Int = SWT.DEFAULT, index: Int, init: SwtTreeColumn.() -> Unit = {}) =
            build(SwtTreeColumn(this, style, index), init)

}

@SwtDsl
class SwtTreeItem : TreeItem {

    override fun checkSubclass() {}

    constructor(parent: Tree, style: Int) : super(parent, style)
    constructor(parent: Tree, style: Int, index: Int) : super(parent, style, index)
    constructor(parent: TreeItem, style: Int) : super(parent, style)
    constructor(parent: TreeItem, style: Int, index: Int) : super(parent, style, index)

    /**
     * Add a tree item with the given style
     *
     * @param style The style of the item
     * @return the tree item
     */
    fun item(style: Int = SWT.DEFAULT, init: SwtTreeItem.() -> Unit = {}) =
            build(SwtTreeItem(this, style), init)

    /**
     * Add a tree item with the given style at the specified index
     *
     * @param style The style of the item
     * @param index The index of the tree item
     * @return the tree item
     */
    fun item(style: Int = SWT.DEFAULT, index: Int, init: SwtTreeItem.() -> Unit = {}) =
            build(SwtTreeItem(this, style, index), init)

}

@SwtDsl
class SwtTreeColumn : TreeColumn {

    override fun checkSubclass() {}

    constructor(parent: Tree, style: Int) : super(parent, style)
    constructor(parent: Tree, style: Int, index: Int) : super(parent, style, index)

}

@SwtDsl
class SwtTable(parent: Composite, style: Int) : Table(parent, style) {

    override fun checkSubclass() {}

    /**
     * Add a column with the given style
     *
     * @param style The style of the column
     * @return the column
     */
    fun column(style: Int = SWT.DEFAULT, init: SwtTableColumn.() -> Unit = {}) =
            build(SwtTableColumn(this, style), init)

    /**
     * Add a column with the given style at the specified index
     *
     * @param style The style of the column
     * @param index The index of the column
     * @return the column
     */
    fun column(style: Int = SWT.DEFAULT, index: Int, init: SwtTableColumn.() -> Unit = {}) =
            build(SwtTableColumn(this, style, index), init)

    /**
     * Add an item with the given style at the specified index
     *
     * @param style The style of the item
     * @return the item
     */
    fun item(style: Int = SWT.DEFAULT, init: SwtTableItem.() -> Unit = {}) =
            build(SwtTableItem(this, style), init)

    /**
     * Add an item with the given style at the specified index
     *
     * @param style The style of the item
     * @param index The index of the item
     * @return the item
     */
    fun item(style: Int = SWT.DEFAULT, index: Int, init: SwtTableItem.() -> Unit = {}) =
            build(SwtTableItem(this, style, index), init)
}

@SwtDsl
class SwtTableColumn : TableColumn {

    override fun checkSubclass() {}

    constructor(parent: Table, style: Int) : super(parent, style)
    constructor(parent: Table, style: Int, index: Int) : super(parent, style, index)

}

@SwtDsl
class SwtTableItem : TableItem {

    override fun checkSubclass() {}

    constructor(parent: Table, style: Int) : super(parent, style)
    constructor(parent: Table, style: Int, index: Int) : super(parent, style, index)

}

@SwtDsl
class SwtToolBar(parent: Composite, style: Int) : ToolBar(parent, style) {

    override fun checkSubclass() {}

    /**
     * Add an item with the given style
     *
     * @param style The style of the item
     * @return the item
     */
    fun item(style: Int = SWT.DEFAULT, init: SwtToolItem.() -> Unit = {}) =
            build(SwtToolItem(this, style), init)

    /**
     * Add an item with the given style at the specified index
     *
     * @param style The style of the item
     * @param index The index of the item
     * @return the item
     */
    fun item(style: Int = SWT.DEFAULT, index: Int, init: SwtToolItem.() -> Unit = {}) =
            build(SwtToolItem(this, style, index), init)

}

@SwtDsl
class SwtToolItem : ToolItem {

    override fun checkSubclass() {}

    constructor(parent: ToolBar, style: Int) : super(parent, style)
    constructor(parent: ToolBar, style: Int, index: Int) : super(parent, style, index)

}

/**
 * Create a Shell with the given style
 *
 * @param display The display, defaults to `Display.getCurrent()`
 * @param style The style of the shell, default to `SWT.SHELL_TRIM`
 * @return the shell
 */
fun shell(display: Display = Display.getCurrent(), style: Int = SWT.SHELL_TRIM, init: SwtShell.() -> Unit = {}) =
        build(SwtShell(display, style), init)

/**
 * Create a Composite with the given style
 *
 * @param parent The parent Composite
 * @return the Composite
 */
fun composite(parent: Composite, style: Int = SWT.NONE, init: SwtComposite.() -> Unit = {}) =
        build(SwtComposite(parent, style), init)

/**
 * Create a menu
 *
 * @param parent The parent
 * @param style The style of the menu
 * @return the menu
 */
fun menu(parent: Decorations, style: Int = SWT.DEFAULT, init: SwtMenu.() -> Unit = {}) =
        build(SwtMenu(parent, style), init)

/**
 * Create a menu
 *
 * @param parent The parent control
 * @return the menu
 */
fun menu(parent: Control, init: SwtMenu.() -> Unit = {}) =
        build(SwtMenu(parent), init)
