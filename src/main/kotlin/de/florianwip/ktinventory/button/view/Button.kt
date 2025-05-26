package de.florianwip.ktinventory.button.view

import de.florianwip.ktinventory.inventory.InventoryBase
import de.florianwip.ktinventory.inventory.list.ListInventory
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

/**
 * A Button is a clickable Item in an Inventory. Primarily used in [de.florianwip.ktinventory.inventory.view.ViewInventory] and the border of an [ListInventory]
 */
interface Button<T: InventoryBase<T>> {

    /**
     * The [ItemStack] visible in the GUI
     */
    val item: ItemStack

    /**
     * The Map containing [ClickListener] by their [ClickType]
     */
    val clickActions: Map<ClickType, ClickListener<T>>

}