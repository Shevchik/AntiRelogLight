package com.github.r0306.AntiRelog.Listeners;

import java.io.IOException;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.github.r0306.AntiRelog.CombatTracker;
import com.github.r0306.AntiRelog.Util.Clock;
import com.github.r0306.AntiRelog.Util.Colors;
import com.github.r0306.AntiRelog.Util.Configuration;

public class LogPrevention implements Listener, Colors {

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onQuit(PlayerQuitEvent event) throws IOException {
		Player player = event.getPlayer();
		if (CombatTracker.isInCombat(player)) {
			long end = CombatTracker.getEndingTime(player);
			if (!Clock.isEnded(end)) {
				if (player.getGameMode() != GameMode.CREATIVE) {
					if (Configuration.dropItemsEnabled()) {
						dropItems(player);
					}
					if (Configuration.dropExpEnabled()) {
						dropExp(player);
					}
				}
			}
		}
		CombatTracker.removeFromCombat(player);
	}

	public static void dropItems(HumanEntity player) {
		for (ItemStack i : player.getInventory().getContents()) {
			if (i != null) {
				player.getWorld().dropItemNaturally(player.getLocation(), i);
			}
		}
		player.getInventory().clear();
	}

	public static void dropExp(Player player) {
		int ExpTotal = player.getTotalExperience();
		World world = player.getWorld();
		for (int i = 0; i < 6; i++) {
			world.spawn(player.getLocation(), ExperienceOrb.class).setExperience(ExpTotal);
		}
		player.setLevel(0);
		player.setExp(0);
	}

}
