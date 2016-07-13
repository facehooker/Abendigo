package org.abendigo.plugin.csgo

import org.abendigo.DEBUG
import org.abendigo.csgo.*
import org.abendigo.csgo.Client.clientDLL
import org.abendigo.csgo.Engine.clientState
import org.abendigo.csgo.offsets.*
import org.jire.kotmem.Keys
import java.awt.event.KeyEvent

// Huge credits and thanks to "double v"'s skin changer source!
// Find skin IDs here: http://www.unknowncheats.me/forum/counterstrike-global-offensive/148322-skin-ids.html

object SkinChangerPlugin : InGamePlugin("Skin Changer", duration = 4) {

	private const val APPLY_KEY = KeyEvent.VK_F1

	private const val DEFAULT_SKIN_SEED = 0
	private const val DEFAULT_STATTRACK = -1 // -1 for no StatTrak, 0+ for StatTrak amount
	private const val DEFAULT_WEAR = 0.0001F // lower = less wear, higher = more wear
	private const val DEFAULT_QUALITY = 1

	// state so that Weapons.invoke can infer
	private var weaponAddress = 0
	private lateinit var weapon: Weapons

	private fun skins() {
		Weapons.AK47(44)
		Weapons.DESERT_EAGLE(37)
		Weapons.AWP(344)
		Weapons.M4A1_SILENCER(430)
		Weapons.M4A4(255)
		Weapons.FIVE_SEVEN(44)
		Weapons.GLOCK(38)
		Weapons.GALIL(379)
		Weapons.FAMAS(426)
		Weapons.P2000(279)
		Weapons.SSG08(222)
	}

	override fun cycle() {
		for (i in 1..3) try {
			var currentWeaponIndex: Int = csgo[Me().address + m_hMyWeapon + ((i - 1) * 0x4)]
			currentWeaponIndex = currentWeaponIndex and 0xFFF
			weaponAddress = clientDLL[m_dwEntityList + (currentWeaponIndex - 1) * 0x10]
			val weaponID: Int = csgo[weaponAddress + m_iItemDefinitionIndex]
			val xuid: Int = csgo[weaponAddress + m_OriginalOwnerXuidLow]

			// patch to make the skins stay
			csgo[weaponAddress + m_iItemIDLow] = 2048
			csgo[weaponAddress + m_iItemIDHigh] = 0
			csgo[weaponAddress + m_iAccountID] = xuid

			weapon = Weapons.byID(weaponID)!!

			skins() // then apply custom to override default
		} catch (t: Throwable) {
			if (DEBUG) t.printStackTrace()
		}

		if (Keys[APPLY_KEY]) engineDLL[clientState(1024).address + m_dwForceFullUpdate] = -1
	}

	private fun skin(skinID: Int, skinSeed: Int, statTrack: Int, wear: Float, quality: Int) {
		csgo[weaponAddress + m_nFallbackPaintKit] = skinID
		csgo[weaponAddress + m_nFallbackSeed] = skinSeed
		csgo[weaponAddress + m_nFallbackStatTrak] = statTrack
		csgo[weaponAddress + m_iEntityQuality] = quality
		csgo[weaponAddress + m_flFallbackWear] = wear
	}

	private operator fun Weapons.invoke(skinID: Int, skinSeed: Int = DEFAULT_SKIN_SEED,
	                                    statTrack: Int = DEFAULT_STATTRACK, wear: Float = DEFAULT_WEAR,
	                                    quality: Int = DEFAULT_QUALITY) {
		if (this == weapon) skin(skinID, skinSeed, statTrack, wear, quality)
	}

}