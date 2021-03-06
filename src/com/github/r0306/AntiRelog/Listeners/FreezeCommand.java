package com.github.r0306.AntiRelog.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.github.r0306.AntiRelog.CombatTracker;
import com.github.r0306.AntiRelog.Util.Clock;
import com.github.r0306.AntiRelog.Util.Colors;
import com.github.r0306.AntiRelog.Util.Configuration;

public class FreezeCommand implements Listener, Colors {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void freezeCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (CombatTracker.isInCombat(player)) {
			if (!Clock.isEnded(CombatTracker.getEndingTime(player))) {
				String command = event.getMessage();
				if (command.startsWith("/ar") || command.startsWith("/antirelog") || command.startsWith("/arl")) {
					return;
				}
				for (String s : Configuration.getWhiteList()) {
					if (("/" + s).toLowerCase().contains(command.toLowerCase())) {
						return;
					}
				}
				cancelEvent(event);
			}
		}
	}

	public void cancelEvent(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (Configuration.messagesEnabled()) {
			player.sendMessage(name + Configuration.getFreezeMessage());
		}
		event.setCancelled(true);
	}

}
