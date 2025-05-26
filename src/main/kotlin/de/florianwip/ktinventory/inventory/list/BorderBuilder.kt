package de.florianwip.ktinventory.inventory.list

import de.florianwip.ktinventory.inventory.InventoryBase
import de.florianwip.ktinventory.button.view.Button

/**
 * Build a Border for a [InventoryBase]
 *
 * @param T Type of the [InventoryBase]
 * @param rows rows of the corresponding inventory
 * @param block the [BorderBuilder]
 * @return An [Array] containing null or [Button] values
 */
fun <T: InventoryBase<T>> buildBorder(rows: Int, block: BorderBuilder<T>.() -> Unit): Array<Button<T>?> {
    return BorderBuilder<T>(rows).apply(block).build()
}

/**
 * Build a Border for a [InventoryBase]
 *
 * @param T Type of the [InventoryBase]
 * @param rows rows of the corresponding inventory
 */
class BorderBuilder<T: InventoryBase<T>>(
    val rows: Int,
) {

    /**
     * A map with the slot as key and a [Button] as value
     */
    val buttons = mutableMapOf<Int, Button<T>>()

    /**
     * Set a [Button] for a specified slot
     *
     * @param slot the slot
     * @param button the [Button]
     */
    fun set(slot: Int, button: Button<T>) {
        buttons[slot] = button
    }

    /**
     * Build the actual border
     *
     * @return an Array with the specified length
     */
    fun build(): Array<Button<T>?> {
        val array = arrayOfNulls<Button<T>?>(rows * 9 )
        buttons.forEach { (slot, button) ->
            array[slot] = button
        }
        return array
    }
}