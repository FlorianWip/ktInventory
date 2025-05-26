package de.florianwip.ktinventory.button.list

import de.florianwip.ktinventory.inventory.list.ListInventory
import org.bukkit.event.inventory.InventoryClickEvent

/**
 * A Listener for an Action
 *
 * @param T The Type of the List Element
 * @param I The Type of the [ListInventory]
 */
interface ListClickListener<T: Any, I: ListInventory<T, I>> {

    /**
     * The Method called by the [ListInventory]
     *
     * @param event The [InventoryClickEvent] where the Click was caught
     * @param base The Instance of the [ListInventory]
     * @param t The corresponding List Element
     * @return cancel the [InventoryClickEvent]
     */
    fun onClick(event: InventoryClickEvent, base: I, t: T): Boolean
}