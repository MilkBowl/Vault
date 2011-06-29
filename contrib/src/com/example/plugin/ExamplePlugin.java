package com.example.plugin;

import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {
    
    private static final Logger log = Logger.getLogger("Minecraft");
    private Vault vault = null;

    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
        if(x != null & x instanceof Vault) {
            vault = (Vault) x;
            log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
        } else {
            /**
             * Throw error & disable because we have Vault set as a dependency, you could give a download link
             * or even download it for the user.  This is all up to you as a developer to decide the best option
             * for your users!  For our example, we assume that our audience (developers) can find the Vault
             * plugin and properly install it.  It's usually a bad idea however.
             */
            log.warning(String.format("[%s] Vault was _NOT_ found! Disabling plugin.", getDescription().getName()));
            getPluginLoader().disablePlugin(this);
        }
    }
    
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) {
            log.info("Only players are supported for this Example Plugin, but you should not do this!!!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if(command.getLabel().equals("test-economy")) {
            // Lets give the player 1.05 currency (note that SOME economic plugins require rounding!
            sender.sendMessage(String.format("You have %s", vault.getEconomy().format(vault.getEconomy().getBalance(player.getName()).amount)));
            EconomyResponse r = vault.getEconomy().depositPlayer(player.getName(), 1.05);
            if(r.transactionSuccess()) {
                sender.sendMessage(String.format("You were given %s and now have %s", vault.getEconomy().format(r.amount), vault.getEconomy().format(r.balance)));
            } else {
                sender.sendMessage(String.format("An error occured: %s", r.errorMessage));
            }
            return true;
        } else if(command.getLabel().equals("test-permission")) {
            // Lets test if user has the node "example.plugin.awesome" to determine if they are awesome or just suck
            if(vault.getPermission().hasPermission(player, "example.plugin.awesome", false)) {
                sender.sendMessage("You are awesome!");
            } else {
                sender.sendMessage("You suck!");
            }
            return true;
        } else {
            return false;
        }
    }
}