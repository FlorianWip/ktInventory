package de.florianwip.ktinventory.service.settings

import de.florianwip.ktinventory.inventory.view.background.DefaultBackground
import de.florianwip.ktinventory.inventory.view.background.InventoryBackground
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Sound

class KtInventorySettingsImpl(
    miniMessage: MiniMessage = MiniMessage.miniMessage(),
    override val inventoryOpenSound: SoundEntry? = SoundEntry(Sound.BLOCK_CHEST_OPEN),
    override val inventoryCloseSound: SoundEntry? = SoundEntry(Sound.BLOCK_CHEST_CLOSE),
    override val listInventoryPageChangeSound: SoundEntry? = SoundEntry(Sound.ITEM_BOOK_PAGE_TURN),
    override val viewInventoryBackground: InventoryBackground? = DefaultBackground(),
    override val listInventoryCloseText: String = "<red><b>Close Inventory.",
    override val listInventoryNextPageText: String = "<yellow><b>Next Page.",
    override val listInventoryPrevPageText: String = "<yellow><b>Previous Page.",
    override val itemDisableDefaultFormat: Boolean = true
) : KtInventorySettings {

    companion object {

        val FALLBACK_SETTINGS = KtInventorySettingsImpl()
    }
}