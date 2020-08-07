package com.tgn11.pirateinvasion.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import com.tgn11.pirateinvasion.Main;

public class WorldListener implements Listener
{
	
	Main plugin;

	public WorldListener(com.tgn11.pirateinvasion.Main plugin)
	{
		super();
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBoatBreak(VehicleDestroyEvent e)
	{
		Entity en1 = e.getVehicle();
		Entity en2 = en1.getPassenger();
		Entity en3 = e.getAttacker();
		if(en1 instanceof Boat)
		{
			if(en2 instanceof Player)
			{
				Player attacked = (Player)en2;
				if(en3 instanceof Arrow)
				{
					Entity en4 = ((Arrow) en3).getShooter();
					if(en4 instanceof Player)
					{
						Player attacker = (Player)en4;
						if(plugin.isOnSameTeam(attacker, attacked))
						{
							attacker.sendMessage(ChatColor.RED + "Team-Killing Is Now Allowed!");
						}
						else
						{
							Player p = (Player)en2;
							double damage = 4;
							if(p.getInventory().getChestplate() != null)
							{
								damage = damage - 1;
							}
							if(p.getInventory().getLeggings() != null)
							{
								damage = damage - 1;
							}
							if(p.getInventory().getBoots() != null)
							{
								damage = damage - 1;
							}
							p.damage(damage);
						}
					}
					
				}
				if(en3 instanceof Player)
				{
					Player attacker = (Player)en3;
					if(plugin.isOnSameTeam(attacker, attacked))
					{
						en3.sendMessage(ChatColor.RED + "Team-Killing Is Now Allowed!");
					}
					else
					{
						Player p = (Player)en2;
						double damage = 4;
						if(p.getInventory().getChestplate() != null)
						{
							damage = damage - 1;
						}
						if(p.getInventory().getLeggings() != null)
						{
							damage = damage - 1;
						}
						if(p.getInventory().getBoots() != null)
						{
							damage = damage - 1;
						}
						p.damage(damage);
					}
				}
			}
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Player p = e.getPlayer();
		if(p.isOp() == false || p.getGameMode() != GameMode.CREATIVE)
		{
			p.sendMessage(ChatColor.RED + "Block-Breaking Is Not Allowed!");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void doBlockPhysics(BlockPhysicsEvent e)
	{
		if(e.getBlock().getType() == Material.LADDER)
		{
			e.setCancelled(true);
		}
	}

	/*@EventHandler
	public void onBoatMove(VehicleMoveEvent e)
	{
		Vehicle v = e.getVehicle();
		if(v instanceof Boat)
		{
			if(v.getPassenger() == null)
			{
				v.setVelocity(new Vector(0, 0, 0));
			}
		}
	}*/
}
