package com.ivalicemud.deathcraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
//import org.bukkit.event.EventPriority;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;


public class Listeners implements Listener {

	Deathcraft plugin = Deathcraft.instance;
	
	static boolean pvp;
	private final Random rand = new Random();

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		

		Player killer = event.getEntity().getKiller();
		double lootpct = 1;

		if (!plugin.getConfig().getBoolean("head.pve") && killer == null) // PvE death - no head
			return;

		if (killer != null) {
			ItemStack weapon = killer.getItemInHand();
			if (weapon != null) {
				lootpct += (plugin.getConfig().getDouble("head.lootbonus") * weapon
						.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS));
				plugin.debugmsg("Loot Bonus: " + lootpct, null);
			}
		}

		switch (event.getEntityType()) {
		default:
			CheckHead(event, lootpct);
			break;
		case PLAYER:
			int chance = rand.nextInt(100);
			Player player = (Player) event.getEntity();
			double regdrop = plugin.getConfig().getDouble("head.drop.player");
			double dropChance = regdrop * lootpct;
			if (killer == player) {
				plugin.debugmsg("Suicide - No head", null);
				return;
			} // suicide
			if (chance >= dropChance) {
				plugin.debugmsg("Did not meet drop threshold - Regdrop: "
						+ regdrop + "Drop Chance: " + dropChance + " Rolled: "
						+ chance, null);
				return;
			}

			if (!plugin.getConfig().getBoolean("head.pve") && killer == null) {
				plugin.debugmsg("PVE Kill - No heads", null);
				return;
			}
			if (!plugin.getConfig().getBoolean("head.pvp") && killer != null) {
				plugin.debugmsg("PVP Kill - No heads", null);
				return;
			}

			String name = player.getName();
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			meta.setOwner(name);
			meta.setDisplayName(Deathcraft.DisplayName(player) + "'s Head");
			head.setItemMeta(meta);
			event.getDrops().add(head);

			if (plugin.getConfig().getBoolean("announce")) {
				String msg = "";
				if (killer == null) {
					msg = plugin.getConfig().getString("head.announce.pve");
					name = Deathcraft.DisplayName(player);
					msg = msg.replaceAll("%1", Deathcraft.DisplayName(player));
					Deathcraft.colorchat(msg, false, false);

				} else {
					msg = plugin.getConfig().getString("head.announce.pvp");
					msg = msg.replaceAll("%1", Deathcraft.DisplayName(player));
					msg = msg.replaceAll("%2", Deathcraft.DisplayName(killer));
					Deathcraft.colorchat(msg, true, false);

				}
				
				
			}
			break;

		}
	}

	@EventHandler
	// (priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {

		if ( plugin.getConfig().getBoolean("deathmessages.enableMechanic") == true )
		{
			event.setDeathMessage(null);
			DeathMessage(event.getEntity());
		}
		
		int keepXp = CheckXp(event.getEntity());
		if (keepXp != 0) {
			plugin.debugmsg("Checking exp keeping ...", null);
			int xp = ( event.getEntity().getLevel() * keepXp ) / 100;
			plugin.debugmsg("Keeping " + keepXp + " - totals to keep: " + xp, null);
			event.setNewLevel(xp);
		}

		CheckDeathChest(event, pvp);

	}
	
	public void CheckHead ( EntityDeathEvent event, double bonus ) {
		double regdrop = 0;
		int chance = rand.nextInt(100);
		SkullType skull = null;
		String type = "";

		  if(plugin.getConfig().getBoolean("head.enabled") == false )
			  return;

		switch (event.getEntityType()) {
		default: 
              /*    
              MobSkullType type2 = MobSkullType.getFromEntityType(event.getEntityType());
              String mobName = null;
              if(type != null)
                  mobName = type2.getPlayerName();
              if(CraftBookPlugin.inst().getConfiguration().headDropsCustomSkins.containsKey(typeName))
                  mobName = CraftBookPlugin.inst().getConfiguration().headDropsCustomSkins.get(typeName);
              if(mobName == null || mobName.isEmpty())
                  break;
              toDrop = new ItemStack(Material.SKULL, 1, (short)3);
              toDrop.setData(new MaterialData(Material.SKULL,(byte)3));
              SkullMeta itemMeta = (SkullMeta) toDrop.getItemMeta();
              itemMeta.setDisplayName(ChatColor.RESET + typeName + " Head");
              itemMeta.setOwner(mobName);
              toDrop.setItemMeta(itemMeta);
              return;
              
              
			skull = SkullType.PLAYER;
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			meta.setOwner( "DCMOB"+MobSkull(event.getEntityType() ) );
			String name = AorAn( event.getEntityType().toString().toLowerCase() ) + " "+ event.getEntityType().toString().toLowerCase() + " head";
			meta.setDisplayName( name.replace("_", " ") );
			head.setItemMeta(meta);
			regdrop = plugin.getConfig().getDouble("head.drop."+ event.getEntityType().toString().toLowerCase())*bonus;
			plugin.debugmsg("Chances: " + regdrop, null);
			if ( chance >= regdrop ) { return; }
			
			event.getDrops().add( head );
			*/
			return;
			
		case CREEPER: skull = SkullType.CREEPER; type = "creeper"; break;
		case ZOMBIE: skull = SkullType.ZOMBIE; type = "zombie"; break;
		case SKELETON: // Wither, or regular?
			if (((Skeleton) event.getEntity()).getSkeletonType() == Skeleton.SkeletonType.NORMAL) {
				skull = SkullType.SKELETON;
				type = "skeleton";
			} 
			else if (((Skeleton) event.getEntity()).getSkeletonType() == Skeleton.SkeletonType.WITHER) { //Wither Heads drop normally - we need to remove any that might be dropping!
			
				skull = SkullType.WITHER;
				type = "wither";
				for (Iterator<ItemStack> a = event.getDrops().iterator(); a
						.hasNext();) {
					if (a.next().getType() == Material.SKULL_ITEM) {
						a.remove();
					}
				}
			}
			break;
		}
		
		if ( skull == null ) return; // Non-Skulled kill
		
		regdrop = plugin.getConfig().getDouble("head.drop."+ type)*bonus;
		
		if ( chance >= regdrop ) { return; }
		
		event.getDrops().add( new ItemStack(Material.SKULL_ITEM,1, (short) skull.ordinal() ));
		
		
		
	}

	@SuppressWarnings("unchecked")
	public void DeathMessage(Player player) {
		String msg = "%1 has died.";
		String name = "";
		String killer = "";
		String hand = "bare hands";
		Player k = null;
		Random random = new Random();
		ArrayList<String> Messages = new ArrayList<String>();
		// Player player = (Player) event.getEntity();
		DamageCause cause;
		Entity damager = null;
		pvp = false;
		EntityDamageEvent lastDamageEvent = null; 

		 if ( player.getLastDamageCause() == null || player.getLastDamageCause().getCause() == null )
			 cause = DamageCause.MAGIC;
		 else
		 {
			 lastDamageEvent = player.getLastDamageCause();
			 cause = lastDamageEvent.getCause();
		 }


		if (System.currentTimeMillis() < Deathcraft.LastDied + 50) {
			plugin.debugmsg(
							"Player died a few seconds ago - likely a duplicate message. Cancelling DeathCraft",
							player);
			return;
		}

		if (lastDamageEvent instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent lastDamageByEntityEvent = (EntityDamageByEntityEvent) lastDamageEvent;
			damager = lastDamageByEntityEvent.getDamager();
		}

		if (cause.equals(DamageCause.ENTITY_EXPLOSION)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.tnt
					.clone();
		} else if (cause.equals(DamageCause.CONTACT)) {
			if (lastDamageEvent instanceof EntityDamageByBlockEvent) {
				EntityDamageByBlockEvent lastDamageByBlockEvent = (EntityDamageByBlockEvent) lastDamageEvent;
				Block damagerb = lastDamageByBlockEvent.getDamager();
				if (damagerb.getType() == Material.CACTUS) {
					Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.cactus
							.clone();
				}
			}
		} else if (cause.equals(DamageCause.LAVA)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.lava
					.clone();
		} else if (cause.equals(DamageCause.VOID)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.fallvoid
					.clone();
		} else if (cause.equals(DamageCause.WITHER)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.wither
					.clone();
		} else if (cause.equals(DamageCause.BLOCK_EXPLOSION)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.tnt
					.clone();
		} else if (cause.equals(DamageCause.FIRE)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.fire
					.clone();
		} else if (cause.equals(DamageCause.FIRE_TICK)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.fire
					.clone();
		} else if (cause.equals(DamageCause.SUFFOCATION)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.suffocate
					.clone();
		} else if (cause.equals(DamageCause.DROWNING)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.drown
					.clone();
		} else if (cause.equals(DamageCause.STARVATION)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.starve
					.clone();
		} else if (cause.equals(DamageCause.FALL)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.fall
					.clone();
		} else if (cause.equals(DamageCause.MAGIC)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.magic
					.clone();
		} else if (cause.equals(DamageCause.LIGHTNING)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.lightning
					.clone();
		} else if (cause.equals(DamageCause.SUICIDE)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.suicide
					.clone();
		} else if (cause.equals(DamageCause.FALLING_BLOCK)) {
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.fallingblock
					.clone();
		} else if (cause.equals(DamageCause.PROJECTILE)) {
			if (damager instanceof Fireball) {
				Fireball arrow = (Fireball) damager;
				if (arrow.getShooter() instanceof Player) {

					Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.fireballpvp
							.clone();
					k = (Player) arrow.getShooter();
					pvp = true;
				} else if ( arrow.getShooter() == null ) {
					if ( plugin.getConfig().getBoolean("general.Herobrine")) {
					killer = "Herobrine";
					Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.fireballmob.clone();
					}
					else {
						killer = "Someone";
						Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.fireball.clone();
									}
					
				} else {
					
					killer = arrow.getShooter().toString().substring(5);
					Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.fireballmob
							.clone();
				}
			} else if (damager instanceof Arrow) {
				Arrow arrow = (Arrow) damager;
				if (arrow.getShooter() instanceof Player) {

					Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.arrowpvp
							.clone();
					k = (Player) arrow.getShooter();
					pvp = true;

				} else if ( arrow.getShooter() == null ) {
					if ( plugin.getConfig().getBoolean("general.Herobrine")) {
					killer = "Herobrine";
					}
					else {
						killer = "Someone";
					}
					Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.arrowmob
							.clone();
					
				} else {
					killer = arrow.getShooter().toString().substring(5);
					Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.arrowmob
							.clone();
				}
			}
		} else if (cause.equals(DamageCause.ENTITY_ATTACK)) {

			if (damager instanceof Player) {
				pvp = true;
				k = (Player) damager;
				if (Deathcraft.instance.getConfig().contains(
						"CustomItem." + k.getItemInHand().getTypeId())) {
					Messages = (ArrayList<String>) Deathcraft.instance
							.getConfig().getStringList(
									"CustomItem."
											+ k.getItemInHand().getTypeId());
				} else {
					plugin.debugmsg("Item not found for custom list: "
							+ k.getItemInHand().getTypeId(), null);
					Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.pvp
							.clone();	
				}
				if ( k.getItemInHand() == null )
				{
					hand = "fists";
				}
				else if ( k.getItemInHand().getItemMeta().hasDisplayName() ) {
					hand = k.getItemInHand().getItemMeta().getDisplayName();
				}
				else {
					hand = k.getItemInHand().getType().name().toLowerCase().replaceAll("_", " ");
					
					
				}
					
			} else {

				//killer = damager.getType().toString();
				
				killer = damager.toString().substring(5);
				plugin.debugmsg("Killer is" + killer, null);
				if ( killer.contains("owner=")) {
					
					String ownername = "Someone";
					String killtype = "";
					
					killtype = killer.substring(0,4);
					killer = killer.substring(killer.lastIndexOf("name="));
					ownername = killer.substring(5,killer.indexOf("}"));
					
					
					plugin.debugmsg("Killtype: " + killtype,null);
					if ( killtype.equalsIgnoreCase("wolf")) {
						killer = "wolf";	
					}
					else if ( killtype.equalsIgnoreCase("mype")) { // Support for MyPet Plugin
						killer = killer.substring(killer.lastIndexOf("type=")+4);
						killer = killer.substring(1,killer.length() -1).toLowerCase();
					}
					else {
						killer = "pet";
					}
					
				
				
				k = Deathcraft.instance.getServer().getPlayer(ownername);
				killer = Deathcraft.DisplayName(k) + "'s pet " + killer;
				pvp = true;
				k = null;
				}
/*				
				if (damager.getType().toString() == "WOLF") {
					Wolf wolf = (Wolf) damager;
					if (wolf.getOwner() == null) {
						killer = "wild wolf";
					} else {
						Player a = (Player) wolf.getOwner();
						killer = Deathcraft.DisplayName(a) + "'s pet wolf";
						pvp = true;
					}
					

				}
*/
					Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.mob.clone();
			}

		} else {

			player.sendMessage("Unknown Cause of death: " + cause
					+ " - Please report this!");
			Messages = (ArrayList<String>) com.ivalicemud.deathcraft.Deathcraft.other
					.clone();
		}

		plugin.debugmsg("Cause of death: " + cause, player);
		plugin.debugmsg("Number of Messages: " + Messages.size(), player);
		msg = Messages.get(random.nextInt(Messages.size()));
		Deathcraft.LastDied = System.currentTimeMillis();
		

		if (k != null) {
			
			Deathcraft.log(player.getName() + " was PVP killed by "
					+ k.getName());
			killer = Deathcraft.DisplayName(k);
		} else if (killer.length() > 0) {
			Deathcraft.log(player.getName() + " was slain by " + killer);
		} else {
			Deathcraft.log(player.getName() + " died.");
		}

		// event.setDeathMessage(null);
		name = Deathcraft.DisplayName(player);
		msg = msg.replaceAll("%1", name);
		msg = msg.replaceAll("%2", killer);
		msg = msg.replaceAll("%3",hand);
		msg = msg.replaceAll("%4", player.getWorld().getName());
		msg = msg.replaceAll("%5", AorAn(killer));
		
		Deathcraft.colorchat(msg, pvp, false);
		Messages = null;
	}

	public String AorAn( String msg ) {
		if ( msg.trim().startsWith("[aeiou]")) return "an";

		return "a";
	}
	
	/*
	 * @EventHandler public void interactChest(InventoryClickEvent event ) { if
	 * ( !event.getWhoClicked().hasMetadata("dc.openX") ) return;
	 * 
	 * if ( event.getInventory().getType().toString().equalsIgnoreCase("chest")
	 * ) { plugin.debugmsg("" + event.getAction().toString(),null);
	 * 
	 * if ( event.getAction().toString().startsWith("PLACE")) { ((CommandSender)
	 * event.getWhoClicked()).sendMessage(
	 * "A magical force prevents you from adding items to this Death Chest!");
	 * event.setCancelled(true); return; }
	 * 
	 * 
	 * }
	 * 
	 * }
	 */
	
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

		 if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

	        if(event.getBlock().getType() == Material.SKULL) {
	        	return;
	        	/*
	            Skull skull = (Skull)event.getBlock().getState();
	            if(!skull.hasOwner())
	                return;
	            String playerName = ChatColor.stripColor(skull.getOwner());

	            EntityType type = MobSkullType.getEntityType(playerName);

	            ItemStack stack = new ItemStack(Material.SKULL, 1, (short)3);
	            stack.setData(new MaterialData(Material.SKULL, (byte)3));
	            SkullMeta meta = (SkullMeta) stack.getItemMeta();
	            meta.setOwner(playerName);

	 

	            if(type != null)
	                meta.setDisplayName(ChatColor.RESET + type.getName().replace("_", " ") + " Head");
	            else
	                meta.setDisplayName(ChatColor.RESET + playerName + "'s Head");

	            stack.setItemMeta(meta);

	 
	            event.setCancelled(true);
	            event.getBlock().setTypeId(0);
	            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), stack);
	            */
	        }
	        /*
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        if(event.getBlock().getType() == Material.SKULL) {

            Skull skull = (Skull)event.getBlock().getState();
            if(!skull.hasOwner())
                return;
            
            if ( skull.getOwner().startsWith("DCMOB")) //Deathcraft Mob
            {
        		SkullType skulltype = null;
        		String type = "";
        		
        			skulltype = SkullType.PLAYER;
        			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        			SkullMeta meta = (SkullMeta) head.getItemMeta();
        			String mob = skull.getOwner().substring(6);
        			meta.setOwner( "DCMOB"+mob );
        			String name = AorAn( mob.toLowerCase() ) + " "+ mob.toLowerCase() + " head";
        			meta.setDisplayName( name.replace("_", " ") );
        			head.setItemMeta(meta);
        			event.setCancelled(true);
                    event.getBlock().setTypeId(0);
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), head);
        			
        			return;
        			
        		
            }
            
            return;
            
         
        } */
    }

	
	@EventHandler
	public void closeChest(InventoryCloseEvent event )
	{
		if ( !event.getInventory().getType().toString().equalsIgnoreCase("chest") )
			return;

		if ( !event.getPlayer().hasMetadata("dc.openX") )
			return;
		
		plugin.debugmsg("" + event.getInventory().getType(), null);

        
        int count = 0;
       	Iterator<ItemStack> it = event.getInventory().iterator();
       	
       	while ( it.hasNext() )
       	{
       		ItemStack item = it.next();
       		if ( item != null && item.getType() != Material.AIR)
       		{
       			count++;
       			break;
       		}
        }
       	
        if ( count == 0 )
        {
            int x = ((MetadataValue)event.getPlayer().getMetadata("dc.openX").get(0)).asInt();
            int y = ((MetadataValue)event.getPlayer().getMetadata("dc.openY").get(0)).asInt();
            int z = ((MetadataValue)event.getPlayer().getMetadata("dc.openZ").get(0)).asInt();
            World w = event.getPlayer().getWorld();
            //Block newloc = w.getBlockAt(x, y, z);

            breakChest(x,y,z,w);
            
        }
        
		event.getPlayer().removeMetadata("dc.openX", plugin);
		event.getPlayer().removeMetadata("dc.openY", plugin);
		event.getPlayer().removeMetadata("dc.openZ", plugin);
	}
	
	public void breakChest(int x, int y, int z, World w )
	{
		if ( w.getBlockAt(x,y,z).getType() == Material.CHEST)
			w.getBlockAt(x, y, z).breakNaturally();
		
		if ( w.getBlockAt(x+1,y,z).getType() == Material.CHEST)
			w.getBlockAt(x+1, y, z).breakNaturally();

		if ( w.getBlockAt(x,y,z+1).getType() == Material.CHEST)
			w.getBlockAt(x, y, z+1).breakNaturally();

		if ( w.getBlockAt(x-1,y,z).getType() == Material.CHEST)
			w.getBlockAt(x-1, y, z).breakNaturally();

		if ( w.getBlockAt(x,y,z-1).getType() == Material.CHEST)
			w.getBlockAt(x, y, z-1).breakNaturally();

		if ( plugin.getChestConfig().contains(y + "." + x + "." + z))
			plugin.getChestConfig().set(y + "." + x + "." + z,null);

		if ( plugin.getChestConfig().contains(y + "." + (x+1) + "." + z))
			plugin.getChestConfig().set(y + "." + (x+1) + "." + z,null);

		if ( plugin.getChestConfig().contains(y + "." + (x-1) + "." + z))
			plugin.getChestConfig().set(y + "." + (x-1) + "." + z,null);

		if ( plugin.getChestConfig().contains(y + "." + x + "." + (z+1)))
			plugin.getChestConfig().set(y + "." + x + "." + (z+1),null);

		if ( plugin.getChestConfig().contains(y + "." + x + "." + (z-1)))
			plugin.getChestConfig().set(y + "." + x + "." + (z-1),null);

		
		plugin.saveChestConfig();
		

	}
	@EventHandler
	    public void onPlayerInteract(PlayerInteractEvent e){
		if ( e.getClickedBlock() == null )
			return;
		
		if ( e.getClickedBlock().getType() != Material.CHEST )
			return;
    	
    	if ( plugin.getConfig().getBoolean("chest.protect") == false )
    		return;

		int x = e.getClickedBlock().getX();
		int y = e.getClickedBlock().getY();
		int z = e.getClickedBlock().getZ();
		World w = e.getClickedBlock().getWorld();
		Block newloc;

		newloc = w.getBlockAt(x - 1, y, z);
		if ( newloc.getType() == Material.CHEST ) { x = x - 1; } 

		newloc = w.getBlockAt(x, y, z - 1);
		if ( newloc.getType() == Material.CHEST ) {z = z - 1; } 
		
		if ( !plugin.getChestConfig().contains(y + "." + x + "." + z + ".owner") ) 
		{
			plugin.debugmsg("Chest not found in file - not a death chest.", null);
			return;
		}
	    
		long timer = plugin.getChestConfig().getLong(y + "." + x + "." + z + ".time");
		String owner = plugin.getChestConfig().getString(y + "." + x + "." + z + ".owner");
		int decay = plugin.getConfig().getInt("chest.decayInMinutes");
		
		if ( timer + (decay * 60000) < System.currentTimeMillis() )
		{
			e.getPlayer().sendMessage("The chest decays into dust, spilling it's contents!");
			breakChest(x,y,z,w);
			e.setCancelled(true);
			return;
		}
		else if ( !owner.equals(e.getPlayer().getName()) && !e.getPlayer().hasPermission("deathcraft.admin"))
		{
			e.getPlayer().sendMessage("A strange force prevents you from touching that Death chest!");
			e.setCancelled(true);
		}
		else
		{
			if ( owner.equals(e.getPlayer().getName()) )
			{
				e.getPlayer().setMetadata("dc.openX", new FixedMetadataValue(plugin, x) );
				e.getPlayer().setMetadata("dc.openY", new FixedMetadataValue(plugin, y) );
				e.getPlayer().setMetadata("dc.openZ", new FixedMetadataValue(plugin, z) );
			}
			else
				e.getPlayer().sendMessage("Opening a DeathChest that does not belong to you ...");
			return;
		}
			
	}
	        
	public void saveChest(Chest chest, Player p )
	{
    	
    	

		int x = chest.getX();
		int y = chest.getY();
		int z = chest.getZ();
		World w = chest.getWorld();
		Block newloc;

		newloc = w.getBlockAt(x - 1, y, z);
		if ( newloc.getType() == Material.CHEST ) { x = x - 1; } 

		newloc = w.getBlockAt(x, y, z - 1);
		if ( newloc.getType() == Material.CHEST ) {z = z - 1; } 

		plugin.getChestConfig().set(y + "." + x + "." + z + ".owner", p.getName());
		plugin.getChestConfig().set(y + "." + x + "." + z + ".time", System.currentTimeMillis());
		plugin.saveChestConfig();		
	}
	
	
	public void CheckDeathChest(PlayerDeathEvent event, boolean ispvp) {
		if ( event.getDrops().size() == 0 )
			return;
		
		Player p = event.getEntity();
		int chestamt = 0;
		Location loc = p.getLocation();
		Block chestloc = p.getWorld().getBlockAt(loc.getBlockX(),
				loc.getBlockY(), loc.getBlockZ());
		Block chestloc2 = null;
		boolean found = false;
		boolean large = false;

		if (!ispvp && Deathcraft.PVEChest == false)
			return;
		if (ispvp && Deathcraft.PVPChest == false)
			return;

		for (ItemStack item : event.getDrops()) {
			if (item == null)
				continue;
			if (item.getType() == Material.CHEST)
				chestamt += 1;
			found = true;
		}

		if (p.hasPermission("deathcraft.chest.large.free")) {

			chestamt = 0;
			large = true;
			found = true;
		} else if (p.hasPermission("deathcraft.chest.large")
				&& chestamt >= 2) {
			chestamt = 2;
			large = true;
			found = true;

		} else if (p.hasPermission("deathcraft.chest.small.free")) {

			chestamt = 0;
			found = true;
		} else if (p.hasPermission("deathcraft.chest.small")
				&& chestamt >= 1) {

			chestamt = 1;
			found = true;
		} else {
			return;
		}

		if (found == false)
			return;

		chestloc = goodloc(chestloc);
		if (chestloc == null) {
			Deathcraft.log("Chest location could not be found for "
					+ p.getName());
			return;
		}

		if (large == true) {
			chestloc2 = goodloclarge(chestloc);
			if (chestloc2 == null) {
				large = false;
				return;
			}
		}

		chestloc.setType(Material.CHEST);
		if (large == true) {
			chestloc2.setType(Material.CHEST);
		}

		Chest chest = (Chest) chestloc.getState();
		Chest chest2 = null;
		if (large == true) {
			chest2 = (Chest) chestloc2.getState();
		}

		int maxslot = chest.getInventory().getSize();
		int slots = 0;
		if (large) {
			maxslot *= 2;
		}

		// Timer & Lock Chest
		saveChest(chest,p);
		
		for (Iterator<ItemStack> iter = event.getDrops().listIterator(); iter
				.hasNext();) {
			ItemStack item = iter.next();
			if (item == null)
				continue;

			if (chestamt > 0 && item.getType() == Material.CHEST) {
				if (item.getAmount() >= chestamt) {
					item.setAmount(item.getAmount() - chestamt);
					chestamt = 0;
				} else {
					chestamt -= item.getAmount();
					item.setAmount(0);
				}
				if (item.getAmount() == 0) {
					iter.remove();
					continue;
				}
			}

			if (slots < maxslot) {
				if (slots >= chest.getInventory().getSize()) {
					if (large == true) {
						break;
					} else {
						chest2.getInventory().setItem(
								slots % chest.getInventory().getSize(), item);
					}
				} else {
					chest.getInventory().setItem(slots, item);
				}

				iter.remove();
				slots++;
			} else if (chestamt == 0)
				break;
		}
	}

	public int CheckXp(Player p) {

		if (Deathcraft.CheckXp == false)
			return 0;

		int num = 100;
		while (num > 0) {
			if (p.hasPermission("deathcraft.keepxp." + num)) {
					return num;
			}
			num -= 1;
		}

		return 0;
	}

	public Boolean replacable(Material m) {
		if (Deathcraft.instance.getConfig().contains(
				"chest.DestroyBlock." + m.getId())) {
			return true;
		}

		return false;
	}

	public Block goodloc(Block loc) {
		int x = loc.getX();
		int y = loc.getY();
		int z = loc.getZ();
		World w = loc.getWorld();
		Block newloc;

		if (replacable(loc.getType()))
			return loc;

		newloc = w.getBlockAt(x - 1, y, z);
		if (replacable(newloc.getType()))
			return newloc;
		newloc = w.getBlockAt(x + 1, y, z);
		if (replacable(newloc.getType()))
			return newloc;
		newloc = w.getBlockAt(x, y, z + 1);
		if (replacable(newloc.getType()))
			return newloc;
		newloc = w.getBlockAt(x, y, z - 1);
		if (replacable(newloc.getType()))
			return newloc;

		return null;
	}

	public Block goodloclarge(Block loc) {
		int x = loc.getX();
		int y = loc.getY();
		int z = loc.getZ();
		World w = loc.getWorld();
		Block newloc;

		newloc = w.getBlockAt(x - 1, y, z);
		if (replacable(newloc.getType()))
			return newloc;
		newloc = w.getBlockAt(x + 1, y, z);
		if (replacable(newloc.getType()))
			return newloc;
		newloc = w.getBlockAt(x, y, z + 1);
		if (replacable(newloc.getType()))
			return newloc;
		newloc = w.getBlockAt(x, y, z - 1);
		if (replacable(newloc.getType()))
			return newloc;

		return null;
	}

	 private enum MobSkullType {

	        //Official
	        BLAZE("MHF_Blaze", "Blaze_Head"),
	        CAVE_SPIDER("MHF_CaveSpider"),
	        CHICKEN("MHF_Chicken", "scraftbrothers1"),
	        COW("MHF_Cow", "VerifiedBernard", "CarlosTheCow"),
	        ENDERMAN("MHF_Enderman", "Violit"),
	        GHAST("MHF_Ghast", "_QuBra_"),
	        MAGMA_CUBE("MHF_LavaSlime"),
	        MUSHROOM_COW("MHF_MushroomCow", "Mooshroom_Stew"),
	        PIG("MHF_Pig", "XlexerX"),
	        PIG_ZOMBIE("MHF_PigZombie", "ManBearPigZombie", "scraftbrothers5"),
	        SHEEP("MHF_Sheep", "SGT_KICYORASS", "Eagle_Peak"),
	        SLIME("MHF_Slime", "HappyHappyMan"),
	        SPIDER("MHF_Spider", "Kelevra_V"),
	        VILLAGER("MHF_Villager", "Villager", "Kuvase", "scraftbrothers9"),
	        IRON_GOLEM("MHF_Golem", "zippie007"),

	        //Unofficial/Community
	        BAT("coolwhip101", "bozzobrain"),
	        ENDER_DRAGON("KingEndermen", "KingEnderman"),
	        SQUID("squidette8"),
	        SILVERFISH("AlexVMiner"),
	        SNOWMAN("scraftbrothers2", "Koebasti"),
	        HORSE("gavertoso"),
	        OCELOT("scraftbrothers3"),
	        WITCH("scrafbrothers4");

	        MobSkullType(String playerName, String ... oldNames) {

	            this.playerName = playerName;
	            this.oldNames = new ArrayList<String>(Arrays.asList(oldNames));
	        }

	        private String playerName;
	        private List<String> oldNames;

	        public String getPlayerName() {

	            return playerName;
	        }

	        public boolean isOldName(String name) {

	            return oldNames.contains(name);
	        }

	        public static MobSkullType getFromEntityType(EntityType entType) {

	            try {
	                return MobSkullType.valueOf(entType.name());
	            } catch(Exception e){
	                return null;
	            }
	        }

	        public static EntityType getEntityType(String name) {

	            for(MobSkullType type : values())
	                if(type.getPlayerName().equalsIgnoreCase(name) || type.isOldName(name) ) 
	                    return EntityType.valueOf(type.name());

	            return null;
	        }
	    }
	public String MobSkull( EntityType e ) {

			if ( e.name().equalsIgnoreCase("BLAZE")) return  "MHF_Blaze";
			if ( e.name().equalsIgnoreCase("CAVE_SPIDER")) return  "MHF_CaveSpider";
			if ( e.name().equalsIgnoreCase("CHICKEN")) return  "MHF_Chicken";
			if ( e.name().equalsIgnoreCase("COW")) return  "MHF_Cow";
			if ( e.name().equalsIgnoreCase("ENDERMAN")) return  "MHF_Enderman";
			if ( e.name().equalsIgnoreCase("GHAST")) return  "MHF_Ghast";
			if ( e.name().equalsIgnoreCase("MAGMA_CUBE")) return  "MHF_LavaSlime";
			if ( e.name().equalsIgnoreCase("MUSHROOM_COW")) return  "MHF_MushroomCow";
			if ( e.name().equalsIgnoreCase("PIG")) return  "MHF_Pig";
			if ( e.name().equalsIgnoreCase("PIG_ZOMBIE")) return  "MHF_PigZombie";
			if ( e.name().equalsIgnoreCase("SHEEP")) return  "MHF_Sheep";
			if ( e.name().equalsIgnoreCase("SLIME")) return  "MHF_Slime";
			if ( e.name().equalsIgnoreCase("SPIDER")) return  "MHF_Spider";
			if ( e.name().equalsIgnoreCase("VILLAGER")) return  "MHF_Villager";
			if ( e.name().equalsIgnoreCase("IRON_GOLEM")) return  "MHF_Golem";
			if ( e.name().equalsIgnoreCase("SQUID")) return  "MHF_Squid";
			if ( e.name().equalsIgnoreCase("OCELOT")) return  "MHF_Ocelot";
			if ( e.name().equalsIgnoreCase("BAT")) return "bozzobrain";
			if ( e.name().equalsIgnoreCase("ENDER_DRAGON")) return "KingEndermen";
			if ( e.name().equalsIgnoreCase("SILVERFISH")) return "AlexVMiner";
			if ( e.name().equalsIgnoreCase("SNOWMAN")) return "scraftbrothers2";
			if ( e.name().equalsIgnoreCase("HORSE")) return "gavertoso";
			if ( e.name().equalsIgnoreCase("WITCH")) return "scrafbrothers4";
			
		return null;
	}
	
}
