package de.florianwip.ktinventory.button.view

import de.florianwip.ktinventory.inventory.InventoryBase
import org.bukkit.event.inventory.InventoryClickEvent

/**
 * A Listener for an Action
 *
 * @param T The Type of the [InventoryBase]
 */
interface ClickListener<T: InventoryBase<T>> {

    /**
     * The Method called by the [InventoryBase]
     *
     * @param event The [InventoryClickEvent] where the Click was caught
     * @param base The Instance of the [InventoryBase]
     * @return cancel the [InventoryClickEvent]
     */
    fun onClick(event: InventoryClickEvent, base: T): Boolean
}