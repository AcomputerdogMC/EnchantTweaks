package net.acomputerdog.enchanttweaks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginEnchantTweaks extends JavaPlugin implements Listener {

    private static final int[] xpLevels = {1, 5, 10};
    private static final int[] bottleCounts = {8, 16, 32};

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlaceInEnchantingTable(PrepareItemEnchantEvent e) {
        ItemStack item = e.getItem();
        if (item.getType() == Material.GLASS_BOTTLE) {
            for (int i = 0; i < 3; i++) {
                e.getExpLevelCostsOffered()[i] = xpLevels[i]; //add 1, 5, and 10 as XP levels
            }
            e.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEnchant(EnchantItemEvent e) {
        ItemStack item = e.getItem();
        if (item.getType() == Material.GLASS_BOTTLE) {
            Player enchanter = e.getEnchanter();
            int level = xpLevels[e.whichButton()];
            if (enchanter.getLevel() >= level) {
                e.getItem().setType(Material.EXP_BOTTLE); //convert from glass to EXP bottles
                e.getItem().setAmount(bottleCounts[e.whichButton()]); //set the amount of bottles

                //e.setExpLevelCost(xpLevels[e.whichButton()]); //set XP level to actually use the suggested amount
                enchanter.setLevel(enchanter.getLevel() - level);
                //e.setCancelled(false);
            }
        }
    }
}
