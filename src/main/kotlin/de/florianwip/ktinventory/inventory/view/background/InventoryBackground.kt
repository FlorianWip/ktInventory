package de.florianwip.ktinventory.inventory.view.background

import de.florianwip.ktinventory.inventory.InventoryBase
import de.florianwip.ktinventory.button.view.Button

interface InventoryBackground {

    /**
     * Generate a background for an [InventoryBase] e.g [de.florianwip.ktinventory.inventory.view.ViewInventory]
     *
     * @param T Type of the [InventoryBase]
     * @param rows amount of rows
     * @return [Array] of [Button] or null values in size of the rows
     */
    fun <T: InventoryBase<T>> getBackground(rows: Int): Array<Button<T>?>
}