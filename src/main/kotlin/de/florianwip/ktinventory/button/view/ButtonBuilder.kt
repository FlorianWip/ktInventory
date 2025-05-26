package de.florianwip.ktinventory.button.view

import de.florianwip.ktinventory.inventory.InventoryBase
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * Build a Button for a [InventoryBase] e.g. [de.florianwip.ktinventory.inventory.view.ViewInventory]
 *
 * @param T The type of the [InventoryBase]
 * @param block The [ButtonBuilder]
 * @return A created [Button]
 */
fun <T: InventoryBase<T>> buildButton(block: ButtonBuilder<T>.() -> Unit): Button<T> {
    return ButtonBuilder<T>().apply(block).build()
}

/**
 * Build a Button for a [InventoryBase] e.g. [de.florianwip.ktinventory.inventory.view.ViewInventory] without any actions
 *
 * @param T The type of the [InventoryBase]
 * @param item The [ItemStack] visible in the GUI
 * @return A created [Button]
 */
fun <T: InventoryBase<T>> buildDummyButton(item: ItemStack): Button<T> = buildButton {
    this.item = item
}

/**
 * A Builder to build a [Button]
 *
 * @param T Type of the [InventoryBase]
 */
class ButtonBuilder<T: InventoryBase<T>> {

    /**
     * The [ItemStack] visible in the GUI
     */
    var item: ItemStack? = null

    /**
     * The Map containing [ClickListener] by their [ClickType]
     */
    val clickActions = mutableMapOf<ClickType, ClickListener<T>>()

    /**
     * Add an Action for a specific [ClickType]
     *
     * @param type The [ClickType] which triggers the Action
     * @param listener The Listener to this Action
     */
    fun on(type: ClickType, listener: (event: InventoryClickEvent, base: T) -> Boolean) {
        clickActions[type] = ClickListenerImpl(listener)
    }

    /**
     * Build a [Button] with the defined properties
     *
     * @return A new [Button]
     */
    fun build(): Button<T> {
        val display = requireNotNull(item) { "'item' must be set"}
        return ButtonImpl(display, clickActions)
    }
}

private data class ButtonImpl<T: InventoryBase<T>>(
    override val item: ItemStack,
    override val clickActions: Map<ClickType, ClickListener<T>>
): Button<T>

private data class ClickListenerImpl<T: InventoryBase<T>>(
    val action: (event: InventoryClickEvent, base: T) -> Boolean
): ClickListener<T> {
    override fun onClick(event: InventoryClickEvent, base: T): Boolean {
        return action(event, base)
    }
}