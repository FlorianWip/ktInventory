package de.florianwip.ktinventory.inventory.list

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

data class InventoryTexts(
    var close: Component = MiniMessage.miniMessage().deserialize("<red><b>Inventar schließen."),
    var nextPage: Component = MiniMessage.miniMessage().deserialize("<yellow><b>Nächste Seite."),
    var prevPage: Component = MiniMessage.miniMessage().deserialize("<yellow><b>Vorherige Seite.")
)