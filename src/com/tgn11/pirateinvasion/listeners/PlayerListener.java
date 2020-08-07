package com.tgn11.pirateinvasion.listeners;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.tgn11.pirateinvasion.extra.ParticleEffect;

public class PlayerListener implements Listener
{

	com.tgn11.pirateinvasion.Main plugin;

	Random random = new Random();

	public PlayerListener(com.tgn11.pirateinvasion.Main plugin)
	{
		super();
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();
		/*
		Location locboat = player.getLocation();
		if(locboat.getX()>-530 && locboat.getZ()>470
		&& locboat.getX()<-400 && locboat.getZ()<630)
		{}else
		if(isOutOfBounds(-530, 470, -400, 630, locboat))
		{
			if(random.nextInt(80) > 65)
			{
			//player.sendMessage(ChatColor.RED + "You're outside the map bounds! Go back.");
			}

			if(random.nextInt(25) == random.nextInt(25))
			{
			//player.damage(1.0);
			}
		}
		 */
		if(player.getGameMode() == GameMode.SURVIVAL)
		{
			if(player.getLocation().getBlock().isLiquid())
			{
				if(random.nextInt(100) > 75)
				{
					if (!player.isInsideVehicle())
					{
						if(random.nextInt(30) == random.nextInt(30))
						{

							{
								if(random.nextInt(20) > 10)
								{
									player.sendMessage(ChatColor.AQUA + "The water is freezing cold!");
								}
								player.damage(1.0);
								player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 150, 1));
							}
							if(random.nextInt(5) == random.nextInt(5))
							{
								if(random.nextInt(20) > 10)
								{
									player.sendMessage(ChatColor.BLUE + "*A shark attacks you*");
									player.damage(5.0);
									player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
								}
							}
						}
					}
				}
			}else{

				if(random.nextInt(100) > 65)
				{
					if(random.nextInt(25) == random.nextInt(25))
					{
						Damageable dam = (Damageable)player;
						double h = dam.getHealth();
						double val = h+1.0;
						if(val <= 20.0)
						{
							player.setHealth(val);
						}
					}
				}

			}
		}
	}

	@EventHandler
	public void regenHealth(EntityRegainHealthEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		if(plugin.matchInProgress)
		{
			Player p = e.getEntity();

			e.setDroppedExp(0);

			Player killer = e.getEntity().getKiller();
			if(killer instanceof Player)
			{
				String n = killer.getName();
				if(plugin.kills.containsKey(n))
				{
					Integer kills = plugin.kills.get(n)+1;
					plugin.kills.put(n, kills);
				}else{
					plugin.kills.put(n, 1);
				}
				if(plugin.gold.containsKey(n))
				{
					plugin.gold.put(n, plugin.gold.get(n) + 1);
				}
				else
				{
					plugin.gold.put(n, 1);
				}
				killer.giveExpLevels(1);
			}

			boolean checkAgain = checkTeamsEmpty();

			plugin.removePlayerFromTeam(p);

			if(checkAgain)
			{
				checkTeamsEmpty();
			}

			Location loc = p.getLocation();
			loc.setY(p.getLocation().getY()+.5);
		}
	}

	public boolean checkTeamsEmpty()
	{
		if(plugin.matchInProgress)
		{
			if(plugin.team0.size() < 1) //Ship Crew
			{
				plugin.prepareRestart(1,plugin.team1,"",20); //Pirates Win
				return false;
			}
			if(plugin.team1.size() < 1) //Pirates
			{
				plugin.prepareRestart(0,plugin.team0,"",20); //Ship Crew Win
				return false;
			}
			return true;
		}
		return false;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if(plugin.matchInProgress || plugin.isRestarting == false)
		{
			Player p = e.getPlayer();
			checkTeamsEmpty();
			plugin.removePlayerFromTeam(p);
			p.setGameMode(GameMode.SPECTATOR);
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		if(plugin.matchInProgress && plugin.isOnTeam(p))
		{
			plugin.resetPlayer(p);
		}else{
			if(plugin.matchInProgress || plugin.isOnTeam(p) == false)
			{
				p.setGameMode(GameMode.SPECTATOR);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		p.setGameMode(GameMode.SPECTATOR);
		checkTeamsEmpty();
		if(plugin.matchInProgress || plugin.isRestarting == false)
		{
			plugin.removePlayerFromTeam(p);
				checkTeamsEmpty();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void antiTeamAttack(EntityDamageByEntityEvent e)
	{
		Entity e1 = e.getDamager();
		Entity e2 = e.getEntity();
		if(e1 instanceof Player && e2 instanceof Player)
		{
			Player attacker = (Player)e1;
			Player attacked = (Player)e2;

			if(plugin.isOnSameTeam(attacker, attacked))
			{
				attacker.sendMessage(ChatColor.RED + "Team-Killing Is Now Allowed!");
				e.setCancelled(true);
			}
			else
			{
				Location loc = attacked.getLocation();
				loc.setY(loc.getY() + .5F);
				ParticleEffect.REDSTONE.display(.3F, 1, .3F, 0, 10, loc, 3);
				attacked.getWorld().playSound(loc, Sound.FALL_BIG, 1F, 1F);

			}
		}
		if(e1 instanceof Arrow && e2 instanceof Player)
		{
			Arrow en1 = (Arrow)e1;
			Player attacked = (Player)e2;

			Player attacker = (Player)en1.getShooter();
			if(plugin.isOnSameTeam(attacker, attacked))
			{
				attacker.sendMessage(ChatColor.RED + "Team-Killing Is Now Allowed!");
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if(e.getSlotType() == SlotType.ARMOR && e.getCurrentItem().getType() == Material.WOOL)
		{
			e.setCancelled(true);
		}
	}
}