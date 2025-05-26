package de.florianwip.ktinventory.item

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.profile.PlayerTextures
import java.util.UUID

/**
 * Build an [ItemStack]
 *
 * @param block the [ItemBuilder]
 * @return a new [ItemStack] with the defined values
 */
fun buildItem(block: ItemBuilder.() -> Unit): ItemStack {
    return ItemBuilder().apply(block).build()
}

/**
 * Build an [ItemStack]
 */
class ItemBuilder {

    /**
     * Set the [Material] with default [Material.AIR]
     */
    var type: Material = Material.AIR

    /**
     * Set the amount with default of 1
     */
    var amount: Int = 1

    /**
     * Set the displayName as [String]. It will be serialized as [MiniMessage]
     */
    var name: String? = null

    /**
     * Set the skullOwner by [String]
     *
     * Note: This overwrites [type]
     */
    @Deprecated(message = "meta.owner is deprecated in Bukkit")
    var skullName: String? = null

    /**
     * Set the skullOwner by [OfflinePlayer]
     *
     * Note: This overwrites [type]
     */
    var skullOwner: OfflinePlayer? = null

    /**
     * Set the skullOwner by [PlayerProfile]
     *
     * Note: This overwrites [type]
     */
    var skullProfile: PlayerProfile? = null

    /**
     * Set the skullTexture to the given Base64 Value
     */
    var skullBase64: String? = null

    /**
     * Set the displayName as [Component]
     */
    var displayName: Component? = null

    /**
     * Set the lore as [Array] of [String] which will be serialized as [MiniMessage]
     */
    var stringLore: Array<String>? = null

    /**
     * Set the lore as [Array] of [Component]
     */
    var lore: Array<Component>? = null

    /**
     * Set [Enchantment] as [Array] of [Pair] of [Enchantment] and its level
     *
     * Note: The level can exceed Minecraft Limits
     */
    var enchantments: Array<Pair<Enchantment, Int>>? = null

    /**
     * Set the [Color] of Leather Armor
     *
     * Note: This has only effect on items with [LeatherArmorMeta]
     * @see [LeatherArmorMeta]
     */
    var color: Color? = null

    /**
     * Set the flags of an item as [Array] of [ItemFlag]
     */
    var flags: Array<ItemFlag>? = null

    /**
     * ItemStack will be copied and the changes will be applied on the Copy
     *
     * Note: This overwrites [type]
     * @see [java.lang.Cloneable]
     */
    var copyFrom: ItemStack? = null

    private fun isHead(): Boolean {
        return skullProfile != null || skullName != null || skullOwner != null || skullBase64 != null
    }

    /**
     * Build an [ItemStack] with given properties
     *
     * @return A new [ItemStack]
     */
    fun build(): ItemStack {
        val mini = MiniMessage.miniMessage()
        val type = if (isHead()) Material.PLAYER_HEAD else this.type
        val item = copyFrom?.clone() ?: ItemStack(type)
        item.amount = amount

        val meta = item.itemMeta
        if (meta != null) {
            if (displayName != null) {
                meta.displayName(displayName)
            } else if (name != null) {
                meta.displayName(mini.deserialize(name!!))
            }
            if (lore != null) {
                meta.lore(lore!!.asList())
            } else if (stringLore != null) {
                meta.lore(stringLore!!.map { mini.deserialize(it) })
            }
            if (enchantments != null) {
                enchantments!!.forEach {
                    meta.addEnchant(it.component1(), it.component2(), true)
                }
            }
            if (color != null && meta is LeatherArmorMeta) {
                meta.setColor(color)
            }
            if (isHead() && meta is SkullMeta) {
                if (skullBase64 != null) {
                    val profile = Bukkit.createProfile(UUID.randomUUID())
                    profile.setProperty(ProfileProperty("textures", skullBase64!!))
                    meta.playerProfile = profile

                } else if (skullProfile != null) {
                    meta.playerProfile = skullProfile
                } else if (skullOwner != null) {
                    meta.owningPlayer = skullOwner
                } else {
                    meta.owner = skullName
                }
            }
            if (flags != null) {
                meta.addItemFlags(*flags!!)
            }
            item.itemMeta = meta
        }
        return item
    }
}