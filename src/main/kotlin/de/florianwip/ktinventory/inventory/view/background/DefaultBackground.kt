package de.florianwip.ktinventory.inventory.view.background

import de.florianwip.ktinventory.inventory.InventoryBase
import de.florianwip.ktinventory.button.view.Button
import de.florianwip.ktinventory.button.view.buildDummyButton
import de.florianwip.ktinventory.item.buildItem
import de.florianwip.ktinventory.service.KtInventoryService
import org.bukkit.Material

class DefaultBackground: InventoryBackground {

    private val template1Row = arrayOf("#:::::::#")
    private val template2Row = arrayOf("#:::::::#", "#:::::::#")
    private val template3Row = arrayOf(".#:::::#.", "#:::::::#", ".#:::::#.")
    private val template4Row = arrayOf(".#:::::#.", "#:::::::#", "#:::::::#", ".#:::::#.")
    private val template5Row = arrayOf(".#:::::#.", "#:::::::#", "#:-----:#", "#:::::::#", ".#:::::#.")
    private val template6Row = arrayOf(".#:::::#.", "#:::::::#", "#:-----:#", "#:-----:#", "#:::::::#", ".#:::::#.")

    override fun <T: InventoryBase<T>> getBackground(rows: Int, service: KtInventoryService?): Array<Button<T>?> {
        val templateArray = when (rows) {
            1 -> template1Row
            2 -> template2Row
            3 -> template3Row
            4 -> template4Row
            5 -> template5Row
            6 -> template6Row
            else -> throw IllegalArgumentException("rows must be between 1 and 6")
        }
        val template = templateArray.joinToString("")
        val button = buildDummyButton<T>(buildItem(service) {
            type = Material.STONE_BUTTON
            name = "<red>"
        })
        val whiteGlass = buildDummyButton<T>(buildItem(service) {
            type = Material.WHITE_STAINED_GLASS_PANE
            name = "<red>"
        })
        val grayGlass = buildDummyButton<T>(buildItem(service) {
            type = Material.GRAY_STAINED_GLASS_PANE
            name = "<red>"
        })
        val blackGlass = buildDummyButton<T>(buildItem(service) {
            type = Material.BLACK_STAINED_GLASS_PANE
            name = "<red>"
        })
        return template.toCharArray().map {
            when (it) {
                '#' -> blackGlass
                '.' -> button
                ':' -> grayGlass
                '-' -> whiteGlass
                else -> null
            }
        }.toTypedArray()
    }
}