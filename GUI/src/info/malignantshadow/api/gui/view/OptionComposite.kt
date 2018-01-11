package info.malignantshadow.api.gui.view

import info.malignantshadow.api.gui.SwtContainer
import info.malignantshadow.api.gui.SwtDsl
import info.malignantshadow.api.gui.composite
import info.malignantshadow.api.gui.formData
import info.malignantshadow.api.gui.formLayout
import info.malignantshadow.api.util.build
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StackLayout
import org.eclipse.swt.layout.FormAttachment
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem

@SwtDsl
class OptionComposite(val parent: Composite, treeWidth: Int) {

    private val _treeWidth = treeWidth
    private val _container: Composite
    private lateinit var _stacked: Composite
    private val _stackLayout: StackLayout = StackLayout()
    private lateinit var _tree: Tree
    private val _items = ArrayList<Item>()
    private var _selected: TreeItem? = null

    init {
        _container = composite(parent) {
            layout = formLayout {
                spacing = 5
                marginWidth = 5
                marginHeight = 5
            }

            this@OptionComposite._tree = tree(SWT.BORDER or SWT.H_SCROLL or SWT.V_SCROLL) {
                layoutData = formData(0, 0, 100, this@OptionComposite._treeWidth)
                addListener(SWT.Selection) {
                    if ((it.item as TreeItem) != this@OptionComposite._selected)
                        this@OptionComposite.select(it.item as TreeItem)
                }
            }

            this@OptionComposite._stacked = composite {
                layout = this@OptionComposite._stackLayout
                layoutData = formData {
                    top = FormAttachment(0)
                    left = FormAttachment(this@OptionComposite._tree, 0, SWT.RIGHT)
                    bottom = FormAttachment(100)
                    right = FormAttachment(100)
                }
            }
        }
    }

    fun select(item: TreeItem) {
        _items.forEach {
            if(it.item === item) {
                _tree.setSelection(item)
                _stackLayout.topControl = it.comp
                _stacked.layout()
                _selected = item
                return@select
            }
        }
    }

    fun item(title: String, init: Item.() -> Unit = {}) = item(null, title, init)

    fun item(parent: TreeItem?, title: String, init: Item.() -> Unit = {}) =
            build(addItem(parent, title), init)

    fun addItem(parent: TreeItem? = null, title: String): Item {
        val item =
                if(parent == null) TreeItem(_tree, SWT.NONE)
                else TreeItem(parent, SWT.NONE)
        item.text = title
        return Item(this, item, Composite(_stacked, SWT.NONE))
    }

    data class Item(val parent: OptionComposite, val item: TreeItem, val comp: Composite): SwtContainer {

        override val container: Composite = comp

        val isRoot = item.parentItem == null

        fun item(title: String, init: Item.() -> Unit= {}) =
                build(parent.addItem(item, title), init)
    }

}

fun SwtContainer.optionComposite(treeWidth: Int, init: OptionComposite.() -> Unit) =
        build(OptionComposite(container, treeWidth), init)