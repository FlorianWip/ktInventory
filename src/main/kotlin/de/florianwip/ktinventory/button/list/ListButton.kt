package de.florianwip.ktinventory.button.list

import de.florianwip.ktinventory.inventory.list.ListInventory
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

/**
 * A Button is a clickable Item in an [ListInventory]
 *
 * @param T The Type of the List Element
 * @param I The Type of the [ListInventory]
 */
interface ListButton<T: Any, I: ListInventory<T, I>> {

    /**
     * The [ItemStack] visible in the GUI
     */
    val item: ItemStack

    /**
     * The Map containing [ListClickListener] by their [ClickType]
     */
    val clickActions: Map<ClickType, ListClickListener<T, I>>

}