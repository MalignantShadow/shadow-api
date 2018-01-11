@file:Suppress("unused")

package info.malignantshadow.api.gui.view

import info.malignantshadow.api.gui.SwtContainer
import info.malignantshadow.api.gui.SwtDsl
import info.malignantshadow.api.gui.composite
import info.malignantshadow.api.gui.formData
import info.malignantshadow.api.gui.formLayout
import info.malignantshadow.api.util.build
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.ScrolledComposite
import org.eclipse.swt.custom.StackLayout
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.FormAttachment
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem

/**
 * Represents a view where the user can select an item on a [Tree] widget and the composite
 * to the right of the view changes. This type of view is typically used in IDEs an Settings
 * windows.
 *
 * @param parent The composite in which to add the container
 * @param treeWidth The width of the Tree, in terms of a percentage
 * @param treeStyle Additional styles for the Tree (defaults to `0`).
 * [BORDER][SWT.BORDER], [H_SCROLL][SWT.H_SCROLL], and [V_SCROLL][SWT.V_SCROLL] are applied by default
 */
@Suppress("CanBeParameter")
@SwtDsl
class OptionComposite(val parent: Composite, treeWidth: Int, treeStyle: Int = 0) {

    private val _treeWidth = treeWidth
    private val _stackLayout: StackLayout = StackLayout()
    private lateinit var _scrolled: ScrolledComposite
    private lateinit var _stacked: Composite
    private lateinit var _tree: Tree
    private val _items = ArrayList<Item>()
    private var _selected: TreeItem? = null

    /**
     * The containing composite (the direct child of the `parent` parameter)
     */
    val container: Composite

    /**
     * The ScrolledComposite to the right of the Tree
     */
    val scrolled get() = _scrolled

    /**
     * The Tree
     */
    val tree get() = _tree

    init {
        container = composite(parent) {
            layout = formLayout {
                spacing = 5
                marginWidth = 5
                marginHeight = 5
            }

            this@OptionComposite._tree = tree(SWT.BORDER or SWT.H_SCROLL or SWT.V_SCROLL or treeStyle) {
                layoutData = formData(0, 0, 100, this@OptionComposite._treeWidth)
                addListener(SWT.Selection) {
                    if ((it.item as TreeItem) != this@OptionComposite._selected)
                        this@OptionComposite.select(it.item as TreeItem)
                }
            }

            this@OptionComposite._scrolled = scrolledComposite(SWT.H_SCROLL or SWT.V_SCROLL) {
                layout = FillLayout()
                layoutData = formData {
                    top = FormAttachment(0)
                    left = FormAttachment(this@OptionComposite._tree, 0, SWT.RIGHT)
                    bottom = FormAttachment(100)
                    right = FormAttachment(100)
                }

                this@OptionComposite._stacked = composite {
                    layout = this@OptionComposite._stackLayout
                }

                content = this@OptionComposite._stacked
                expandHorizontal = true
                expandVertical = true
            }
        }
    }

    /**
     * Indicates whether the given TreeItem can be found on the Tree
     */
    operator fun contains(item: TreeItem) = _items.firstOrNull { it.item === item } != null

    /**
     * Select the given item in the Tree and update the Composite on the right to match.
     * If the given TreeItem is not contained in the Tree, nothing happens
     *
     * @param item The tree item
     */
    fun select(item: TreeItem) {
        _items.forEach {
            if (it.item === item) {
                _tree.setSelection(item)
                _stackLayout.topControl = it.container
                _stacked.layout()
                _selected = item
                _scrolled.setMinSize(it.container.computeSize(SWT.DEFAULT, SWT.DEFAULT))
                return@select
            }
        }
    }

    /**
     * Select an item on the Tree and update the Composite on the right to match.
     * An item's index is 0-based, according to when it was added to this
     * OptionComposite
     */
    fun select(index: Int) {
        val treeItem = _items.getOrNull(index)?.item
        if (treeItem != null) select(treeItem)
    }

    /**
     * Add an item to this OptionComposite
     *
     * @param title The text of the TreeItem
     */
    fun item(title: String, init: Item.() -> Unit = {}) = item(null, title, init)

    /**
     * Add an item to this OptionComposite.
     *
     * @param parent The parent of the item. If the given parent is not found in the tree, the
     * item added will be top-level
     * @param title The text of the TreeItem
     */
    fun item(parent: TreeItem?, title: String, init: Item.() -> Unit = {}) =
            build(addItem(if(parent != null && contains(parent)) parent else null, title), init)

    /**
     * Add an item to this OptionComposite.
     *
     * @param parent The parent of the item. If the given parent is not found in the tree, the
     * item added will be top-level
     * @param title The text of the TreeItem
     */
    fun addItem(parent: TreeItem? = null, title: String): Item {
        val item =
                if (parent == null || !contains(parent)) TreeItem(_tree, SWT.NONE)
                else TreeItem(parent, SWT.NONE)
        item.text = title
        val it = Item(this, item, Composite(_stacked, SWT.NONE))
        _items.add(it)
        return it
    }

    /**
     * Represents an item in the OptionComposite, which contains the OptionComposite instance it is
     * attached to, the TreeItem, and the Composite to be shown if selected.
     */
    data class Item(val parent: OptionComposite, val item: TreeItem, override val container: Composite) : SwtContainer {

        val isRoot = item.parentItem == null

        fun item(title: String, init: Item.() -> Unit = {}) =
                build(parent.addItem(item, title), init)
    }

}

/**
 * Create an OptionComposite as a child of this SwtContainer
 */
fun SwtContainer.optionComposite(treeWidth: Int, treeStyle: Int = 0, init: OptionComposite.() -> Unit) =
        optionComposite(container, treeWidth, treeStyle, init)

/**
 * Create an OptionComposite
 */
fun optionComposite(parent: Composite, treeWidth: Int, treeStyle: Int = 0, init: OptionComposite.() -> Unit) =
        build(OptionComposite(parent, treeWidth, treeStyle), init)