package com.github.r0306.AntiRelog.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.r0306.AntiRelog.Storage.DataBase;
import com.github.r0306.AntiRelog.Util.Clock;
import com.github.r0306.AntiRelog.Util.Colors;
import com.github.r0306.AntiRelog.Util.Configuration;
import com.github.r0306.AntiRelog.Util.Util;

public class DamageListener implements Listener, Colors
{

	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event)
	{
		Entity entity = event.getEntity();
		Entity attacker = event.getDamager();
		
		if (Configuration.getExcludedWorlds().contains(entity.getWorld().getName()) 
		|| Configuration.getExcludedWorlds().contains(attacker.getWorld().getName()))
		{
			return;
		}
		
		if (event.getCause() != DamageCause.CUSTOM && event.getDamage() >= 0)
		{
			
			Player player1 = entity instanceof Player ? (Player) entity : null;
			Player player2 = attacker instanceof Player ? (Player) attacker: null;
			
			if (player1 != null && player2 != null)
			{
				
				tagPlayer(player1);
				tagPlayer(player2);
				
				DataBase.setLastDamager(player1, player2);
				
			}
			
			else if (player1 != null && attacker instanceof Projectile)
			{
				
				tagPlayerProjectile(player1, (Projectile) attacker);
				
			}
			else if (player1 == null ^ player2 == null)
			{
				
				tagPlayerMob(getPlayer(player1, player2), getEntity(entity, attacker));
				
			}
			
		}
		
	}
	
	public void tagPlayer(Player player)
	{
		
		if (!Util.canBypass(player))
		{
		
			if (!DataBase.isInCombat(player))
			{
				
				if (Configuration.tagMessageEnabled())
				{
				
					player.sendMessage(name + Configuration.getTagMessage());
				
				}
					
			}
			
			long end = Clock.getEnd();
			
			DataBase.addInCombat(player, end);
			
			Clock.scheduleDelayedMessage(player);
			
		}
		
	}
	
	public void tagPlayerProjectile(Player player, Projectile projectile)
	{
		
		if (projectile.getShooter() instanceof Player)
		{
			
			Player attacker = (Player) projectile.getShooter();
			
			tagPlayer(player);
			tagPlayer(attacker);
			
			DataBase.setLastDamager(player, attacker);
			
		}
		
		else if (Configuration.mobLoggerEnabled())
		{
		
			if (Util.getHostileMobs().contains(projectile.getShooter().getType()))
			{
			
				tagPlayer(player);
				
				DataBase.setLastDamager(player, projectile.getShooter());
				
			}
			
			else if (Configuration.passiveLoggerEnabled())
			{
				
				if (Util.getPassiveMobs().contains(projectile.getShooter().getType()))
				{
					
					tagPlayer(player);
					
					DataBase.setLastDamager(player, projectile.getShooter());
					
				}
				
			}
				
		}
		
	}
	
	public Player getPlayer(Player player1, Player player2)
	{
		
		return player1 != null ? player1 : player2;
		
	}
	
	public Entity getEntity(Entity entity1, Entity entity2)
	{
		
		return entity1 instanceof Player ? entity2 : entity1;
		
	}
	
	public void tagPlayerMob(Player player, Entity entity)
	{
		
		if (Configuration.mobLoggerEnabled())
		{

			if (Util.getHostileMobs().contains(entity.getType()))
			{
				
				tagPlayer(player);
				
			}
			
			else if (Configuration.passiveLoggerEnabled())
			{
				
				if (Util.getPassiveMobs().contains(entity.getType()))
				{
					
					tagPlayer(player);
					
				}
				
			}
			
		}
		
	}
	
}
