package de.florianwip.ktinventory.button.list

import de.florianwip.ktinventory.inventory.list.ListInventory
import de.florianwip.ktinventory.item.ItemBuilder
import de.florianwip.ktinventory.item.MHFSkull
import de.florianwip.ktinventory.service.KtInventoryService
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
fun <T: Any, I: ListInventory<T, I>> buildListButton(service: KtInventoryService? = null, block: ListButtonBuilder<T, I>.() -> Unit): ListButton<T, I> {
    return ListButtonBuilder<T, I>(service).apply(block).build()
}

/**
 * Build a [ListButton] for a [ListInventory] without any actions
 *
 * @param T Type of the List Element
 * @param I Type of the [ListInventory]
 * @param itemStack The [ItemStack] visible in the GUI
 * @return A new [ListButton]
 */
@Deprecated(replaceWith = ReplaceWith("buildListButton<T, I> { item = itemStack }"), message = "Use buildListButton instead")
fun <T: Any, I: ListInventory<T, I>> buildDummyListButton(itemStack: ItemStack): ListButton<T, I> = buildListButton {
    this.item = itemStack
}

/**
 * A Builder to create a [ListButton]
 *
 * @param T Type of the List Element
 * @param I Type of the [ListInventory]
 */
class ListButtonBuilder<T: Any, I: ListInventory<T, I>>(
    val service: KtInventoryService? = null
) {

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

    /**
     * Build an [ItemStack] with the correct service applied
     *
     * @param block the [ItemBuilder] block
     * @return the built [ItemStack]
     */
    fun buildItem(block: ItemBuilder.() -> Unit): ItemStack {
        return de.florianwip.ktinventory.item.buildItem(service, block)
    }

    /**
     * Build a skull [ItemStack] with the correct service applied
     *
     * @param block the [ItemBuilder] block
     * @return the built skull [ItemStack]
     */
    fun MHFSkull.buildSkull(block: ItemBuilder.() -> Unit): ItemStack {
        return this.buildSkull(service, block)
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