package org.abendigo.plugin.csgo

import org.abendigo.*
import org.abendigo.csgo.*
import org.abendigo.plugin.Plugin
import java.awt.event.KeyEvent

class BunnyHopPlugin : Plugin("Bunny Hop", description = "Jumps the player around", author = "Jire") {

	override fun enable() {
		every(8) {
			if (keys[KeyEvent.VK_SPACE] && +me.flags % 2 == 1) {
				client[m_dwForceJump] = 5
				sleep(32)
				client[m_dwForceJump] = 4
				sleep(32 - 8)
			}
		}
	}

}
