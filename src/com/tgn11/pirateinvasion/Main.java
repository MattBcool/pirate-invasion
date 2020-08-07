package com.tgn11.pirateinvasion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.server.v1_8_R1.TileEntityChest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.tgn11.pirateinvasion.listeners.PlayerListener;
import com.tgn11.pirateinvasion.listeners.WorldListener;

public class Main extends JavaPlugin
{
	public static Main plugin;

	public boolean isRestarting = false;
	public boolean matchInProgress = false;
	//public ArrayList<String> team0 = new ArrayList<String>();
	//public ArrayList<String> team1 = new ArrayList<String>();
	public ArrayList<Location> boatlc = new ArrayList<Location>();
	public ArrayList<Location> chestlc = new ArrayList<Location>();
	public HashMap<String, Integer> kills = new HashMap<String, Integer>();
	public HashMap<String, Integer> gold = new HashMap<String, Integer>();
	public ArrayList<String> team0 = new ArrayList<String>();
	public ArrayList<String> team1 = new ArrayList<String>();

	Random random = new Random();
	
	/*com.tgn11.pirateinvasion.extra.MyConfig pgold;
	com.tgn11.pirateinvasion.extra.MyConfigManager manager;*/

	public void onEnable()
	{	
		/*if(classes.getList("Mages") == null)
		{
			u.add(name);
			classes.set("Mages", u);
		}
		else
		{
			List<String> mages = (List<String>) classes.getList("Mages");
			mages.add(name);
			classes.set("Mages", mages);
		}
	}
	classes.saveConfig();
		
		manager = new com.tgn11.pirateinvasion.extra.MyConfigManager(this);
		pgold = manager.getNewConfig("Gold.yml");
		
		if(pgold.getList("Gold") == null)
		{
			pgold.set("Gold", "null");
			pgold.saveConfig();
		}
		else
		{
			ArrayList<Object> obj = new ArrayList<Object>();
			obj.addAll(pgold.getList("Gold"));
			for(Object o : obj)
			{
				Bukkit.broadcastMessage(o + "");
				String s = (String)o;
				String[] s1 = s.split(",");
				String s2 = s1[0];
				String s3 = s1[1];
				gold.put(s2, Integer.parseInt(s3));
			}
		}
		
		Bukkit.broadcastMessage(ChatColor.YELLOW + "@@@@@@@@@ " + gold.get("TheGamingNinja11"));
		
		if(pgold.contains("Gold"))
		{
			gold.putAll((Map<? extends String, ? extends Integer>) pgold.get("Gold"));
		}*/
		
		plugin = this;

		getServer().getLogger().info("Pirate Invasion (Gamemode) - Running!");
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new WorldListener(this), this);

		boatlc.add(new Location(getServer().getWorlds().get(0), -853, 105, -850));
		boatlc.add(new Location(getServer().getWorlds().get(0), -853, 105, -860));
		boatlc.add(new Location(getServer().getWorlds().get(0), -853, 105, -870));

		boatlc.add(new Location(getServer().getWorlds().get(0), -920, 105, -850));
		boatlc.add(new Location(getServer().getWorlds().get(0), -920, 105, -860));
		boatlc.add(new Location(getServer().getWorlds().get(0), -920, 105, -870));

