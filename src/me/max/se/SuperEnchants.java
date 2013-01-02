package me.max.se;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class SuperEnchants extends JavaPlugin {
	public final Logger logger = Logger.getLogger("Minecraft");
    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;
	public static Map<Player, ArrayList<Block>> tntdelay = new HashMap<Player, ArrayList<Block>>();
	public static Map<Player, ArrayList<Block>> waterdelay = new HashMap<Player, ArrayList<Block>>();

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    public void onEnable() {
    	PluginDescriptionFile pdffile = this.getDescription();
		this.logger.info(pdffile.getName() + " Has Been Enabled!");
		setupPermissions();
		setupEconomy();
		this.getServer().getPluginManager().registerEvents(new seListener(this), this);

    }
    static public boolean hasEnch(ItemStack tool, org.bukkit.enchantments.Enchantment ench, Player player) {
        if (tool == null) {
            return false;
        }

        
        // TODO: optional player permission support

        // TODO: instead, should we check for permissions on player pickup?? more lightweight. but what about chests? listen for inventory close too?

        //plugin.log.info("hasEnch "+tool.getTypeId()+" "+ench.getId());
        return tool.containsEnchantment(ench);
    }
    static public int getLevel(ItemStack tool, org.bukkit.enchantments.Enchantment ench, Player player) {
        return tool.getEnchantmentLevel(ench);
    }
    
}
