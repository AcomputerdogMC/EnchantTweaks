package net.acomputerdog.enchanttweaks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

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
                //TODO remove lapis, if it becomes possible at some point

                enchanter.setLevel(enchanter.getLevel() - level);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("unenchant")) {
            if (sender.hasPermission("enchanttweaks.unenchant")) {
                if (sender instanceof Player) {
                    if (unEnchant((Player)sender)) {
                        sendMessage(sender, "Item unenchanted successfully.");
                    } else {
                        sendError(sender, "Unable to unenchant item, make sure you hold it in your main hand or off hand.");
                    }
                } else {
                    sendError(sender, "This command can only be run by a player!");
                }
            } else {
                sendError(sender, "You do not have permission!");
            }
        } else {
            sendError(sender, "Unknown command passed to SendAll!");
        }
        return true;
    }

    private void sendError(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + message);
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.AQUA + message);
    }

    private boolean unEnchant(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInMainHand();
        if (item == null) {
            item = inventory.getItemInOffHand();
        }
        if (item != null) {
            Map<Enchantment, Integer> enchantments = item.getEnchantments();
            for (Map.Entry<Enchantment, Integer> enchant : enchantments.entrySet()) { //loop through each enchantment
                ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);

                //enchanted books have to have enchantments stored through item meta
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta)book.getItemMeta();
                meta.addStoredEnchant(enchant.getKey(), enchant.getValue(), true);
                book.setItemMeta(meta);

                if (!inventory.addItem(book).isEmpty()) { //add item and check if it fit
                    player.getWorld().dropItem(player.getLocation(), book); //drop at feet if it didn't
                }

                item.removeEnchantment(enchant.getKey()); //remove the enchantment
            }
            return true;
        }
        return false;
    }
}
