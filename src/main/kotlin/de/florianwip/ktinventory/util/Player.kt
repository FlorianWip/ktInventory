package de.florianwip.ktinventory.util

import de.florianwip.ktinventory.service.settings.SoundEntry
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

fun Player.playSound(location: Location, sound: SoundEntry) {
    playSound(location, sound.sound, sound.volume, sound.pitch)
}

fun Player.playSound(sound: SoundEntry) {
    playSound(location, sound)
}

fun HumanEntity.playSound(location: Location, sound: SoundEntry) {
    if (this is Player) {
        playSound(location, sound.sound, sound.volume, sound.pitch)
    } else {
        Bukkit.getLogger().warning("Can't play sound for HumanEntity that is not a Player!" +
                " (uuid=${this.uniqueId}) (s=${sound.sound} v=${sound.volume} p=${sound.pitch})")
        Thread.dumpStack()
    }
}

fun HumanEntity.playSound(sound: SoundEntry) {
    playSound(location, sound)
}
