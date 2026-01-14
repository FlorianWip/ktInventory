package de.florianwip.ktinventory.inventory

import de.florianwip.ktinventory.button.view.Button
import de.florianwip.ktinventory.item.ItemBuilder
import de.florianwip.ktinventory.item.MHFSkull
import de.florianwip.ktinventory.item.buildItem
import de.florianwip.ktinventory.service.KtInventoryService
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * A base interface to represent a basic GUI
 *
 * @param T the implemented type of an [InventoryBase]
 */
interface InventoryBase<T: InventoryBase<T>> {

    /**
     * Register this [InventoryBase] to a [KtInventoryService]
     *
     * @param service the service to register
     * @return the current instance for chaining
     */
    fun register(service: KtInventoryService): T

    /**
     * The service bound to this [InventoryBase]
     */
    fun getService(): KtInventoryService?

    /**
     * Open the [InventoryBase] for a [Player]
     *
     * @param player the [Player]
     */
    fun open(player: Player)

    /**
     * Close the [InventoryBase] for a [Player]
     *
     * @param player the [Player]
     */
    fun close(player: Player)

    /**
     * Check if a specified [Inventory] is from this [InventoryBase]
     *
     * @param inventory the given [Inventory]
     * @return true if its matching
     */
    fun isInventory(inventory: Inventory): Boolean

    /**
     * Add a [Button]
     *
     * @param button the [Button]
     */
    fun addButton(button: Button<T>)

    /**
     * Set a [Button] on a specified slot
     *
     * @param slot the slot
     * @param button the [Button]
     */
    fun setButton(slot: Int, button: Button<T>)

    /**
     * Handle an [InventoryClickEvent]
     *
     * @param event the [InventoryClickEvent]
     */
    fun handleClick(event: InventoryClickEvent)

    /**
     * Handle an [InventoryOpenEvent]
     *
     * @param event the [InventoryOpenEvent]
     */
    fun handleOpen(event: InventoryOpenEvent)

    /**
     * Handle an [InventoryCloseEvent]
     *
     * @param event the [InventoryCloseEvent]
     */
    fun handleClose(event: InventoryCloseEvent)

    /**
     * Build an [ItemStack] with the correct service applied
     *
     * @param block the [ItemBuilder] block
     * @return the built [ItemStack]
     */
    fun buildItem(block: ItemBuilder.() -> Unit): ItemStack {
        return buildItem(getService(), block)
    }

    /**
     * Build a skull [ItemStack] with the correct service applied
     *
     * @param block the [ItemBuilder] block
     * @return the built skull [ItemStack]
     */
    fun MHFSkull.buildSkull(block: ItemBuilder.() -> Unit): ItemStack {
        return this.buildSkull(getService(), block)
    }
}