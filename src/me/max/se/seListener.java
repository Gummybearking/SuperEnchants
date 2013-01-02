package me.max.se;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_4_6.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import com.griefcraft.lwc.LWCPlugin;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class seListener implements Listener{
	final static Enchantment PROTECTION = Enchantment.PROTECTION_ENVIRONMENTAL;
    final static Enchantment FIRE_PROTECTION = Enchantment.PROTECTION_FIRE;
    final static Enchantment FEATHER_FALLING = Enchantment.PROTECTION_FALL;
    final static Enchantment BLAST_PROTECTION = Enchantment.PROTECTION_EXPLOSIONS;
    final static Enchantment PROJECTILE_PROTECTION = Enchantment.PROTECTION_PROJECTILE;
    final static Enchantment RES = Enchantment.OXYGEN;
    final static Enchantment AQUA_AFFINITY = Enchantment.WATER_WORKER;
    final static Enchantment SHARPNESS = Enchantment.DAMAGE_ALL;
    final static Enchantment SMITE = Enchantment.DAMAGE_UNDEAD;
    final static Enchantment BANE = Enchantment.DAMAGE_ARTHROPODS;
    final static Enchantment KNOCKBACK = Enchantment.KNOCKBACK;
    final static Enchantment FIRE_ASPECT = Enchantment.FIRE_ASPECT;
    final static Enchantment LOOTING = Enchantment.LOOT_BONUS_MOBS;
    final static Enchantment EFFICIENCY = Enchantment.DIG_SPEED;
    final static Enchantment SILK_TOUCH = Enchantment.SILK_TOUCH;
    final static Enchantment UNBREAKING = Enchantment.DURABILITY;
    final static Enchantment FORTUNE = Enchantment.LOOT_BONUS_BLOCKS;
    final static Enchantment POWER = Enchantment.ARROW_DAMAGE;
    final static Enchantment PUNCH = Enchantment.ARROW_KNOCKBACK;
    final static Enchantment FLAME = Enchantment.ARROW_FIRE;
    final static Enchantment INFINITE = Enchantment.ARROW_INFINITE;
    public SuperEnchants plugin;
	public static Map<UUID, ArrayList<Block>> tntmap = new HashMap<UUID, ArrayList<Block>>();

	public seListener(SuperEnchants p) {
	    plugin = p;
	}
	@EventHandler  
	public void onPlayerHit(EntityDamageByEntityEvent e){
		Entity en = e.getEntity();
		Entity da = e.getDamager(); 
		if(en instanceof Player && da instanceof Player){
			Player p = (Player)en;
			Player d = (Player)da;
			ItemStack hand = d.getItemInHand();
			if(hand.getType() == Material.DIAMOND_SWORD ||hand.getType() == Material.GOLD_SWORD || hand.getType() == Material.IRON_SWORD || hand.getType() == Material.WOOD_SWORD ){
				if(SuperEnchants.hasEnch(hand, RES, p)){
					if(SuperEnchants.getLevel(hand, RES, p) == 1){
						p.kickPlayer("You have been kicked!");
    	                d.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "SuperEnchants" + ChatColor.RED + "]" + ChatColor.YELLOW + " Player kicked, Good Ridance!");

						return;	
					}
					if(SuperEnchants.getLevel(hand, RES, p) == 2){
						p.setBanned(true);
    	                d.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "SuperEnchants" + ChatColor.RED + "]" + ChatColor.YELLOW + " Player banned, Good Ridance!");
						return;	
					}
					
				}
			}
		}
	}
    @EventHandler
	public void PlayerRightClick(final PlayerInteractEvent e){
    	
    	if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)){
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
				Player player = e.getPlayer();
				ItemStack item = player.getItemInHand();
				if((item.getType() == Material.DIAMOND_HOE || item.getType() == Material.GOLD_HOE || item.getType() == Material.IRON_HOE || item.getType() == Material.WOOD_HOE)&& SuperEnchants.hasEnch(item, AQUA_AFFINITY, player)){
					Block b = e.getClickedBlock();
					if(b.getTypeId() == 60 && canBuild(player, b)){
						damage(item, 2, player);
						b.setData((byte)8);
					}else{

					}
				}
				
			}
			final Player player = e.getPlayer();
			debug(player, "RightClicked");
			final ItemStack item = player.getItemInHand();
			if(item.getType() == Material.FLINT_AND_STEEL){
				debug(player, "IsFlintAndSteel");
				if(SuperEnchants.hasEnch(item, POWER, player)){
					debug(player, "HasPower");
					final Location loc = player.getLocation().add(0, 2, 0);
					final World world = player.getWorld();
					if(canBuildHere(player,player.getLocation()) && canBuildHere(player,player.getTargetBlock(null, 100))){
						debug(player, "CanBuildHere");
						if(!SuperEnchants.tntdelay.containsKey(player)){
		                	debug(player, "InTNTDELAYHashmap");
							SuperEnchants.tntdelay.put(player, null);
		                	damage(item, player);
		                	TNTPrimed tnt = (TNTPrimed)world.spawn(loc, TNTPrimed.class);
		                	int n = SuperEnchants.getLevel(item, POWER, player);
		                	tntmap.put(tnt.getUniqueId(), null);
		                	tnt.setVelocity(player.getLocation().getDirection().normalize().multiply(n));
		                	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		            			   public void run() {
		            				  SuperEnchants.tntdelay.remove(player);
		             			      
		             		   }
		             		}, 100L);
		                }else{
	    	                player.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "SuperEnchants" + ChatColor.RED + "]" + ChatColor.YELLOW + " Your enchantment is recharging");

		                }
					}else{
    	                player.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "SuperEnchants" + ChatColor.RED + "]" + ChatColor.YELLOW + " Can't use here!");


					}

	                

	                //tnt.setFuseTicks(n * 20*2); // TODO: should we change?

	               
				}
			}	
		}else if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
			
			
			Block block = e.getClickedBlock();
			final Player player = e.getPlayer();
			ItemStack item = player.getItemInHand();
	    	if(item.getType() == Material.DIAMOND_PICKAXE || item.getType() == Material.IRON_PICKAXE || item.getType() == Material.GOLD_PICKAXE || item.getType() == Material.WOOD_PICKAXE){
	    		if(SuperEnchants.hasEnch(item, POWER, player)){
	    			int level = SuperEnchants.getLevel(item, POWER, player);
	                int dx = (int)Math.signum(block.getLocation().getX() - player.getLocation().getX());
	                int dy = (int)Math.signum(block.getLocation().getY() - player.getLocation().getY());
	                int dz = (int)Math.signum(block.getLocation().getZ() - player.getLocation().getZ());
	                for (int i = 0; i < level; i += 1) {
	                    // Note: this also works for bedrock!
	                    //plugin.log.info("break "+i);
	                    
	                    Block loca = block.getRelative(dx*i, dy*i, dz*i);
	                    if(canBuild(player, block)){
	                    	block.getRelative(dx*i, dy*i, dz*i).breakNaturally(item);
	                    }
	                }

	                damage(item,2, player);
	    		}
	    	}else if((item.getType() == Material.DIAMOND_HOE || item.getType() == Material.GOLD_HOE || item.getType() == Material.IRON_HOE || item.getType() == Material.WOOD_HOE)&& SuperEnchants.hasEnch(item, AQUA_AFFINITY, player)){
	    		if(canBuild(player, block)){
	    			if(!SuperEnchants.waterdelay.containsKey(player)){
	    				Block b = block.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ());
	    				if(b.getType() == Material.AIR){
	    					b.setType(Material.WATER);

			    			SuperEnchants.waterdelay.put(player, null);
	    				}
	    				

		    			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	         			   public void run() {
	         				  SuperEnchants.waterdelay.remove(player);
	          			      
	         			   }
		    			}, 100L);
	    			}else{
	    			}
	    		}else{

	    		}
	    	}
		}
	}
    @EventHandler
    public void ArrowShoot(ProjectileHitEvent event){
    	 if(event.getEntity() instanceof Arrow){
    		 Entity arrow = event.getEntity();
    		 Arrow a = (Arrow) event.getEntity();
    		 if(a.getShooter() instanceof Player){
    			 Player player = (Player)a.getShooter();
    				 ItemStack bow = player.getItemInHand();
    				 if (SuperEnchants.hasEnch(bow, LOOTING, player)) {
    		             double s = 5.0 * SuperEnchants.getLevel(bow, LOOTING, player);

    		             List<Entity> loots = arrow.getNearbyEntities(s, s, s);
    		             
    		             for (Entity loot: loots) {
    		                 // This moves everything!
    		                 if(loot instanceof Player){
        		            	 

    		                 }else{
    		                	 loot.teleport(player.getLocation());
    		                 }
    		             }
    	                player.sendMessage(ChatColor.RED + "[" + ChatColor.GOLD + "SuperEnchants" + ChatColor.RED + "]" + ChatColor.YELLOW + " Your arrow brought you a present!");

    		         }
    			 
    		 }
    	 }
    	
    }
    @EventHandler
    public void TntGoBoom(EntityDamageByEntityEvent e){
    	
    	Entity en = e.getEntity();
    	if(en instanceof Player){
    		debug(((Player) e.getEntity()), "InstanceofPlayer" );
    		Entity d = e.getDamager();
    		if(d instanceof TNTPrimed&& tntmap.containsKey(d.getUniqueId())){
        		debug(((Player) e.getEntity()), "Contained" );

    			tntmap.remove(d.getUniqueId());
    			Block b = en.getLocation().getBlock();
    			if(!TownyUniverse.isWilderness(b)){
    	    		debug(((Player) e.getEntity()), "Wilderness" );

    				e.setCancelled(true);
    			}
    		}
    	}
    }
	public static void damage(ItemStack tool, Player player) {
        damage(tool, 1, player);
    }
	 public static void damage(ItemStack tool, int amount, Player player) {
	       net.minecraft.server.v1_4_6.ItemStack nativeTool = CraftItemStack.asNMSCopy(tool);
	       net.minecraft.server.v1_4_6.EntityLiving nativeEntity = ((CraftPlayer)player).getHandle();

	       nativeTool.damage(amount, nativeEntity);

	       tool.setDurability((short)nativeTool.getData());



	       tool.setDurability((short)(tool.getDurability() + amount));

	       if (tool.getDurability() >= tool.getType().getMaxDurability()) {
	            // reached max, break
	           PlayerInventory inventory = player.getInventory();
	           if (inventory.getItemInHand().getType() == tool.getType()) {
	               inventory.clear(inventory.getHeldItemSlot());
	           } 
	       } 
	  }
	 public boolean canBuild(Player player, Block block){
		 if(canBuildHere(player, block) && !hasProtection(block)){
			 if(TownyUniverse.isWilderness(block)){
				 return true;
			 }else{
				 boolean bBuild = PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getTypeId(), block.getData(), TownyPermission.ActionType.BUILD);
				 if(bBuild){
					 return true;
				 }else{
					 return false;
				 }
			 }
			 
		 }else{
			 return false;
		 }
		
	 }
	 public boolean hasProtection(Block b) {
		 LWCPlugin p = (LWCPlugin)Bukkit.getServer().getPluginManager().getPlugin("LWC");
		 return p.getLWC().findProtection(b) != null;		 
	 }
	 public boolean canBuildHere(Player player, Location location) {
	        if (location == null) {
	            return true;
	        }

	        WorldGuardPlugin wg = getWorldGuard();
	        if (wg == null) {
	            return true;
	        }

	        return wg.canBuild(player, location);
	    }

	    public boolean canBuildHere(Player player, Block block) {
	        if (block == null) {
	            return true;
	        }

	        WorldGuardPlugin wg = getWorldGuard();
	        if (wg == null) {
	            return true;
	        }

	        return wg.canBuild(player, block);
	    }
	    public WorldGuardPlugin getWorldGuard() {
	        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
	        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	            return null;
	        }

	        return (WorldGuardPlugin)plugin;
	    }
	    public void debug(Player p, String s){
	    	boolean useDebugs = false;
	    	if(p.isOp() && useDebugs){
	    		p.sendMessage("debug [SE]: " + s);
	    	}
	    }
	    
}
