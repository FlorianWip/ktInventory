package de.florianwip.ktinventory.inventory.list

import de.florianwip.ktinventory.button.list.ListButton
import de.florianwip.ktinventory.button.view.Button
import de.florianwip.ktinventory.button.view.buildButton
import de.florianwip.ktinventory.button.view.buildDummyButton
import de.florianwip.ktinventory.item.MHFSkull
import de.florianwip.ktinventory.service.KtInventoryService
import de.florianwip.ktinventory.service.settings.KtInventorySettingsImpl
import de.florianwip.ktinventory.util.playSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

open class DefaultListInventory<T : Any>(
    name: (player: Player) -> Component,
    val entrySupplier: (player: Player) -> List<T>,
    val converter: (t: T) -> ListButton<T, DefaultListInventory<T>>,
    service: KtInventoryService? = null,
    rows: Int = 6,
): ListInventory<T, DefaultListInventory<T>>(name, rows) {

    constructor(
        name: String,
        entrySupplier: (player: Player) -> List<T>,
        converter: (t: T) -> ListButton<T, DefaultListInventory<T>>,
        service: KtInventoryService? = null,
        rows: Int = 6,
    ): this( { MiniMessage.miniMessage().deserialize(name) }, entrySupplier, converter, service, rows)

    init {
        if (service != null) {
            register(service)
        }
    }

    override val base: DefaultListInventory<T> = this

    override fun entries(player: Player): List<T> {
        return entrySupplier(player)
    }

    override fun convert(t: T): ListButton<T, DefaultListInventory<T>> {
        return converter(t)
    }

    override fun border(): Array<Button<DefaultListInventory<T>>?> = buildBorder(rows) {
        val glass = buildDummyButton<DefaultListInventory<T>> (
            _service!!.itemBuilder {
                type = Material.BLACK_STAINED_GLASS_PANE
                this.name = "<red>"
            }
        )
        set(inventorySize - 9, glass)
        set(inventorySize - 8, glass)
        set(inventorySize - 7, buildButton {
            item = MHFSkull.ARROW_LEFT.buildSkull(_service) {
                name = _service?.settings?.listInventoryPrevPageText ?: KtInventorySettingsImpl.FALLBACK_SETTINGS.listInventoryPrevPageText
            }
            on(ClickType.LEFT) { event, base ->
                val player = event.whoClicked as Player
                val page = base.getCurrentPage(player)
                if (page < 1) {
                    player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f)
                } else {
                    val sound = _service?.settings?.listInventoryPageChangeSound
                    if (sound != null) {
                        player.playSound(sound)
                    }
                    base.previousPage(player)
                }
                true
            }
        })
        set(inventorySize - 6, glass)
        set(inventorySize - 5, buildButton {
            item = _service!!.itemBuilder {
                type = Material.BARRIER
                name =  _service?.settings?.listInventoryCloseText ?: KtInventorySettingsImpl.FALLBACK_SETTINGS.listInventoryCloseText
            }
            on(ClickType.LEFT) { event, base ->
                val player = event.whoClicked as Player
                base.close(player)
                true
            }
        })
        set(inventorySize - 4, glass)
        set(inventorySize - 3, buildButton {
            item = MHFSkull.ARROW_RIGHT.buildSkull {
                name = _service?.settings?.listInventoryNextPageText ?: KtInventorySettingsImpl.FALLBACK_SETTINGS.listInventoryNextPageText
            }
            on(ClickType.LEFT) { event, base ->
                val player = event.whoClicked as Player
                val page = base.getCurrentPage(player)
                if (page != base.cached[player.uniqueId]?.maxPage) {
                    base.nextPage(player)
                    val sound = _service?.settings?.listInventoryPageChangeSound
                    if (sound != null) {
                        player.playSound(sound)
                    }
                } else {
                    player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f)
                }
                true
            }
        })
        set(inventorySize - 2, glass)
        set(inventorySize - 1, glass)
    }

}