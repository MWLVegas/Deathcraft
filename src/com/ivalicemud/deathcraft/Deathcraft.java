package com.ivalicemud.deathcraft;

//import java.util.logging.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Deathcraft extends JavaPlugin{
	static Deathcraft instance = null;
	
	private FileConfiguration ChestConfig;
	private File ChestConfigFile;
	
	static ArrayList<String> cactus = new ArrayList<String>();
	static ArrayList<String> fall = new ArrayList<String>();
	static ArrayList<String> pvp = new ArrayList<String>();
	static ArrayList<String> arrowmob = new ArrayList<String>();
	static ArrayList<String> arrowpvp = new ArrayList<String>();
	static ArrayList<String> tnt = new ArrayList<String>();
	static ArrayList<String> mob = new ArrayList<String>();
	static ArrayList<String> lava = new ArrayList<String>();
	static ArrayList<String> fallvoid = new ArrayList<String>();
	static ArrayList<String> fire = new ArrayList<String>();
	static ArrayList<String> suffocate = new ArrayList<String>();
	static ArrayList<String> drown = new ArrayList<String>();
	static ArrayList<String> magic = new ArrayList<String>();
	static ArrayList<String> lightning = new ArrayList<String>();
	static ArrayList<String> suicide = new ArrayList<String>();
	static ArrayList<String> other = new ArrayList<String>();
	static ArrayList<String> starve = new ArrayList<String>();
	static ArrayList<String> fireball = new ArrayList<String>();
	static ArrayList<String> fireballpvp = new ArrayList<String>(); 
	static ArrayList<String> fireballmob = new ArrayList<String>();
	static ArrayList<String> wither = new ArrayList<String>();
	static ArrayList<String> fallingblock = new ArrayList<String>();
	
	static String SmallChest = null;
	static String SmallChestFree = null;
	static String LargeChest = null;
	static String LargeChestFree = null;
	static boolean debug = false;
	static boolean UseDisplayName = true;
	static String Prefix = null;
	static boolean CheckForUpdates = true;
	static boolean PVPChest = false;
	static boolean PVEChest = false;
	static String currentVersion = null;
	static String thisVersion = null;
	static Plugin plugin = null;
	static boolean OutOfDate = false;
	static boolean CheckXp = false;
	static long LastDied = 0;
	static java.security.Timestamp LastDeath = null;
	
	//static FileConfiguration config = null;
	
	static boolean OverridePlugins = true;

	@Override
	 public void onEnable() {
		 instance = this;
		 plugin = getServer().getPluginManager().getPlugin("deathcraft");
		 thisVersion = plugin.getDescription().getVersion();
		 
		 loadConfig();
		 checkUpdate();
		 
	     getServer().getPluginManager().registerEvents(new Listeners(), this);
	     
//	     if ( getServer().getPluginManager().getPlugin("Essentials") != null ) {
//	    	 Bukkit.getServer().getLogger().info("This server is using 'Essentials' - /kill and /suicide are being overriden by DeathCraft - Set 'OverridePlugins' to false in the config to disable this!");
//	     }
	    }
		
	 public void saveChestConfig()
	    {
	        
	        debugmsg("Saving flatfile deathchest  config ...", null);
	        if(ChestConfig == null || ChestConfigFile == null)
	            return;
	        try
	        {
	            getChestConfig().save(ChestConfigFile);
	        }
	        catch(IOException ex)
	        {
	            getServer().getLogger().warning((new StringBuilder("Could not save config to ")).append(ChestConfigFile).toString());
	        }
	    }

	    public FileConfiguration getChestConfig()
	    {
	        if(ChestConfig == null)
	            reloadChestConfig();
	        return ChestConfig;
	    }
	    
	    
	    public void reloadChestConfig() {
	    	
	        if (ChestConfigFile == null) {
	        ChestConfigFile = new File(getDataFolder(),"deathchests.yml");
	        }
	        ChestConfig = YamlConfiguration.loadConfiguration(ChestConfigFile);

	    }
	    
	 @Override
	public void onDisable() {
	
		 nullArrays();
		instance = null;
	}
	
		@Override
	    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

			if( cmd.getName().equalsIgnoreCase("suicide") || cmd.getName().equalsIgnoreCase("wrists") || cmd.getName().equalsIgnoreCase("wrist"))  
	    	{
	    		if (!(sender instanceof Player)) {
	    		sender.sendMessage("Suicide is a player only command.");
	    		return true;
	    		}
	    				    		
	    		Player p = (Player) sender;
	    		EntityDamageEvent ede = new EntityDamageEvent(p,EntityDamageEvent.DamageCause.SUICIDE,1000);
	    		
	    		//Bukkit.getPluginManager().callEvent(ede);
	    		//if ( ede.isCancelled()) return true;
	    		
	    		ede.getEntity().setLastDamageCause(ede);
	    		p.setHealth(0);
	    		sender.sendMessage("You take your own life.");
	    		ede = null;	    		

	    	return true;
	    	}

	    	if( cmd.getName().equalsIgnoreCase("kill"))
	    	{
	    		if ( args.length < 1 ) { sender.sendMessage("Who are you trying to kill?"); return true; }
	    		
	    		Player target = (Bukkit.getServer().getPlayer(args[0]));
	            if (target == null) {
	               sender.sendMessage(args[0].toString() + " is not here!");
	               return true;
	            }
	            
	            EntityDamageEvent ede = new EntityDamageEvent(target,EntityDamageEvent.DamageCause.MAGIC,1000);
	    		//Bukkit.getPluginManager().callEvent(ede);
	    		//if ( ede.isCancelled()) return true;
	    		ede.getEntity().setLastDamageCause(ede);
	    		target.setHealth(0);
	    		sender.sendMessage("You smite " + target.getDisplayName() + ".");
	    		ede = null;	    		
	    	return true;
	    	}
	    	
	    	if(cmd.getName().equalsIgnoreCase("deathcraft")) { 
	    		ArrayList<String> Messages = null;// = new ArrayList<String>();
    		
	    		
	    		if ( args.length == 0 ) {//| (args.length == 1 & args[0].equalsIgnoreCase("version"))) {
	    			sender.sendMessage("DeathCraft v" + thisVersion + " ~ By Raum");
	    			sender.sendMessage("Commands:");
	    			sender.sendMessage("&6Ignore                 - Ignore PVP or PVE Death Messages");
	    		
	    		if ( sender.hasPermission("deathcraft.admin") ) { 
	    			sender.sendMessage("&3Reload - Reloads the configuration file");
	    			sender.sendMessage("&6List (msg) - List messages for damage type (msg)");
	    			sender.sendMessage("&3Add (type) (msg) - Add a message to death (type)");
	    			sender.sendMessage("&6Delete (type) (num) - Remove (num) from death (type)");
	    			sender.sendMessage("&3ListCustom (Id) - List messages for item (id)");
	    			sender.sendMessage("&6AddCustom (Id) (msg) - Add a message to item (id)");
	    			sender.sendMessage("&3DeleteCustom (Id) (num) - Remove (num) from item (id)");
	    			sender.sendMessage("&6Set (options) -  Set portions of the config online");
	    			sender.sendMessage("");
	    			sender.sendMessage("&FPlease open a ticket for any suggestions, or bug reports!");
	    		}
	    		return true;	
	    		}
	    		
	    		if ( args.length >= 1 && args[0].equalsIgnoreCase("ignore") ) {
	    			
	    			if (!(sender instanceof Player)) {
	    	    		sender.sendMessage("Ignore is a player only command.");
	    	    		return true;
	    	    		}
	    			
	    			Player p = (Player) sender;
	    			
	    			
	    			
	    			if ( args.length < 2 ) {
	    				sender.sendMessage("You must choose to ignore PVE or PVP");
	    				sender.sendMessage("Ignore PVE: " + p.hasMetadata("deathcraft.ignore.pve"));
	    				sender.sendMessage("Ignore PVP: " +p.hasMetadata("deathcraft.ignore.pvp"));
	    				return true;
	    			}
	    			    			
		   	
	    				if ( args[1].equalsIgnoreCase("pve")) {
	    					if ( p.hasMetadata("deathcraft.ignore.pve")) {
	    						sender.sendMessage("No longer ignoring PVE.");
	    						p.removeMetadata("deathcraft.ignore.pve", plugin);
	    					} else {
	    						sender.sendMessage("Now ignoring PVE.");
	    						p.setMetadata("deathcraft.ignore.pve",  new FixedMetadataValue(plugin,"true"));
	    					}
	    						
	    					}
	    				
	    				else if ( args[1].equalsIgnoreCase("pvp")) {
	    					if ( p.hasMetadata("deathcraft.ignore.pvp")) {
	    						sender.sendMessage("No longer ignoring PVP.");
	    						p.removeMetadata("deathcraft.ignore.pvp", plugin);
	    					} else {
	    						sender.sendMessage("Now ignoring PVP.");
	    						p.setMetadata("deathcraft.ignore.pvp",  new FixedMetadataValue(plugin,"true"));
	    					}
	    						
	    					}
	    				else { sender.sendMessage("You must choose to ignore PVE or PVP"); return true; }
	    				
	    				sender.sendMessage("Flag toggled. Current Settings:");
	    				sender.sendMessage("Ignore PVE: " + p.hasMetadata("deathcraft.ignore.pve"));
	    				sender.sendMessage("Ignore PVP: " + p.hasMetadata("deathcraft.ignore.pvp"));

	    				return true;
	    			
	    		}
	    		
	    		if ( !sender.hasPermission("deathcraft.admin") ) 
	    			return true;
	    		
	    		if (args[0].equalsIgnoreCase("head")) {
	    			if ( !(sender instanceof Player))
	    				return true;
	    			
	    			if ( !sender.hasPermission("deathcraft.head") && !sender.hasPermission("deathcraft.admin") )
	    			{
	    				sender.sendMessage("You are unable to do that.");
	    				return true;
	    			}
	    			
	    			Player p = (Player) sender;
	    			if ( args.length != 2 )
	    			{
	    				sender.sendMessage("Syntax: /dc head (player)");
	    				return true;
	    			}
	    			
	    			String name = args[1].toString();
	    			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
	    			SkullMeta meta = (SkullMeta) head.getItemMeta();
	    			meta.setOwner(name);
	    			meta.setDisplayName(name + "'s Head");
	    			head.setItemMeta(meta);
	    			p.getInventory().addItem(head);
	    			return true;
	    		}	    		
	    		if (args[0].equalsIgnoreCase("save")) { 
	    			SaveConfig(); sender.sendMessage("Saving config file ..."); return true;
	    			}

	    		if (args[0].equalsIgnoreCase("reload")) {
	    				sender.sendMessage("Reloading DeathCraft configuration ...");
	    				reloadConfig();
	    				saveConfig();
	    				
	    				nullArrays();
	    				loadConfig();
	    				return true;
	    			
	    		}
	    		
	    		if (args[0].equalsIgnoreCase("set")) {
	    			if ( args.length <= 2 ) {
	    				sender.sendMessage("What option do you wish to set?");
	    				sender.sendMessage("Prefix");
	    				return true;
	    			}
	    			if (  args[1].equalsIgnoreCase("prefix")) {
	    				if ( args.length <= 2 ) {
	    					sender.sendMessage("You must provide a prefix to set!");
	    					return true;
	    				}
	    				
	    				this.getConfig().set("general.Prefix", args[2] );
	    				Deathcraft.Prefix = args[2];
	    				sender.sendMessage("Prefix set to: -->"+ colorize(Prefix)+"<-- (Remember to close your color tags!)");
	    				SaveConfig();
	    				return true;
	    			}
	    			
	    		}
	    		
				if (args[0].equalsIgnoreCase("list")) {
	    				
    				if ( args.length <= 1) { sender.sendMessage("You must supply a damage type you're listing!"); return true; }
					
    				Messages = getArray(args[1].toLowerCase());

    				if ( Messages == null ) {
		    				sender.sendMessage("That is an invalid damage type.");
		    			return true;
		    			}

	    				sender.sendMessage("Listing Messages for " + args[1]);
	    				sender.sendMessage("==============================================");
	    				int num = 0;
	    				while ( num < Messages.size() ) {
	    					sender.sendMessage(num + ") " + Messages.get(num));
	    		    		num++;
	    		    	}
	    				Messages = null;
	    				return true;
	    			
	    		}

	    		if (args[0].equalsIgnoreCase("add")) {
	    			String NewMessage = "";
    				if ( args.length <= 2) { sender.sendMessage("Syntax: /dc add (type) (message)"); return true; }
					
    				Messages = getArray(args[1].toLowerCase());

    				if ( Messages == null ) {
		    				sender.sendMessage("That is an invalid damage type.");
		    			return true;
		    			}

    				
    				for ( int i = 2; i < args.length; ++i) {
    					NewMessage += args[i] + " ";
    				}
    				
    				
    				Messages.add(NewMessage);
    				SaveConfig();
    				sender.sendMessage("Adding Message for : " + args[1]);
    				sender.sendMessage(NewMessage); 
    				
	    				return true;
	    			} 
	    		if (args[0].equalsIgnoreCase("delete")) {
	    			if ( args.length <= 2) { sender.sendMessage("Syntax: /dc delete (type) (message #)"); return true; }
					
	    			if ( !isnumber(args[2]) ) { sender.sendMessage("That is not a valid number!"); return true; }
	    			
	    			int num = getnum(args[2]);
	    			
    				Messages = getArray(args[1].toLowerCase());


    				if ( Messages == null ) {
		    				sender.sendMessage("That is an invalid damage type.");
		    			return true;
		    			}

	    			if ( num < 0 | num > Messages.size() ) {
	    				sender.sendMessage("That is not a valid number to delete.");
	    				return true;
	    			}
	    			
	    			sender.sendMessage("Deleting message '" + Messages.get(num) + "' from '"+args[1]+"'");
	    			Messages.remove(num);
	    			
	    			SaveConfig();
	    			loadConfig();
	    	
	    		return true;
	    	}

	    		if (args[0].equalsIgnoreCase("listcustom")) {
    				
    				if ( args.length <= 1) { sender.sendMessage("You must supply a damage type you're listing!"); return true; }
					
    				Messages = (ArrayList<String>) this.getConfig().getStringList("CustomItem." + args[1]);

    				if ( Messages.size() < 1 ) {
		    				sender.sendMessage("That item is not found in the config!");
		    			return true;
		    			}

	    				sender.sendMessage("Listing Messages for Item # " + args[1]);
	    				sender.sendMessage("==============================================");
	    				int num = 0;
	    				while ( num < Messages.size() ) {
	    					sender.sendMessage(num + ") " + Messages.get(num));
	    		    		num++;
	    		    	}
	    				Messages = null;
	    				return true;
	    			
	    		}

	    		if (args[0].equalsIgnoreCase("addcustom")) {
	    			String NewMessage = "";
    				if ( args.length <= 2) { sender.sendMessage("Syntax: /dc add (type) (message)"); return true; }
					
    				if ( !isnumber(args[1]) ) { sender.sendMessage("That is not a valid number!"); return true; }
	    			
    				Messages = (ArrayList<String>) this.getConfig().getStringList("CustomItem." + args[1]);

    				if ( Messages == null ) {
		    				sender.sendMessage("That is an invalid item number.");
		    			return true;
		    			}

    				    				
    				for ( int i = 2; i < args.length; ++i) {
    					NewMessage += args[i] + " ";
    				}
    				addlist("CustomItem."+args[1],NewMessage);
    				saveConfig();
    				
    				sender.sendMessage("Adding Message for Item ID " + args[1]);
    				sender.sendMessage(NewMessage); 
    				
	    				return true;
	    			} 
	    		if (args[0].equalsIgnoreCase("deletecustom")) {
	    			if ( args.length <= 2) { sender.sendMessage("Syntax: /dc delete (type) (message #)"); return true; }
					
	    			if ( !isnumber(args[2]) ) { sender.sendMessage("That is not a valid number!"); return true; }
	    			
	    			
	    			int num = getnum(args[2]);
	    			
	    			Messages = (ArrayList<String>) this.getConfig().getStringList("CustomItem." + args[1]);

    				if ( Messages.size() < 1 ) {
		    				sender.sendMessage("That is an invalid damage type.");
		    			return true;
		    			}

	    			if ( num < 0 | num > Messages.size() ) {
	    				sender.sendMessage("That is not a valid number to delete.");
	    				return true;
	    			}
	    			
	    			sender.sendMessage("Deleting message '" + Messages.get(num) + "' from '"+args[1]+"'");
	    			Messages.remove(num);
	    			this.getConfig().set("CustomItem."+args[1], (List<String>) Messages);
	    			saveConfig();
	    			sender.sendMessage("Message deleted.");
	    			Messages = null;
	    		return true;
	    	}
	    		
	    	}
	    	
	    	
	    	return false; 
	    }
	    

		
		public void nullArrays(  ) {
			 cactus = null;
			 fall = null;
			 pvp = null;
			 arrowmob = null;
			 arrowpvp = null;
			 tnt = null;
			 mob = null;
			 lava = null;
			 fallvoid = null;
			 fire = null;
			 suffocate = null;
			 drown = null;
			 magic = null;
			 lightning = null;
			 suicide = null;
			 other = null;
			 starve = null;
			 fireball = null;
			 fireballpvp = null;
			 fireballmob = null;
			 wither = null;
			 fallingblock = null;
			 
			  SmallChest = null;
				 SmallChestFree = null;
				 LargeChest = null;
				 LargeChestFree = null;
				 
				 debug = false;
				 UseDisplayName = true;
				 Prefix = null;
				 CheckForUpdates = true;
				 PVPChest = false;
				 PVEChest = false;
				 currentVersion = null;
				plugin = null;
				 OutOfDate = false;
				 CheckXp = false;
				LastDied = 0;
				LastDeath = null;
				 
				 OverridePlugins = true;
		}

		public void addlist(String theList, String theLine)
		{
			
			List<String> list = getConfig().getStringList(theList); 
			list.add(theLine);
			getConfig().set(theList,list);
			
		}
		
		public void savelist(String ListName, ArrayList<String> List) {
			
		}

		public void SaveConfig() {
			
			
			this.getConfig().set("deathmessages.Cactus", (List<String>) cactus);
			this.getConfig().set("deathmessages.Fall", (List<String>) fall);
			this.getConfig().set("deathmessages.pvp", (List<String>) pvp);
			this.getConfig().set("deathmessages.ArrowMob", (List<String>) arrowmob);
			this.getConfig().set("deathmessages.ArrowPVP", (List<String>) arrowpvp);
			this.getConfig().set("deathmessages.TNT", (List<String>) tnt);
			this.getConfig().set("deathmessages.Mob", (List<String>) mob);
			this.getConfig().set("deathmessages.Lava", (List<String>) lava);
			this.getConfig().set("deathmessages.Void", (List<String>) fallvoid);
			this.getConfig().set("deathmessages.Fire", (List<String>) fire);
			this.getConfig().set("deathmessages.Suffocate", (List<String>) suffocate);
			this.getConfig().set("deathmessages.Drown", (List<String>) drown);
			this.getConfig().set("deathmessages.Magic", (List<String>) magic);
			this.getConfig().set("deathmessages.Lightning", (List<String>) lightning);
			this.getConfig().set("deathmessages.Suicide", (List<String>) suicide);
			this.getConfig().set("deathmessages.Other", (List<String>) other);
			this.getConfig().set("deathmessages.Starve", (List<String>) starve);
			this.getConfig().set("deathmessages.Fireball", (List<String>) fireball);
			this.getConfig().set("deathmessages.FireballPVP", (List<String>) fireballpvp);
			this.getConfig().set("deathmessages.FireballMob", (List<String>) fireballmob);
			this.getConfig().set("deathmessages.Wither", (List<String>) wither);
			this.getConfig().set("deathmessages.FallingBlock", (List<String>) fallingblock);
			
		
			saveConfig();
		}
		
		public void ConvertConfig() {

			if (getConfig().contains("Debug")) { this.getConfig().set("general.Debug", getConfig().getBoolean("Debug")); getConfig().set("Debug", null); }
        	if (getConfig().contains("CheckForUpdates")) { this.getConfig().set("general.CheckForUpdates",getConfig().getBoolean("CheckForUpdates"));getConfig().set("CheckForUpdates",null); }
        	if (getConfig().contains("Prefix")) { this.getConfig().set("general.Prefix", getConfig().getString("Prefix"));  getConfig().set("Prefix",null); }
          	if (getConfig().contains("UseDisplayName")) { this.getConfig().set("general.UseDisplayName",getConfig().getBoolean("UseDisplayName")); getConfig().set("UseDisplayName",null); }
          	if (getConfig().contains("OverridePlugins")) { this.getConfig().set("general.OverridePlugins",getConfig().getBoolean("OverridePlugins")); getConfig().set("OverridePlugins",null);  }
        	if (getConfig().contains("PVEChest")) { this.getConfig().set("chest.pvechest",getConfig().getBoolean("PVEChest")); getConfig().set("PVEChest",null); }
        	if (getConfig().contains("PVPChest")) { this.getConfig().set("chest.pvpchest",getConfig().getBoolean("PVPChest")); getConfig().set("PVPChest",null); }
        	if (getConfig().contains("CheckXp")) { this.getConfig().set("general.CheckXp",getConfig().getBoolean("CheckXp")); getConfig().set("CheckXp",null); }

        	if (getConfig().contains("AdminPerm")) { this.getConfig().set("general.AdminPerm", getConfig().getString("AdminPerm")); getConfig().set("AdminPerm",null);}
        	if (getConfig().contains("SmallChest")) { this.getConfig().set("chest.SmallChest", getConfig().getString("SmallChest")); getConfig().set("SmallChest",null);}
        	if (getConfig().contains("SmallChestFree")) { this.getConfig().set("chest.SmallChestFree", getConfig().getString("SmallChestFree")); getConfig().set("SmallChestFree",null);}
        	if (getConfig().contains("LargeChest")) { this.getConfig().set("chest.LargeChest", getConfig().getString("LargeChest"));getConfig().set("LargeChest",null); }
        	if (getConfig().contains("LargeChestFree")) { this.getConfig().set("chest.LargeChestFree", getConfig().getString("LargeChestFree")); getConfig().set("LargeChestFree",null);}

        	
        	if (getConfig().contains("Cactus")) {
        	cactus = (ArrayList<String>) this.getConfig().getStringList("Cactus");
        	fall= (ArrayList<String>) this.getConfig().getStringList("Fall");
        	pvp= (ArrayList<String>) this.getConfig().getStringList("PVP");
        	arrowmob= (ArrayList<String>) this.getConfig().getStringList("ArrowMob");
        	arrowpvp= (ArrayList<String>) this.getConfig().getStringList("ArrowPVP");
        	tnt= (ArrayList<String>) this.getConfig().getStringList("TNT");
        	mob= (ArrayList<String>) this.getConfig().getStringList("Mob");
        	lava= (ArrayList<String>) this.getConfig().getStringList("Lava");
        	fallvoid= (ArrayList<String>) this.getConfig().getStringList("Void");
        	fire= (ArrayList<String>) this.getConfig().getStringList("Fire");
        	suffocate= (ArrayList<String>) this.getConfig().getStringList("Suffocate");
        	drown= (ArrayList<String>) this.getConfig().getStringList("Drown");
        	magic= (ArrayList<String>) this.getConfig().getStringList("Magic");
        	lightning= (ArrayList<String>) this.getConfig().getStringList("Lightning");
        	suicide= (ArrayList<String>) this.getConfig().getStringList("Suicide");
        	other= (ArrayList<String>) this.getConfig().getStringList("Other");
        	starve= (ArrayList<String>) this.getConfig().getStringList("Starve");
        	fireball= (ArrayList<String>) this.getConfig().getStringList("Fireball");
        	fireballpvp= (ArrayList<String>) this.getConfig().getStringList("FireballPVP");
        	fireballmob= (ArrayList<String>) this.getConfig().getStringList("FireballMob");
        	wither= (ArrayList<String>) this.getConfig().getStringList("Wither");
        	fallingblock= (ArrayList<String>) this.getConfig().getStringList("FallingBlock");
        	SaveConfig();
        	getConfig().set("Fall",null);
        	getConfig().set("Cactus",null);
        	getConfig().set("PVP",null);
        	getConfig().set("ArrowMob",null);
        	getConfig().set("ArrowPVP",null);
        	getConfig().set("TNT",null);
        	getConfig().set("Mob",null);
        	getConfig().set("Lava",null);
        	getConfig().set("Void",null);
        	getConfig().set("Fire",null);
        	getConfig().set("Suffocate",null);
        	getConfig().set("Drown",null);
        	getConfig().set("Magic",null);
        	getConfig().set("Lightning",null);
        	getConfig().set("Suicide",null);
        	getConfig().set("Other",null);
        	getConfig().set("Starve",null);
        	getConfig().set("Fireball",null);
        	getConfig().set("FireballPVP",null);
        	getConfig().set("FireballMob",null);
        	getConfig().set("Wither",null);
        	getConfig().set("FallingBlock",null);
        	}
        	
        	if (getConfig().contains("general.AdminPerm")) this.getConfig().set("general.AdminPerm",null);
        	if (getConfig().contains("chest.SmallChest")) this.getConfig().set("chest.SmallChest", null );
        	if (getConfig().contains("chest.SmallChestFree")) this.getConfig().set("chest.SmallChestFree", null);
        	if (getConfig().contains("chest.LargeChest")) this.getConfig().set("chest.LargeChest", null);
        	if (getConfig().contains("chest.LargeChestFree")) this.getConfig().set("chest.LargeChestFree", null);

        	if (getConfig().contains("KeepXp")) this.getConfig().set("KeepXp", null);
        	saveConfig();

        	
		}
		
		public void setConfig( String m, Object str)
		{
        	if (!getConfig().contains(m)) this.getConfig().set(m, str);

		}
		public void loadConfig() {
			try {

				
	        	File Deathcraft = new File("plugins" + File.separator + "Deathcraft" + File.separator + "config.yml");
	        	Deathcraft.mkdir();
	        	
	        	ConvertConfig();
	        	
	        	if (!getConfig().contains("general.Debug")) this.getConfig().set("general.Debug",false);
	        	if (!getConfig().contains("general.CheckForUpdates")) this.getConfig().set("general.CheckForUpdates",true);
	        	if (!getConfig().contains("general.Prefix")) this.getConfig().set("general.Prefix","&9[&7DC&9] ");
	          	if (!getConfig().contains("general.UseDisplayName")) this.getConfig().set("general.UseDisplayName",true);
	        	if (!getConfig().contains("general.CheckXp")) this.getConfig().set("general.CheckXp",false);
	        	if (!getConfig().contains("general.Herobrine")) this.getConfig().set("general.Herobrine",true);
	        	
	        	if (!getConfig().contains("chest.protect")) this.getConfig().set("chest.protect",true);
	        	if (!getConfig().contains("chest.decayInMinutes")) this.getConfig().set("chest.decayInMinutes",60);
	          	if (!getConfig().contains("chest.pvechest")) this.getConfig().set("chest.pvechest",false);
	        	if (!getConfig().contains("chest.pvpchest")) this.getConfig().set("chest.pvpchest",false);
	        	if (!getConfig().contains("chest.DestroyBlock")) 
	        		{
	        		this.getConfig().set("chest.DestroyBlock.0",true);
	        		this.getConfig().set("chest.DestroyBlock.6",true);
	        		this.getConfig().set("chest.DestroyBlock.8",true);
	        		}

	        
	        	
	        	
	        	if (!getConfig().contains("CustomItem")) addlist("CustomItem.7","%1 was whacked to death by %2's bedrock!");
	        	
	        	setConfig("deathmessages.enableMechanic",true);
	        	if (!getConfig().contains("deathmessages.Cactus")) addlist("deathmessages.Cactus","%1 was pricked to death!");   	
	        	if (!getConfig().contains("deathmessages.Fall")) addlist("deathmessages.Fall","%1 hit the ground too hard!");  	
	        	if (!getConfig().contains("deathmessages.pvp")) addlist("deathmessages.pvp","%1 was vanquished by %2!");  	
	        	if (!getConfig().contains("deathmessages.ArrowMob")) addlist("deathmessages.ArrowMob","%1 was shot by %5 %2!");  	
	        	if (!getConfig().contains("deathmessages.ArrowPVP")) addlist("deathmessages.ArrowPVP","%1 was shot by %2's bow!");  	
	        	if (!getConfig().contains("deathmessages.TNT")) addlist("deathmessages.TNT","%1 has exploded.");  	
	        	if (!getConfig().contains("deathmessages.Mob")) addlist("deathmessages.Mob","%1 was killed by %5 %2.");  	
	        	if (!getConfig().contains("deathmessages.Lava")) addlist("deathmessages.Lava","%1 tried to swim in lava.");  	
	        	if (!getConfig().contains("deathmessages.Void")) addlist("deathmessages.Void","%1 fell through the world.");  	
	        	if (!getConfig().contains("deathmessages.Fire")) addlist("deathmessages.Fire","%1 went up in flames!");  	
	        	if (!getConfig().contains("deathmessages.Suffocate")) addlist("deathmessages.Suffocate","%1 suffocated.");  	
	        	if (!getConfig().contains("deathmessages.Drown")) addlist("deathmessages.Drown","%1 drowned.");  	
	        	if (!getConfig().contains("deathmessages.Magic")) addlist("deathmessages.Magic","%1 was killed by magic.");  	
	        	if (!getConfig().contains("deathmessages.Lightning")) addlist("deathmessages.Lightning","%1 was struck down by lightning.");  	
	        	if (!getConfig().contains("deathmessages.Suicide")) addlist("deathmessages.Suicide","%1 took their own life.");  	
	        	if (!getConfig().contains("deathmessages.Other")) addlist("deathmessages.Other","%1 was killed by mysterious forces.");  	
	        	if (!getConfig().contains("deathmessages.Starve")) addlist("deathmessages.Starve","%1 has starved to death.");
	        	if (!getConfig().contains("deathmessages.Fireball")) addlist("deathmessages.Fireball","%1 has been struck by a fireball!");
	        	if (!getConfig().contains("deathmessages.FireballPVP")) addlist("deathmessages.FireballPVP","%1 has was fried by %2's fireball!");
	        	if (!getConfig().contains("deathmessages.FireballMob")) addlist("deathmessages.FireballMob","A %2 has fried %1 with a fireball!");
	        	if (!getConfig().contains("deathmessages.Wither")) addlist("deathmessages.Wither","%1 has withered away.");
	        	if (!getConfig().contains("deathmessages.FallingBlock")) addlist("deathmessages.FallingBlock","%1 was crushed by a falling anvil!");
	        	
	        	setConfig("head.enabled",true);
	        	if (!getConfig().contains("head.lootbonus")) this.getConfig().set("head.lootbonus",1);
	        	if (!getConfig().contains("head.announce")) this.getConfig().set("head.announce",true);
	        	if (!getConfig().contains("head.announce.pve")) this.getConfig().set("head.announce.pve","%1 has been beheaded!");
	        	if (!getConfig().contains("head.announce.pvp")) this.getConfig().set("head.announce.pvp","%2 has beheaded %1!");
	        	if (!getConfig().contains("head.pve")) this.getConfig().set("head.pve", true);
	        	if (!getConfig().contains("head.pvp")) this.getConfig().set("head.pvp", true);
	        	if (!getConfig().contains("head.drop.player")) this.getConfig().set("head.drop.player",5);
	        	if (!getConfig().contains("head.drop.creeper")) this.getConfig().set("head.drop.creeper",2);
	        	if (!getConfig().contains("head.drop.zombie")) this.getConfig().set("head.drop.zombie",2);
	        	if (!getConfig().contains("head.drop.skeleton")) this.getConfig().set("head.drop.skeleton",2);
	        		setConfig("head.drop.wither",2);
	        		setConfig("head.drop.blaze",2);
	        		setConfig("head.drop.cave_spider",2);
	        		setConfig("head.drop.chicken",2);
	        		setConfig("head.drop.cow",2);
	        		setConfig("head.drop.enderman",2);
	        		setConfig("head.drop.ghast",2);
	        		setConfig("head.drop.magma_cube",2);
	        		setConfig("head.drop.mushroom_cow",2);
	        		setConfig("head.drop.pig",2);
	        		setConfig("head.drop.pig_zombie",2);
	        		setConfig("head.drop.sheep",2);
	        		setConfig("head.drop.slime",2);
	        		setConfig("head.drop.spider",2);
	        		setConfig("head.drop.villager",2);
	        		setConfig("head.drop.iron_golem",2);
	        		setConfig("head.drop.ocelot",2);
	        		setConfig("head.drop.squid",2);
	        		setConfig("head.drop.bat",2);
	        		setConfig("head.drop.ender_dragon",2);
	        		setConfig("head.drop.silverfish",2);
	        		setConfig("head.drop.snowman",2);
	        		setConfig("head.drop.horse",2);
	        		setConfig("head.drop.witch",2);

	        	
	        	CheckForUpdates = this.getConfig().getBoolean("general.CheckForUpdates");
	        	OverridePlugins = this.getConfig().getBoolean("general.OverridePlugins");
	        	Prefix = this.getConfig().getString("general.Prefix");
	        	debug = this.getConfig().getBoolean("general.Debug");
	        	PVEChest = this.getConfig().getBoolean("chest.pvechest");
	        	PVPChest = this.getConfig().getBoolean("chest.pvpchest");
	        	UseDisplayName = this.getConfig().getBoolean("general.UseDisplayName");
	        	
	        	cactus = (ArrayList<String>) this.getConfig().getStringList("deathmessages.Cactus");
	        	fall= (ArrayList<String>) this.getConfig().getStringList("deathmessages.Fall");
	        	pvp= (ArrayList<String>) this.getConfig().getStringList("deathmessages.pvp");
	        	arrowmob= (ArrayList<String>) this.getConfig().getStringList("deathmessages.ArrowMob");
	        	arrowpvp= (ArrayList<String>) this.getConfig().getStringList("deathmessages.ArrowPVP");
	        	tnt= (ArrayList<String>) this.getConfig().getStringList("deathmessages.TNT");
	        	mob= (ArrayList<String>) this.getConfig().getStringList("deathmessages.Mob");
	        	lava= (ArrayList<String>) this.getConfig().getStringList("deathmessages.Lava");
	        	fallvoid= (ArrayList<String>) this.getConfig().getStringList("deathmessages.Void");
	        	fire= (ArrayList<String>) this.getConfig().getStringList("deathmessages.Fire");
	        	suffocate= (ArrayList<String>) this.getConfig().getStringList("deathmessages.Suffocate");
	        	drown= (ArrayList<String>) this.getConfig().getStringList("deathmessages.Drown");
	        	magic= (ArrayList<String>) this.getConfig().getStringList("deathmessages.Magic");
	        	lightning= (ArrayList<String>) this.getConfig().getStringList("deathmessages.Lightning");
	        	suicide= (ArrayList<String>) this.getConfig().getStringList("deathmessages.Suicide");
	        	other= (ArrayList<String>) this.getConfig().getStringList("deathmessages.Other");
	        	starve= (ArrayList<String>) this.getConfig().getStringList("deathmessages.Starve");
	        	fireball= (ArrayList<String>) this.getConfig().getStringList("deathmessages.Fireball");
	        	fireballpvp= (ArrayList<String>) this.getConfig().getStringList("deathmessages.FireballPVP");
	        	fireballmob= (ArrayList<String>) this.getConfig().getStringList("deathmessages.FireballMob");
	        	wither= (ArrayList<String>) this.getConfig().getStringList("deathmessages.Wither");
	        	fallingblock= (ArrayList<String>) this.getConfig().getStringList("deathmessages.FallingBlock");

	        	CheckXp = this.getConfig().getBoolean("general.CheckXp");
	        	SmallChest = this.getConfig().getString("chest.SmallChest");
	        	SmallChestFree = this.getConfig().getString("chest.SmallChestFree");
	        	LargeChest = this.getConfig().getString("chest.LargeChest");
	        	LargeChestFree = this.getConfig().getString("chest.LargeChestFree");
	        	saveConfig();
	        	getChestConfig();
	        	saveChestConfig();
	        	
	        }catch(Exception e1){
	        	e1.printStackTrace();
	        }
		}
		
	    
	 ArrayList<String> getArray( String name ) {
		 
			if ( name.equalsIgnoreCase("pvp")) {
              	  return (ArrayList<String>) Deathcraft.pvp;
    				} else if ( name.equalsIgnoreCase("cactus")) {
	              	  return (ArrayList<String>) Deathcraft.cactus;
  				} else if ( name.equalsIgnoreCase("fall")) {
	              	  return (ArrayList<String>) Deathcraft.fall;
  				} else if ( name.equalsIgnoreCase("arrowmob")) {
	              	  return (ArrayList<String>) Deathcraft.arrowmob;
  				} else if ( name.equalsIgnoreCase("arrowpvp")) {
	              	  return (ArrayList<String>) Deathcraft.arrowpvp;
  				} else if ( name.equalsIgnoreCase("tnt")) {
	              	  return (ArrayList<String>) Deathcraft.tnt;
  				} else if ( name.equalsIgnoreCase("mob")) {
	              	  return (ArrayList<String>) Deathcraft.mob;
  				} else if ( name.equalsIgnoreCase("lava")) {
	              	  return (ArrayList<String>) Deathcraft.lava;
  				} else if ( name.equalsIgnoreCase("fallvoid")) {
	              	  return (ArrayList<String>) Deathcraft.fallvoid;
  				} else if ( name.equalsIgnoreCase("fire")) {
	              	  return (ArrayList<String>) Deathcraft.fire;
  				} else if ( name.equalsIgnoreCase("suffocate")) {
	              	  return (ArrayList<String>) Deathcraft.suffocate;
  				} else if ( name.equalsIgnoreCase("drown")) {
	              	  return (ArrayList<String>) Deathcraft.drown;
  				} else if ( name.equalsIgnoreCase("lightning")) {
	              	  return (ArrayList<String>) Deathcraft.lightning;
  				} else if ( name.equalsIgnoreCase("magic")) {
	              	  return (ArrayList<String>) Deathcraft.magic;
  				} else if ( name.equalsIgnoreCase("suicide")) {
	              	  return (ArrayList<String>) Deathcraft.suicide;
  				} else if ( name.equalsIgnoreCase("other")) {
	              	  return (ArrayList<String>) Deathcraft.other;
  				} else if ( name.equalsIgnoreCase("starve")) {
	  	             	  return (ArrayList<String>) Deathcraft.starve;
	    			} else if ( name.equalsIgnoreCase("fireball")) {
		  	           	  return (ArrayList<String>) Deathcraft.fireball;
	    			} else if ( name.equalsIgnoreCase("fireballpvp")) {
		  	            	  return (ArrayList<String>) Deathcraft.fireballpvp;
	    			} else if ( name.equalsIgnoreCase("fireballmob")) {
		  	            	  return (ArrayList<String>) Deathcraft.fireballmob;
	    			} else if ( name.equalsIgnoreCase("wither")) {
	  	            	  return (ArrayList<String>) Deathcraft.wither;
	    			} else if ( name.equalsIgnoreCase("fallingblock")) {
	  	            	  return (ArrayList<String>) Deathcraft.fallingblock;
	    			}
		 return null;
	 }
	  	  
	 public boolean isnumber( String s ) {
			    try { 
			        Integer.parseInt(s); 
			    } catch(NumberFormatException e) { 
			        return false; 
			    }
			   return true;
	}
	 
	 public int getnum( String s ) {
		 int num;
		    try { 
		        num = Integer.parseInt(s); 
		    } catch(NumberFormatException e) { 
		        return -1; 
		    }

		    return num;
	 }
	 
	 static public String DisplayName( Player p ) {
		 
		 if ( !Deathcraft.UseDisplayName )
		 {
			 return p.getName();
		 }
		 
		
			 
			 return p.getDisplayName();
		 }
		 
	
		public void debugmsg(String message, Player player) {
			if ( debug != true ) { return; }
			
			Bukkit.getServer().getLogger().info("DC DEBUG: " + message);
			if ( player != null ) player.sendMessage("DC DEBUG: " + message);
		}
		
		public static void log(String message) {
			Bukkit.getServer().getLogger().info(message);
		}
		
		
		public static void colorchat( String msg, Boolean pvp, Boolean forced ) {
			
			String newmsg = Prefix.replaceAll("(?i)&([a-k0-9])", "\u00A7$1") + " " +msg.replaceAll("(?i)&([a-k0-9])", "\u00A7$1");
			Player[] onlinePlayerList = Bukkit.getServer().getOnlinePlayers();
			boolean disp = false;

			
			for ( Player player : onlinePlayerList) {
				//log("Checking Player " + player.getName() + ":" + player.hasPermission("deathcraft.ignore.pvp") + "," + player.hasPermission("deathcraft.ignore.pve") + " - PVP?" + pvp );
				
   	    	
				disp = false;
				if ( pvp && !player.hasMetadata("deathcraft.ignore.pvp")) disp = true;
				else if ( !pvp && !player.hasMetadata("deathcraft.ignore.pve")) disp = true;
				
				//if ( pvp && !player.isPermissionSet("deathcraft.ignore.pvp") ) disp = true;
				//else if ( !pvp && !player.isPermissionSet("deathcraft.ignore.pve")) disp = true;
				
				if ( disp == true || forced == true ) player.sendMessage(newmsg);
			}
		}
		
		public String colorize( String msg ) {
			return msg.replaceAll("(?i)&([a-k0-9])", "\u00A7$1");
		}
	  

	    public void checkUpdate()
	    {
	    	return;
	    	/*
	        boolean found = false;
	        if(!getConfig().getBoolean("general.CheckForUpdates"))
	            return;
	        String readurl = "http://dev.bukkit.org/server-mods/deathcraft2/files.rss";
	        try
	        {
	            URL url = new URL(readurl);
	            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
	            String str;
	            while((str = br.readLine()) != null) 
	            {
	                if(str.contains("<title>") && !found)
	                {
	                    found = true;
	                    continue;
	                }
	                if(!str.contains("<title>") || !found)
	                    continue;
	                String line = str.replace("<title>", "");
	                str = line.replace("</title>", "");
	                str = str.replace("Deathcraft", "");
	                currentVersion = str.trim();
	                if(!currentVersion.equals(plugin.getDescription().getVersion()))
	                {
	                	
	           			 getServer().getLogger().info("This version of DeathCraft is not the most recent release on Bukkit!"); 
	           			getServer().getLogger().info("You are using "+ plugin.getDescription().getVersion() + " but the latest on Bukkit is " + currentVersion);
	           			getServer().getLogger().info("You may download the most recent release at http://dev.bukkit.org/server-mods/deathcraft2/");
	                    OutOfDate = true;
	                }
	                break;
	            }
	            br.close();
	        }
	        catch(IOException ioexception) { }
	        */
	    }
 
}

