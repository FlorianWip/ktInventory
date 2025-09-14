package de.florianwip.ktinventory.service.settings

import de.florianwip.ktinventory.inventory.view.background.InventoryBackground

/**
 * Interface to provide settings for KtInventory
 */
interface KtInventorySettings {

    /**
     * Sound played when an [de.florianwip.ktinventory.inventory.InventoryBase] is opened
     */
    val inventoryOpenSound: SoundEntry?

    /**
     * Sound played when an [de.florianwip.ktinventory.inventory.InventoryBase] is closed
     */
    val inventoryCloseSound: SoundEntry?

    /**
     * Default background used in a [de.florianwip.ktinventory.inventory.view.ViewInventory] if no custom background is provided
     */
    val viewInventoryBackground: InventoryBackground?

    /**
     * Sound played when a player switches a page in a [de.florianwip.ktinventory.inventory.list.ListInventory]
     */
    val listInventoryPageChangeSound: SoundEntry?

    /**
     * Component displayed for the "Close" item in a [de.florianwip.ktinventory.inventory.list.ListInventory]
     */
    val listInventoryCloseText: String

    /**
     * Component displayed for the "Next Page" item in a [de.florianwip.ktinventory.inventory.list.ListInventory]
     */
    val listInventoryNextPageText: String

    /**
     * Component displayed for the "Previous Page" item in a [de.florianwip.ktinventory.inventory.list.ListInventory]
     */
    val listInventoryPrevPageText: String

    /**
     * Per default custom displayName are italic and lore is purple and italic. This setting disables this behavior in the [de.florianwip.ktinventory.item.ItemBuilder]
     */
    val itemDisableDefaultFormat: Boolean
}