		chestlc.add(new Location(getServer().getWorlds().get(0), -893, 112, -862));
		chestlc.add(new Location(getServer().getWorlds().get(0), -887, 110, -865));
		chestlc.add(new Location(getServer().getWorlds().get(0), -883, 108, -863));
		chestlc.add(new Location(getServer().getWorlds().get(0), -890, 107, -851));
	}


	public void onDisable()
	{
		/*for(String s : gold.keySet())
		{
			Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + s);
			pgold.set("Gold", s + "," + gold.get(s));
			pgold.saveConfig();
		}*/
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			Player p = (Player)sender;
			if(p.isOp())
			{
				if(label.equalsIgnoreCase("pr"))
				{
					prepareRestart(0, team0, "", 20);
				}
				if(label.equalsIgnoreCase("ipr"))
				{
					prepareRestart(0, team0, "", 1);
				}
			}
			if(label.equalsIgnoreCase("shop"))
			{
				
			}
		}
		return false;
	}

	public void resetPI()
	{
		resetMap();
		initPI();
	}

	@SuppressWarnings("deprecation")
	public void initPI()
	{

		Integer pc = getServer().getOnlinePlayers().length;
		for(Player p : getServer().getOnlinePlayers())
		{
			if(pc > 0)
			{
				if(team0.size() < team1.size())
				{
					setTeam(p, 1);
				}else{
					setTeam(p, 0);
				}
			}
			resetPlayer(p);
		}
		isRestarting = false;
		matchInProgress = true;
	}

	public String getTeamName(int t)
	{
		if(t == 0)
		{
			return "Ship Crew";	
		}

		if(t == 1)
		{
			return "Pirates";	
		}

		return "error_unnamed_team";
	}

	public void removePlayerFromTeam(Player p)
	{
		String n = p.getName();
		if(team0.contains(n))
		{
			team0.remove(n);
		}
		if(team1.contains(n))
		{
			team1.remove(n);
		}
	}

	/*public int getAliveTeamPlayers(int t)
	{
		if(t == 0)
		{
			return team0.size();
		}
		if(t == 1)
		{
			return team1.size();
		}
		return 0;

	}*/

	@SuppressWarnings("deprecation")
	public void prepareRestart(int winnerteam, ArrayList<String> t, String customMSG, final int timer)
	{	
		isRestarting = true;

		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			public void run() {
				resetPI();
			}
		}, 20L*timer);

		for (Player players : getServer().getOnlinePlayers())
		{

			if(players.getGameMode() != GameMode.SPECTATOR)
			{
				players.setGameMode(GameMode.SPECTATOR);
			}

			if(customMSG.length() > 1)
			{
				players.sendMessage(customMSG);
			}

			players.sendMessage(ChatColor.GREEN+"The " + getTeamName(winnerteam) + " won!");
			players.sendMessage(ChatColor.GOLD+"The surviving " + getTeamName(winnerteam).toLowerCase() + " are:");
			for (String s : t)
			{
				Player p = Bukkit.getPlayer(s);
				String n = p.getName();
				players.sendMessage(ChatColor.AQUA+n + " - Kills: " + kills.get(n));
			}

			kills.put(players.getName(), 0);
		}

		team0.clear();
		team1.clear();
		matchInProgress = false;
		
		playTimer(timer);
	}

	public void resetMap()
	{
		World w = getServer().getWorlds().get(0);
		for(Entity e : w.getEntities())
		{
			if(!(e instanceof Player))
			{
				e.remove();
			}
		}

		for(Location loc : boatlc)
		{
			w.spawnEntity(loc, EntityType.BOAT);
		}

		//Chest Contents
		ItemStack i1 = createCustomItemStack(Material.GOLD_SWORD, ChatColor.GOLD, "King's Sword", "A sword that used to be used from a famous king known to the lands of Equestria!");
		ItemStack i2 = createCustomItemStack(Material.GOLD_NUGGET, ChatColor.GOLD, "Gold", "Gold that was once used to forge some of the strongest swords.");
		//ItemStack i3 = createCustomItemStack(Material.GOLD_HELMET, ChatColor.GOLD, "King's Helmet", "A helmet that used to be used from a famous king known to the lands of Equestria!");
		ItemStack i4 = createCustomItemStack(Material.GOLD_CHESTPLATE, ChatColor.GOLD, "King's Chestplate", "A chestplate that used to be used from a famous king known to the lands of Equestria!");
		ItemStack i5 = createCustomItemStack(Material.GOLD_LEGGINGS, ChatColor.GOLD, "King's Leggings", "Leggings that used to be used from a famous king known to the lands of Equestria!");
		ItemStack i6 = createCustomItemStack(Material.GOLD_BOOTS, ChatColor.GOLD, "King's Boots", "Boots that used to be used from a famous king known to the lands of Equestria!");

		for(Location loc : chestlc)
		{
			Block block = loc.getBlock();
			if(block.getType() == Material.CHEST)
			{
				Chest chest = (Chest)block.getState();
				try
				{
				    java.lang.reflect.Field inventoryField = chest.getClass().getDeclaredField("chest");
				    inventoryField.setAccessible(true);
				    TileEntityChest teChest = ((TileEntityChest) inventoryField.get(chest));
				    teChest.a(ChatColor.YELLOW + "Treasure Chest");
				}
				catch (Exception e)
				{
				     e.printStackTrace();
				}
				Inventory inv = chest.getInventory();
				inv.clear();
			}
			loc.getBlock().setType(Material.CHEST);
			Chest chest = (Chest)block.getState();
			Inventory inv = chest.getInventory();

			int i = random.nextInt(4);
			if(i == 0){inv.addItem(i1, i1, i5, i2, i6);}
			if(i == 1){inv.addItem(i2, i2, i2, i1, i2, i2);}
			if(i == 2){inv.addItem(i6, i2);}
			if(i == 3){inv.addItem(i4, i2, i2, i2);}
			if(i == 4){inv.addItem(i1, i2, i5, i6, i5);}
		}
	}

	public void playTimer(final int i)
	{
		int i1 = 0;
		if(i > 1)
		{
			i1 = i-1;
		}
		final int i2 = i1;
		if(i1 != 0)
		{
			boolean isDivisibleBy10 = i % 10 == 0;
			if(isDivisibleBy10 || i < 10)
			{
				Bukkit.broadcastMessage(ChatColor.GREEN + "Match starting in "+i+" second(s)!");
			}
			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

				public void run() {
					playTimer(i2);
				}
			}, 20L);
		}
		else{Bukkit.broadcastMessage(ChatColor.GOLD + "New Match Starting Now!");}

	}

	public ItemStack createCustomItemStack(Material mat, ChatColor color, String disname, String lore)
	{
		ItemStack item = new ItemStack(mat);
		ItemMeta item_meta = item.getItemMeta();
		item_meta.setDisplayName(color + disname);
		ArrayList<String> item_meta_lore = new ArrayList<String>();
		item_meta_lore.add(ChatColor.AQUA + lore);
		item_meta.setLore(item_meta_lore);
		item.setItemMeta(item_meta);

		return item;
	}

	public boolean isOnSameTeam(Player attacker, Player attacked)
	{
		String n1 = attacker.getName();
		String n2 = attacked.getName();
		if(team0.contains(n1) && team0.contains(n2)
				|| team1.contains(n1) && team1.contains(n2))
		{
			return true;
		}
		return false;
	}

	public int getTeam(Player p)
	{
		String n = p.getName();
		if(team0.contains(n))
		{
			return 0;
		}else{
			if(team1.contains(n))
			{
				return 1;
			}
		}
		return 2;
	}

	public boolean isOnTeam(Player p)
	{
		String n = p.getName();
		if(team0.contains(n) || team1.contains(n))
		{
			return true;
		}
		return false;
	}

	public void resetPlayer(Player p)
	{
		if(p.getGameMode() != GameMode.SURVIVAL)
		{
			p.setGameMode(GameMode.SURVIVAL);
		}

		p.setHealth(20D);
		p.setFoodLevel(20);
		p.setSaturation(20);

		Inventory i = p.getInventory();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		i.clear();
		i.addItem(createCustomItemStack(Material.WOOD_SWORD, ChatColor.DARK_GREEN, "Basic Sword", "A basic sword used by Ship Crew and Pirates."));
		ItemStack it = createCustomItemStack(Material.BOW, ChatColor.DARK_GREEN, "Basic Bow", "A basic bow used by Ship Crew and Pirates.");
		it.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		i.addItem(it);
		i.addItem(new ItemStack(Material.ARROW));
		if(getTeam(p) == 1)
		{
			p.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (short) 14));
			Location loc = new Location(p.getWorld(), -836.5, 109, -884.5, 0, 0);
			p.teleport(loc);
		}
		else if(getTeam(p) == 0)
		{
			p.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (short) 11));
			Location loc = new Location(p.getWorld(), -935.5, 110, -833.5, 180, 0);
			p.teleport(loc);
		}
	}
	
	public void setTeam(Player p, int i)
	{
		String n = p.getName();
		if(i == 0)
		{
			team1.add(n);
			Bukkit.broadcastMessage(ChatColor.RED + n + " Was placed on team 'PIRATES'");
			p.setDisplayName(ChatColor.RED + n + ChatColor.WHITE);
		}
		if(i == 1)
		{
			team0.add(n);
			Bukkit.broadcastMessage(ChatColor.BLUE + n + " Was placed on team 'SHIP CREW'");
			p.setDisplayName(ChatColor.BLUE + n + ChatColor.WHITE);
		}
		if(i > 1){return;}
	}
	
	/*public void openShop(Player p)
	{
		Inventory i = Bukkit.createInventory(null, 27);
		//i.addItem(arg0)
	}*/
}
