package de.florianwip.ktinventory.inventory

import com.google.common.base.Preconditions
import de.florianwip.ktinventory.inventory.view.background.InventoryBackground
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.plugin.java.JavaPlugin

/**
 * The [InventoryRegistry] which handles the global things of the [InventoryBase]
 */
object InventoryRegistry {

    private val bases = mutableListOf<InventoryBase<*>>()

    /**
     * Set the default [InventoryProperties]
     * @see [InventoryProperties]
     */
    var defaultProperties = InventoryProperties()

    /**
     * Set the default [InventoryBackground]
     * @see [InventoryBackground]
     */
    var defaultBackground: InventoryBackground? = null

    /**
     * Set the plugin needed to register the listener and provide a logger
     *
     * **Note: This need to be set to use any [InventoryBase]**
     */
    var plugin: JavaPlugin? = null
        set(plugin) {
            if (field == null && plugin != null) {
                Bukkit.getPluginManager().registerEvents(InventoryListener(), plugin)
            }
            field = plugin
        }

    /**
     * Bind a [InventoryBase] to the registry
     *
     * @param base
     */
    fun bind(base: InventoryBase<*>) {
        Preconditions.checkState(plugin != null, "A plugin hasn't been set yet.")
        bases.add(base)
    }

    private class InventoryListener: Listener {

        @EventHandler
        fun onEvent(event: InventoryClickEvent) {
            bases.forEach { it.handleClick(event) }
        }

        @EventHandler
        fun onEvent(event: InventoryCloseEvent) {
            bases.forEach { it.handleClose(event) }
        }

        @EventHandler
        fun onEvent(event: InventoryOpenEvent) {
            bases.forEach { it.handleOpen(event) }
        }
    }

}