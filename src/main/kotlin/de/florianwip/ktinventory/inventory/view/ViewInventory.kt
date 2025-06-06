package de.florianwip.ktinventory.inventory.view

import de.florianwip.ktinventory.inventory.InventoryBase
import de.florianwip.ktinventory.inventory.InventoryRegistry
import de.florianwip.ktinventory.button.view.Button
import de.florianwip.ktinventory.inventory.InventoryProperties
import de.florianwip.ktinventory.util.InventoryHolder
import de.florianwip.ktinventory.util.InventoryHolderFactory
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory

/**
 * Create a default GUI
 *
 * @property rows amount of rows
 * @property name title of the resulting [Inventory]
 * @property background the background or null to use the default background
 */
open class ViewInventory(
    val rows: Int,
    val name: Component,
    val background: Array<Button<ViewInventory>?>?
): InventoryBase<ViewInventory> {

    /**
     * The properties for this inventory
     * @see [InventoryProperties]
     */
    override var properties: InventoryProperties = InventoryRegistry.defaultProperties

    private val holder = InventoryHolderFactory.getNewHolder()

    constructor(rows: Int, name: String): this(rows, MiniMessage.miniMessage().deserialize(name), null)
    constructor(rows: Int, name: Component): this(rows, name, null)

    private val buttons = mutableMapOf<Int, Button<ViewInventory>>()

    init {
        InventoryRegistry.bind(this)
        var applyBackground: Array<Button<ViewInventory>?> = arrayOf()
        if (background != null) {
            applyBackground = background
        } else if (InventoryRegistry.defaultBackground != null) {
            applyBackground = InventoryRegistry.defaultBackground!!.getBackground(rows)
        }
        for (i in 0..(rows*9).minus(1)) {
            if (i >= applyBackground.size) {
                break
            }
            if (applyBackground[i] != null) {
                setButton(i, applyBackground[i]!!)
            }
        }
    }

    /**
     * Open the defined GUI for a [Player]
     *
     * @param player the [Player]
     */
    override fun open(player: Player) {
        val inventory = createInventory()
        player.closeInventory()
        player.openInventory(inventory)
    }

    /**
     * Close the defined GUI for a [Player]
     *
     * @param player the [Player]
     */
    override fun close(player: Player) {
        player.closeInventory()
    }

    private fun createInventory(): Inventory {
        val inventory = Bukkit.createInventory(holder, rows * 9, name)
        buttons.forEach {
            inventory.setItem(it.key, it.value.item)
        }
        return inventory
    }

    /**
     * Check if the provided [Inventory] is an [Inventory] of this [ViewInventory]
     *
     * @param inventory the [Inventory] to check
     * @return true if it is matching
     */
    override fun isInventory(inventory: Inventory): Boolean {
        if (inventory.holder != null) {
            if (inventory.holder is InventoryHolder) {
                return (inventory.holder as InventoryHolder).uuid == holder.uuid
            }
        }
        return false
    }

    /**
     * Adds a button to the first empty slot ignoring the background
     *
     * Note: If there is no empty slot, it will be set to the last one
     *
     * @param button the [Button] to add
     */
    override fun addButton(button: Button<ViewInventory>) {
        var slot = 0;
        while (buttons.containsKey(slot) || slot + 1 < rows * 9) {slot++}
        buttons[slot] = button
    }

    /**
     * Set the button to a specified slot
     *
     * @param slot the slot to set
     * @param button the [Button] to set
     */
    override fun setButton(
        slot: Int,
        button: Button<ViewInventory>
    ) {
        buttons[slot] = button
    }

    /**
     * Handle an [InventoryClickEvent]
     *
     * @param event the [InventoryClickEvent]
     */
    override fun handleClick(
        event: InventoryClickEvent
    ) {
        if (!isInventory(event.inventory)) {
            return
        }
        val slot = event.slot
        val button = buttons[slot]
        if (button != null) {
            val listener = button.clickActions[event.click]
            if (listener != null) {
                event.isCancelled = listener.onClick(event, this)
            } else {
                event.isCancelled = true
            }
        }
    }

    /**
     * Handle an [InventoryOpenEvent]
     *
     * @param event the [InventoryOpenEvent]
     */
    override fun handleOpen(
        event: InventoryOpenEvent
    ) {
        if (!isInventory(event.inventory)) {
            return
        }
        if (properties.openSound != null) {
            val player = event.player as Player
            player.playSound(player.location, properties.openSound!!, 1f, 1f)
        }
    }

    /**
     * Handle an [InventoryCloseEvent]
     *
     * @param event the [InventoryCloseEvent]
     */
    override fun handleClose(
        event: InventoryCloseEvent
    ) {
        if (!isInventory(event.inventory)) {
            return
        }
        if (properties.closeSound != null) {
            val player = event.player as Player
            player.playSound(player.location, properties.closeSound!!, 1f, 1f)
        }
    }
}