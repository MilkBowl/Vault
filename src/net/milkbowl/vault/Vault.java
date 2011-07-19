/**
 * Copyright (C) 2011 Morgan Humes <morgan@lanaddict.com>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

package net.milkbowl.vault;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.plugins.Economy_3co;
import net.milkbowl.vault.economy.plugins.Economy_BOSE;
import net.milkbowl.vault.economy.plugins.Economy_Essentials;
import net.milkbowl.vault.economy.plugins.Economy_iConomy4;
import net.milkbowl.vault.economy.plugins.Economy_iConomy5;
import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.permission.plugins.Permission_Permissions;
import net.milkbowl.vault.permission.plugins.Permission_PermissionsEx;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class Vault extends JavaPlugin {
    
    private static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        // Load Vault Addons
        loadEconomy();
        loadPermission();
        
        getCommand("vault-info").setExecutor(this);
        getCommand("vault-reload").setExecutor(this);
        log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    /**
     * Attempts to load Economy Addons
     */
    private void loadEconomy() {
        // Try to load 3co
        if(packageExists(new String[] { "me.ic3d.eco.ECO" })) {
            Economy econ = new Economy_3co(this);
            this.getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, econ, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] 3co found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] 3co not found.", getDescription().getName()));
        }
        
        // Try to load BOSEconomy
        if (packageExists(new String[] { "cosine.boseconomy.BOSEconomy" })) {
            Economy bose = new Economy_BOSE(this);
            this.getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, bose, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] BOSEconomy found: %s", getDescription().getName(), bose.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] BOSEconomy not found.", getDescription().getName()));
        }

        // Try to load Essentials Economy
        if (packageExists(new String[] { "com.earth2me.essentials.api.Economy", "com.earth2me.essentials.api.NoLoanPermittedException", "com.earth2me.essentials.api.UserDoesNotExistException" })) {
            Economy essentials = new Economy_Essentials(this);
            this.getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, essentials, this, ServicePriority.Low);
            log.info(String.format("[%s][Economy] Essentials Economy found: %s", getDescription().getName(), essentials.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] Essentials Economy not found.", getDescription().getName()));
        }

        // Try to load iConomy 4
        if (packageExists(new String[] { "com.nijiko.coelho.iConomy.iConomy", "com.nijiko.coelho.iConomy.system.Account" })) {
            Economy icon4 = new Economy_iConomy4(this);
            this.getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, icon4, this, ServicePriority.Lowest);
            log.info(String.format("[%s][Economy] iConomy 4 found: ", getDescription().getName(), icon4.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] iConomy 4 not found.", getDescription().getName()));
        }

        // Try to load iConomy 5
        if (packageExists(new String[] { "com.iConomy.iConomy", "com.iConomy.system.Account", "com.iConomy.system.Holdings" })) {
            Economy icon5 = new Economy_iConomy5(this);
            this.getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, icon5, this, ServicePriority.Lowest);
            log.info(String.format("[%s][Economy] iConomy 5 found: %s", getDescription().getName(), icon5.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] iConomy 5 not found.", getDescription().getName()));
        }
    }
    
    /**
     * Attempts to load Permission Addons
     */
    private void loadPermission() {
        // Try to load PermissionsEx
        if(packageExists(new String[] { "ru.tehkode.permissions.bukkit.PermissionsEx" })) {
            Permission ePerms = new Permission_PermissionsEx(this);
            this.getServer().getServicesManager().register(net.milkbowl.vault.permission.Permission.class, ePerms, this, ServicePriority.Normal);
            log.info(String.format("[%s][Permission] PermissionsEx found: %s", getDescription().getName(), ePerms.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Permission] PermissionsEx not found.", getDescription().getName()));
        }

        // Try to load Permissions (Phoenix)
        if (packageExists(new String[] { "com.nijikokun.bukkit.Permissions.Permissions" })) {
            Permission nPerms = new Permission_Permissions(this);
            this.getServer().getServicesManager().register(net.milkbowl.vault.permission.Permission.class, nPerms, this, ServicePriority.Lowest);
            log.info(String.format("[%s][Permission] Permissions (Yetti) found: %s", getDescription().getName(), nPerms.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Permission] Permissions (Yetti) not found.", getDescription().getName()));
        }
    }
    
    private void reload() {
        loadEconomy();
        
        loadPermission();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(sender instanceof Player) {
            // Check if Player
            // If so, ignore command if player is not Op
            Player p = (Player) sender;
            if(!p.isOp()) {
                return true;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            // Check if NOT console
            // Ignore it if not originated from Console!
            return true;
        }
        
        Economy econ = this.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
        Permission perm = this.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class).getProvider();
        
        if (command.getLabel().equals("vault-info")) {
            sender.sendMessage(String.format("[%s] Vault v%s Information", getDescription().getName(), getDescription().getVersion()));
            sender.sendMessage(String.format("[%s] Economy: %s", getDescription().getName(), econ.getName()));
            sender.sendMessage(String.format("[%s] Permission: %s", getDescription().getName(), perm.getName()));
            return true;
        } else if (command.getLabel().equals("vault-reload")) {
            reload();
            sender.sendMessage(String.format("[%s] Reloaded Addons", getDescription().getName()));
            return true;
        }
        
        // Show help
        sender.sendMessage("Vault Commands:");
        sender.sendMessage("  /vault info - Displays information about Vault");
        sender.sendMessage("  /vault reload - Reloads Addons");
        return true;
    }
    
    /**
     * Determines if all packages in a String array are within the Classpath
     * This is the best way to determine if a specific plugin exists and will be loaded.
     * If the plugin package isn't loaded, we shouldn't bother waiting for it!
     * @param packages String Array of package names to check
     * @return Success or Failure
     */
    private static boolean packageExists(String[] packages) {
        try {
            for (String pkg : packages) {
                Class.forName(pkg);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
