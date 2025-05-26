package de.florianwip.ktinventory.inventory.list

import de.florianwip.ktinventory.button.list.ListButton
import de.florianwip.ktinventory.button.view.Button
import de.florianwip.ktinventory.button.view.buildButton
import de.florianwip.ktinventory.button.view.buildDummyButton
import de.florianwip.ktinventory.item.MHFSkull
import de.florianwip.ktinventory.item.buildItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class DefaultListInventory<T : Any>(
    name: Component,
    val entrySupplier: (player: Player) -> List<T>,
    val converter: (t: T) -> ListButton<T, DefaultListInventory<T>>,
    val texts: InventoryTexts = InventoryTexts()
): ListInventory<T, DefaultListInventory<T>>(name) {

    constructor(
        name: String,
        entrySupplier: (player: Player) -> List<T>,
        converter: (t: T) -> ListButton<T, DefaultListInventory<T>>,
        texts: InventoryTexts = InventoryTexts()
    ): this(MiniMessage.miniMessage().deserialize(name), entrySupplier, converter, texts)

    override val base: DefaultListInventory<T> = this

    override fun entries(player: Player): List<T> {
        return entrySupplier(player)
    }

    override fun convert(t: T): ListButton<T, DefaultListInventory<T>> {
        return converter(t)
    }

    override val border: Array<Button<DefaultListInventory<T>>?> = buildBorder(6) {
        val glass = buildDummyButton<DefaultListInventory<T>> (
            buildItem {
                type = Material.BLACK_STAINED_GLASS_PANE
                this.name = "<red>"
            }
        )
        set(45, glass)
        set(46, glass)
        set(47, buildButton {
            item = MHFSkull.ARROW_LEFT.buildSkull {
                displayName = texts.prevPage
            }
            on(ClickType.LEFT) { event, base ->
                val player = event.whoClicked as Player
                val page = base.getCurrentPage(player)
                if (page < 1) {
                    player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f)
                } else {
                    player.playSound(player.location, Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f)
                    base.previousPage(player)
                }
                true
            }
        })
        set(48, glass)
        set(49, buildButton {
            item = buildItem {
                type = Material.BARRIER
                displayName = texts.close
            }
            on(ClickType.LEFT) { event, base ->
                val player = event.whoClicked as Player
                base.close(player)
                true
            }
        })
        set(50, glass)
        set(51, buildButton {
            item = MHFSkull.ARROW_RIGHT.buildSkull {
                displayName = texts.nextPage
            }
            on(ClickType.LEFT) { event, base ->
                val player = event.whoClicked as Player
                val page = base.getCurrentPage(player)
                if (page != base.cached[player.uniqueId]?.maxPage) {
                    base.nextPage(player)
                    player.playSound(player.location, Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f)
                } else {
                    player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f)
                }
                true
            }
        })
        set(52, glass)
        set(53, glass)
    }

}