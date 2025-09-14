# ktInventory
This library is a utility for things related the Inventory for PaperSpigot.<br>
It is written in kotlin<br>
**Note:** This library only works in PaperSpigot and is developed and tested for the 1.21.4

## Summary
- designed for PaperSpigot 1.21.7
- designed with and for kotlin
- provides an extensive ItemBuilder
- provides a UI Lib with Inventories to list Collections and basic UIs
- better handling of MHF-Skulls

## Contents
1. [Summary](#summary)
2. [Gradle and Maven](#gradlemaven)
3. [UI Lib](#ui-lib)
4. [ItemBuilder](#itembuilder)
5. [MHF-Skulls](#mhf-heads)
## Gradle/Maven
The current version will be found in the Releases Section of Github
### Maven
Add this to your `<repositories>` and to your `<dependencies>` in your `pom.xml`
```xml
<repository>
    <id>flammenfuchs-public</id>
    <url>https://repo.flammenfuchs.de/public</url>
</repository>
```
```xml
<dependency>
    <groupId>de.florianwip</groupId>
    <artifactId>ktInventory</artifactId>
    <version>$VERSION</version>
</dependency>
```
### Gradle
Add this repository and this dependency to your `build.gradle.kt`
```kotlin
reposities {
    maven("https://repo.flammenfuchs.de/public")
}
```
```kotlin
dependencies {
    implementation("de.florianwip:ktInventory:$VERSION")
}
```
## UI Lib
### Before any Usages
Inventories need to be registered in a service. You need to initialize an `KtInventorySerivce` first.<br>
There you can set global values for the inventories like a default background or a prefix for the inventory titles.
```kotlin
val service = KtInventoryService(myPlugin) // <- 'myPlugin' is your plugin instance
```
You can set custom deserializers for the ItemBuilder here too.
```kotlin
val miniMessage = MiniMessage.miniMessage()
val service = KtInventoryService(
    miniMessage,
    KtInventorySettingsImpl(miniMessage),
    myPlugin,
    plugin.logger
)
```
`KtInventoryService` is an open class, so you can also extend it to provide your own functionality.
### ListInventory
List inventories allows to list elements represented by items. It can be used to display for example friends, homes
```kotlin
// Here is an example usage of an ListInventory
fun listGui(player: Player) {
    val gui = DefaultListInventory(
        "<red>Plugins...", // <- Inventory title 
        { Bukkit.getPluginManager().plugins.asList() }, // <- Supplier with it as player to provide the elements
        { // <- Convert the plugin to a ListButton
            buildListButton {
                item = MHFSkull.CHEST.buildSkull {
                    name = "<b><yellow>${it.pluginMeta.name}"
                    stringLore = arrayOf(
                        "<aqua>Authors: ${it.pluginMeta.authors.joinToString(", ")}",
                        "<aqua>Version: ${it.pluginMeta.version}"
                    )
                }
                on(ClickType.LEFT) { event, inv, plugin -> // <- add a click action to a left Click
                    event.whoClicked.sendMessage("You clicked on ${it.pluginMeta.name}")
                    true // <- Return true/false if the event should be cancelled
                }
            }
        }
    ).register(service) // <- Register the inventory in the service
    gui.open(player)
}
```
![Example Inventory](https://i.imgur.com/9it6vG9.png)
### ViewInventory
```kotlin
    fun viewGui(player: Player) {
        InventoryRegistry.defaultBackground = DefaultBackground() // <- You only need to set it one time e.g in onEnable
        val gui = ViewInventory(3, "<red><b>Menu") // <- Rows, Title of the Inventory
        gui.setButton(11, buildDummyButton( // <- DummyButton = Button which does nothing
            buildItem {
                type = Material.COMMAND_BLOCK
                name = "<aqua>Plugins: ${Bukkit.getPluginManager().plugins.size}"
            }
        ))
        gui.setButton(13, buildButton {
            item = buildItem {
                type = Material.BARRIER
                name = "<red>Stop the server"
            }
            on(ClickType.LEFT) {event, inv ->
                Bukkit.shutdown()
                true
            }
        })
        gui.setButton(15, buildDummyButton(
            buildItem {
                type = Material.NAME_TAG
                name = "<aqua>Players Online: ${Bukkit.getOnlinePlayers().size}"
            }
        )).register(service) // <- Register the inventory in the service
        gui.open(player)
    }
```
![Example Inventory](https://i.imgur.com/se5VCFy.png)
## ItemBuilder
It provides an itemBuilder which is easy to use
```kotlin
val item = buildItem {
    type = Material.STONE
    name = "<b>Just a stony cold stone."
}
```
Supported things:
- amount
- displayName
- skulls by SkullOwner (String) by Base64 Value, by PlayerProfile, by OfflinePlayer
- lore
- Enchantment
- ItemFlag
- Color (Leather Armor)
- Copy from existing ItemStacks

The default handling of miniMessage let displayNames and lores appear italic and the lore additional purple.<br>
This item builder is suppressing it by default<br>
You can change this behaviour in the settings of the `KtInventoryService` or with `buildItem(miniMessage, disableFormat) {...}`<br>
To access an ItemBuilder linked to a `KtInventoryService` you can use `service.itemBuilder {...}`<br>

## MHF-Heads
This lib provides (account-)name, (account-)uuid and the base64 value of common MHF-Heads
```kotlin
//example
val item = MHFSkull.ARROW_RIGHT.buildSkull {
    displayName = "<red>Next Page"
}
// if you want to provide a service to pull settings (see ItemBuilder)
val item = MHFSkull.ARROW_RIGHT.buildSkull(service) {
    displayName = "<red>Next Page"
}
val uuid = UUID.fromString(MHFSkull.ARROW_RIGHT.uuid)
val name = MHFSkull.ARROW_RIGHT.mhfName
val texture = MHFSkull.ARROW_RIGHT.base64
```