package de.florianwip.ktinventory.button.list

import de.florianwip.ktinventory.inventory.list.ListInventory
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * Build a [ListButton] for a [ListInventory]
 *
 * @param T Type of the List Element
 * @param I Type of the [ListInventory]
 * @param block The [ListButtonBuilder]
 * @return A new [ListButton]
 */
fun <T: Any, I: ListInventory<T, I>> buildListButton(block: ListButtonBuilder<T, I>.() -> Unit): ListButton<T, I> {
    return ListButtonBuilder<T, I>().apply(block).build()
}

/**
 * Build a [ListButton] for a [ListInventory] without any actions
 *
 * @param T Type of the List Element
 * @param I Type of the [ListInventory]
 * @param item The [ItemStack] visible in the GUI
 * @return A new [ListButton]
 */
fun <T: Any, I: ListInventory<T, I>> buildDummyListButton(item: ItemStack): ListButton<T, I> = buildListButton {
    this.item = item
}

/**
 * A Builder to create a [ListButton]
 *
 * @param T Type of the List Element
 * @param I Type of the [ListInventory]
 */
class ListButtonBuilder<T: Any, I: ListInventory<T, I>> {

    /**
     * The [ItemStack] visible in the GUI
     */
    var item: ItemStack? = null

    /**
     * The Map containing [ListClickListener] by their [ClickType]
     */
    val clickActions = mutableMapOf<ClickType, ListClickListener<T, I>>()

    /**
     * Add an Action for a specific [ClickType]
     *
     * @param type The [ClickType] which triggers the Action
     * @param listener The Listener to this Action
     */
    fun on(type: ClickType, listener: (event: InventoryClickEvent, base: I, i: T) -> Boolean) {
        clickActions[type] = ClickListenerImpl(listener)
    }

    /**
     * Build the resulting [ListButton]
     *
     * @return A new [ListButton]
     */
    fun build(): ListButton<T, I> {
        val display = requireNotNull(item) { "'item' must be set"}
        return ButtonImpl(display, clickActions)
    }
}

private data class ButtonImpl<T: Any, I: ListInventory<T, I>>(
    override val item: ItemStack,
    override val clickActions: Map<ClickType, ListClickListener<T, I>>
): ListButton<T, I>

private data class ClickListenerImpl<T: Any, I:  ListInventory<T, I>>(
    val action: (event: InventoryClickEvent, base: I, i: T) -> Boolean
): ListClickListener<T, I> {
    override fun onClick(event: InventoryClickEvent, base: I, i: T): Boolean {
        return action(event, base, i)
    }
}