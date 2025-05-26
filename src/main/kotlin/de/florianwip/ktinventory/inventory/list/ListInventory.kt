package de.florianwip.ktinventory.inventory.list

import de.florianwip.ktinventory.inventory.InventoryBase
import de.florianwip.ktinventory.inventory.InventoryRegistry
import de.florianwip.ktinventory.button.list.ListButton
import de.florianwip.ktinventory.button.view.Button
import de.florianwip.ktinventory.inventory.InventoryProperties
import de.florianwip.ktinventory.util.InventoryHolder
import de.florianwip.ktinventory.util.InventoryHolderFactory
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import java.util.UUID

const val LIST_INVENTORY_SIZE = 54

/**
 * Build an inventory to list elements in it.
 *
 * @param T Type of the Element to list
 * @param I The Type of the Implementation
 * @property name title of the Inventory as [Component]
 */
abstract class ListInventory<T : Any, I : ListInventory<T, I>>(
    val name: Component,
) : InventoryBase<I> {

    /**
     * Get the List Elements for a specific player
     *
     * @param player the [Player]
     * @return A [List] with the Elements of the defined types
     */
    protected abstract fun entries(player: Player): List<T>

    /**
     * Convert the List Element to a [ListButton]
     *
     * @param t the List Element to convert
     * @return A [ListButton]
     */
    protected abstract fun convert(t: T): ListButton<T, I>

    /**
     * Get the border of the [de.florianwip.ktinventory.inventory.list.ListInventory]
     * @see [BorderBuilder]
     */
    protected abstract val border: Array<Button<I>?>

    /**
     * Returns the instance of the implemented [de.florianwip.ktinventory.inventory.list.ListInventory]
     */
    protected abstract val base: I

    /**
     * A map with all cached data
     */
    protected val cached = mutableMapOf<UUID, ListInventoryCache<T, I>>()

    /**
     * The custom holder to create an inventory with it
     */
    protected val holder = InventoryHolderFactory.getNewHolder()

    private var entriesPerPage = -1

    init {
        InventoryRegistry.bind(this)
    }

    /**
     * Open the next page for a [Player]
     *
     * Note: This method does not check if there is a next Page
     *
     * Note: Players who do not have this gui open will open it on the first page
     *
     * @param player the [Player]
     */
    fun nextPage(player: Player) {
        val cache = cached[player.uniqueId]
        if (cache == null) {
            open(player)
            return
        }
        openPage(player, cache.currentPage + 1)
    }

    /**
     * Open the previous page for a [Player]
     *
     * Note: This method does not check if there is a previous Page
     *
     * Note: Players who do not have this gui open will open it on the first page
     *
     * @param player the [Player]
     */
    fun previousPage(player: Player) {
        val cache = cached[player.uniqueId]
        if (cache == null) {
            open(player)
            return
        }
        openPage(player, cache.currentPage - 1)
    }

    /**
     * Get the current opened page of a [Player]
     *
     * @param player the [Player]
     * @return the current opened page, if closed: -1
     */
    fun getCurrentPage(player: Player): Int = cached[player.uniqueId]?.currentPage ?: -1

    /**
     * Open a specified page for a [Player]
     *
     * Note: If the page does not exist, an error will be printed in log and the inventory will be closed
     *
     * @param player the [Player]
     * @param page the page
     */
    fun openPage(player: Player, page: Int) {
        if (entriesPerPage == -1) {
            entriesPerPage = LIST_INVENTORY_SIZE - border.filter { it != null }.size
        }
        if (!cached.containsKey(player.uniqueId)) {
            open(player)
            return
        }
        val cache = cached[player.uniqueId]!!
        if (cache.maxPage < page || page < 0) {
            InventoryRegistry.plugin?.logger?.severe(
                "Tried to open non existing page (playerName=${player.name}, playerUuid=${player.uniqueId} tried=$page, max=${cache.maxPage})"
            )
            close(player)
        }
        cache.currentPage = page
        cache.buttons.clear()
        val inventory = if (isInventory(player.openInventory.topInventory)) {
            player.openInventory.topInventory
        } else {
            player.closeInventory()
            Bukkit.createInventory(holder, LIST_INVENTORY_SIZE, name)
        }

        var entry = 0
        for (i in 0 until LIST_INVENTORY_SIZE) {
            if (border.size <= i) {
                break
            }
            val button = border[i]
            if (button != null) {
                inventory.setItem(i, button.item)
            } else {
                val pos = page * entriesPerPage + entry
                if (pos >= cache.items.size) {
                    continue
                }
                val listItem = cache.items[pos]
                val button = convert(listItem)
                cache.buttons[i] = listItem to button
                inventory.setItem(i, button.item)
                entry++
            }
        }
        player.openInventory(inventory)
    }

    /**
     * Check if a slot is a border slot
     *
     * @param slot the slot
     * @return if it is a border slot
     */
    protected fun isBorder(slot: Int): Boolean {
        if (border.size > slot) {
            return border[slot] != null
        }
        return false
    }

    /**
     * The properties for this inventory
     * @see [InventoryProperties]
     */
    override var properties: InventoryProperties = InventoryRegistry.defaultProperties

    /**
     * Open the [ListInventory] for a specified [Player]
     *
     * @param player the [Player]
     */
    override fun open(player: Player) {
        val items = entries(player)
        cached[player.uniqueId] = ListInventoryCache(items)
        openPage(player, 0)
        if (properties.openSound != null) {
            player.playSound(player.location, properties.openSound!!, 1f, 1f)
        }
    }

    /**
     * Close the [ListInventory] for a specified [Player]
     *
     * @param player the [Player]
     */
    override fun close(player: Player) {
        cached.remove(player.uniqueId)
        player.closeInventory()
        if (properties.closeSound != null) {
            player.playSound(player.location, properties.closeSound!!, 1f, 1f)
        }
    }

    /**
     * Check if the provided [Inventory] is an [Inventory] of this [ListInventory]
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
     * This method has no effect on a [ListInventory]
     *
     * @param button no effect
     */
    override fun addButton(button: Button<I>) {
        return
    }

    /**
     * This method has no effect on a [ListInventory]
     *
     * @param button no effect
     */
    override fun setButton(slot: Int, button: Button<I>) {
        return
    }

    /**
     * Handle an [InventoryClickEvent]
     *
     * @param event the [InventoryClickEvent]
     */
    override fun handleClick(event: InventoryClickEvent) {
        if (!isInventory(event.inventory)) {
            return
        }
        val slot = event.slot
        if (slot < 0) {
            return
        }
        if (isBorder(slot)) {
            val button = border[slot]
            val cancel = button?.clickActions[event.click]?.onClick(event, base) ?: true
            event.isCancelled = cancel
        } else {
            var cancel = true
            cached[event.whoClicked.uniqueId]?.let { cache ->
                cache.buttons[slot]?.let {
                    val (item, button) = it
                    cancel = button.clickActions[event.click]?.onClick(event, base, item) ?: true
                }
            }
            event.isCancelled = cancel
        }
    }

    /**
     * This method has no effect on a [ListInventory]
     *
     * @param event no effect
     */
    override fun handleOpen(event: InventoryOpenEvent) {
    }

    /**
     * This method has no effect on a [ListInventory]
     *
     * @param event no effect
     */
    override fun handleClose(event: InventoryCloseEvent) {
    }
}

/**
 * Class to cache all values related to an [ListInventory]
 *
 * @param T The type of the List Element
 * @param I The type of the [ListInventory]
 * @property items The cached items
 * @property buttons The cached buttons
 * @property currentPage The current page
 */
class ListInventoryCache<T: Any, I : ListInventory<T, I>>(
    val items: List<T>,
    var buttons: MutableMap<Int, Pair<T, ListButton<T, I>>> = mutableMapOf(),
    var currentPage: Int = 1,
) {
    /**
     * The max page
     */
    val maxPage = items.size / LIST_INVENTORY_SIZE
}
