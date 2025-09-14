package de.florianwip.ktinventory.service

import de.florianwip.ktinventory.inventory.InventoryBase
import de.florianwip.ktinventory.item.ItemBuilder
import de.florianwip.ktinventory.item.buildItem
import de.florianwip.ktinventory.service.settings.KtInventorySettings
import de.florianwip.ktinventory.service.settings.KtInventorySettingsImpl
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

open class KtInventoryService(
    plugin: JavaPlugin,
    val miniMessage: MiniMessage = MiniMessage.miniMessage(),
    val settings: KtInventorySettings = KtInventorySettingsImpl(miniMessage),
    val logger: Logger = plugin.logger
) {

    init {
        plugin.server.pluginManager.registerEvents(InventoryListener(this), plugin)
    }

    private val registeredInventories = mutableListOf<InventoryBase<*>>()

    fun getRegisteredInventories(): List<InventoryBase<*>> = registeredInventories

    fun register(base: InventoryBase<*>) {
        if (base.getService() == null) {
            throw IllegalArgumentException("You need to register a InventoryBase from its instance, see InventoryBase.register()")
        }
        this.registeredInventories.add(base)
    }

    fun itemBuilder(block: ItemBuilder.() -> Unit) = buildItem(this, block)
}

private class InventoryListener(
    private val service: KtInventoryService
): Listener {

    @EventHandler
    fun onEvent(event: InventoryClickEvent) {
        service.getRegisteredInventories().forEach { it.handleClick(event) }
    }

    @EventHandler
    fun onEvent(event: InventoryCloseEvent) {
        service.getRegisteredInventories().forEach { it.handleClose(event) }
    }

    @EventHandler
    fun onEvent(event: InventoryOpenEvent) {
        service.getRegisteredInventories().forEach { it.handleOpen(event) }
    }
}