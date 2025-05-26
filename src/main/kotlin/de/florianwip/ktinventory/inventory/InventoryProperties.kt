package de.florianwip.ktinventory.inventory

import org.bukkit.Sound

/**
 * Data class to define general properties of a [InventoryBase]
 *
 * @property openSound
 * @property closeSound
 */
data class InventoryProperties(
    /**
     * The sound played on open
     */
    val openSound: Sound? = null,
    /**
     * The sound played on close
     */
    val closeSound: Sound? = null
)