package de.florianwip.ktinventory.util

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import java.util.UUID

/**
 * Custom Inventory Holder to identify inventories independent of its title
 *
 * @property uuid
 */
class InventoryHolder(val uuid: UUID): InventoryHolder {

    private val dummyInventory = Bukkit.createInventory(this, 9)

    override fun getInventory(): Inventory = dummyInventory
}

/**
 * Factory to provide unique [de.florianwip.ktinventory.util.InventoryHolder]
 */
object InventoryHolderFactory {

    private var usedUuids = mutableListOf<UUID>()

    /**
     * Ge a new unique [de.florianwip.ktinventory.util.InventoryHolder]
     *
     * @return a unique [de.florianwip.ktinventory.util.InventoryHolder]
     */
    fun getNewHolder(): de.florianwip.ktinventory.util.InventoryHolder {
        return InventoryHolder(uuid())
    }

    private fun uuid(): UUID {
        val uuid = UUID.randomUUID()
        if (usedUuids.contains(uuid)) {
            return uuid()
        }  else {
            usedUuids.add(uuid)
            return uuid
        }
    }
}