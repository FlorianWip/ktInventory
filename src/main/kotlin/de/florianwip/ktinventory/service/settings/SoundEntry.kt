package de.florianwip.ktinventory.service.settings

import org.bukkit.Sound

/**
 * Data Class to hold information for a sound to play
 *
 * @property sound the [Sound] to play
 * @property pitch the pitch which it will be played
 * @property volume the volume which it will be played
 */
data class SoundEntry(
    val sound: Sound,
    val pitch: Float = 1f,
    val volume: Float = 1f,
)