package de.florianwip.ktinventory.inventory.supplied

import de.florianwip.ktinventory.button.view.Button
import de.florianwip.ktinventory.inventory.InventoryBase
import de.florianwip.ktinventory.service.KtInventoryService
import de.florianwip.ktinventory.util.InventoryHolder
import de.florianwip.ktinventory.util.InventoryHolderFactory
import de.florianwip.ktinventory.util.playSound
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import java.util.UUID
import kotlin.math.log

open class SuppliedInventory(
    val name: (Player) -> Component,
    val rows: Int = 3,
    var background: Array<Button<SuppliedInventory>?>? = null,
    service: KtInventoryService? = null,
    val globalSupplier: (Player) -> Map<Int, Button<SuppliedInventory>> = { emptyMap() }
): InventoryBase<SuppliedInventory> {

    private var _service: KtInventoryService? = null

    private val buttons = mutableMapOf<Int, Button<SuppliedInventory>>()

    private val playerButtons = mutableMapOf<UUID, Map<Int, Button<SuppliedInventory>>>()

    private val holder = InventoryHolderFactory.getNewHolder()

    init {
        if (service != null) {
            register(service)
        }
    }

    override fun register(service: KtInventoryService): SuppliedInventory {
        if (_service != null) {
            throw IllegalStateException("SuppliedInventory is already registered")
        }
        _service = service
        service.register(this)
        if (service.settings.viewInventoryBackground != null) {
            background = service.settings.viewInventoryBackground!!.getBackground(rows, service)
        }
        return this
    }

    override fun getService(): KtInventoryService? {
        return _service
    }

    override fun open(player: Player) {
        open(player, emptyMap())
    }

    fun open(player: Player, buttons: Map<Int, Button<SuppliedInventory>>) {
        val finalButtons = mutableMapOf<Int, Button<SuppliedInventory>>()
        if (background != null) {
            for (i in 0 until rows * 9) {
                if (i >= background!!.size) {
                    break
                }
                val button = background!![i]
                if (button != null) {
                    finalButtons[i] = button
                }
            }
        }
        finalButtons.putAll(this.buttons)
        finalButtons.putAll(globalSupplier.invoke(player))
        finalButtons.putAll(buttons)
        playerButtons[player.uniqueId] = finalButtons
        val inventory = Bukkit.createInventory(holder, rows * 9, name.invoke(player))
        player.closeInventory()
        player.openInventory(inventory)
        update(player)
    }

    fun update(player: Player) {
        val finalButtons = playerButtons[player.uniqueId] ?: return
        val inventory = player.openInventory.topInventory
        if (!isInventory(inventory)) {
            _service?.logger?.warning("Tried to update inventory for player ${player.name}, but the inventory is not managed by this SuppliedInventory")
            return
        }
        finalButtons.forEach { (slot, button) ->
            if (slot < inventory.size) {
                inventory.setItem(slot, button.item)
            }
        }
    }

    override fun close(player: Player) {
        player.closeInventory()
    }

    override fun isInventory(inventory: Inventory): Boolean {
        val holder = inventory.holder
        return holder is InventoryHolder && holder.uuid == this.holder.uuid
    }

    override fun addButton(button: Button<SuppliedInventory>) {
        val emptySlot = (0 until rows * 9).firstOrNull { !buttons.containsKey(it) }
            ?: throw IllegalStateException("No empty slot available to add button")
        buttons[emptySlot] = button
    }

    override fun setButton(
        slot: Int,
        button: Button<SuppliedInventory>
    ) {
        if (slot < 0 || slot >= rows * 9) {
            throw IllegalArgumentException("Slot $slot is out of bounds for inventory with ${rows * 9} slots")
        }
        buttons[slot] = button
    }

    override fun handleClick(event: InventoryClickEvent) {
        val inventory = event.inventory
        if (!isInventory(inventory)) {
            return
        }
        val player = event.whoClicked as Player
        val finalButtons = playerButtons[player.uniqueId]
        if (finalButtons == null) {
            _service?.logger?.severe("Player has SuppliedInventory open, but this player is unknown to this SuppliedInventory")
            event.isCancelled = true
            return
        }
        val button = finalButtons[event.slot]
        if (button != null) {
            val action = button.clickActions[event.click]
            if (action != null) {
                action.onClick(event, this)
            } else {
                event.isCancelled = true
            }
        }
    }

    override fun handleOpen(event: InventoryOpenEvent) {
        if (isInventory(event.inventory)) {
            val player = event.player as Player
            player.playSound(_service?.settings?.inventoryOpenSound ?: return)
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {
        if (isInventory(event.inventory)) {
            val player = event.player as Player
            playerButtons.remove(player.uniqueId)
            player.playSound(_service?.settings?.inventoryCloseSound ?: return)
        }
    }
}