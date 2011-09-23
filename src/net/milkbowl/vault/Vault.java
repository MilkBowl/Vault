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

import java.util.Collection;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.plugins.Economy_3co;
import net.milkbowl.vault.economy.plugins.Economy_BOSE;
import net.milkbowl.vault.economy.plugins.Economy_Essentials;
import net.milkbowl.vault.economy.plugins.Economy_MultiCurrency;
import net.milkbowl.vault.economy.plugins.Economy_iConomy4;
import net.milkbowl.vault.economy.plugins.Economy_iConomy5;
import net.milkbowl.vault.economy.plugins.Economy_iConomy6;
import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.permission.plugins.Permission_GroupManager;
import net.milkbowl.vault.permission.plugins.Permission_Permissions2;
import net.milkbowl.vault.permission.plugins.Permission_Permissions3;
import net.milkbowl.vault.permission.plugins.Permission_PermissionsBukkit;
import net.milkbowl.vault.permission.plugins.Permission_PermissionsEx;
import net.milkbowl.vault.permission.plugins.Permission_SuperPerms;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class Vault extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onDisable() {
        // Remove all Service Registrations
        getServer().getServicesManager().unregisterAll(this);

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
        // Try to load MultiCurrency
        if (packageExists(new String[] { "me.ashtheking.currency.Currency", "me.ashtheking.currency.CurrencyList" })) {
            Economy econ = new Economy_MultiCurrency(this);
            getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, econ, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] MultiCurrency found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] MultiCurrency not found.", getDescription().getName()));
        }
        
        // Try to load 3co
        if (packageExists(new String[] { "me.ic3d.eco.ECO" })) {
            Economy econ = new Economy_3co(this);
            getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, econ, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] 3co found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] 3co not found.", getDescription().getName()));
        }

        // Try to load BOSEconomy
        if (packageExists(new String[] { "cosine.boseconomy.BOSEconomy" })) {
            Economy bose = new Economy_BOSE(this);
            getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, bose, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] BOSEconomy found: %s", getDescription().getName(), bose.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] BOSEconomy not found.", getDescription().getName()));
        }

        // Try to load Essentials Economy
        if (packageExists(new String[] { "com.earth2me.essentials.api.Economy", "com.earth2me.essentials.api.NoLoanPermittedException", "com.earth2me.essentials.api.UserDoesNotExistException" })) {
            Economy essentials = new Economy_Essentials(this);
            getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, essentials, this, ServicePriority.Low);
            log.info(String.format("[%s][Economy] Essentials Economy found: %s", getDescription().getName(), essentials.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] Essentials Economy not found.", getDescription().getName()));
        }

        // Try to load iConomy 4
        if (packageExists(new String[] { "com.nijiko.coelho.iConomy.iConomy", "com.nijiko.coelho.iConomy.system.Account" })) {
            Economy icon4 = new Economy_iConomy4(this);
            getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, icon4, this, ServicePriority.High);
            log.info(String.format("[%s][Economy] iConomy 4 found: ", getDescription().getName(), icon4.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] iConomy 4 not found.", getDescription().getName()));
        }

        // Try to load iConomy 5
        if (packageExists(new String[] { "com.iConomy.iConomy", "com.iConomy.system.Account", "com.iConomy.system.Holdings" })) {
            Economy icon5 = new Economy_iConomy5(this);
            getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, icon5, this, ServicePriority.High);
            log.info(String.format("[%s][Economy] iConomy 5 found: %s", getDescription().getName(), icon5.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] iConomy 5 not found.", getDescription().getName()));
        }

        // Try to load iConomy 6
        if (packageExists(new String[] { "com.iCo6.iConomy" })) {
            Economy icon6 = new Economy_iConomy6(this);
            getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, icon6, this, ServicePriority.High);
            log.info(String.format("[%s][Economy] iConomy 6 found: %s", getDescription().getName(), icon6.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] iConomy 6 not found.", getDescription().getName()));
        }
    }

    /**
     * Attempts to load Permission Addons
     */
    private void loadPermission() {
        // Try to load PermissionsEx
        if (packageExists(new String[] { "ru.tehkode.permissions.bukkit.PermissionsEx" })) {
            Permission ePerms = new Permission_PermissionsEx(this);
            getServer().getServicesManager().register(net.milkbowl.vault.permission.Permission.class, ePerms, this, ServicePriority.High);
            log.info(String.format("[%s][Permission] PermissionsEx found: %s", getDescription().getName(), ePerms.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Permission] PermissionsEx not found.", getDescription().getName()));
        }
        
        //Try loading PermissionsBukkit
        if (packageExists(new String[] {"com.platymuus.bukkit.permissions.PermissionsPlugin"} )) {
        	Permission pPerms = new Permission_PermissionsBukkit(this);
        	getServer().getServicesManager().register(net.milkbowl.vault.permission.Permission.class, pPerms, this, ServicePriority.High);
        	log.info(String.format("[%s][Permission] PermissionsBukkit found: %s", getDescription().getName(), pPerms.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Permission] PermissionsBukkit not found.", getDescription().getName()));
        }
        // Try to load GroupManager
        if (packageExists(new String[] { "org.anjocaido.groupmanager.GroupManager" })) {
            Permission gPerms = new Permission_GroupManager(this);
            getServer().getServicesManager().register(net.milkbowl.vault.permission.Permission.class, gPerms, this, ServicePriority.High);
            log.info(String.format("[%s][Permission] GroupManager found: %s", getDescription().getName(), gPerms.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Permission] GroupManager not found.", getDescription().getName()));
        }

        // Try to load Permissions 3 (Yeti)
        if (packageExists(new String[] { "com.nijiko.permissions.ModularControl" })) {
            Permission nPerms = new Permission_Permissions3(this);
            getServer().getServicesManager().register(net.milkbowl.vault.permission.Permission.class, nPerms, this, ServicePriority.Normal);
            log.info(String.format("[%s][Permission] Permissions 3 (Yeti) found: %s", getDescription().getName(), nPerms.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Permission] Permissions 3 (Yeti) not found.", getDescription().getName()));
        }

        // Try to load Permissions 2 (Phoenix)
        if (packageExists(new String[] { "com.nijiko.permissions.Control" })) {
            Permission oPerms = new Permission_Permissions2(this);
            getServer().getServicesManager().register(net.milkbowl.vault.permission.Permission.class, oPerms, this, ServicePriority.Low);
            log.info(String.format("[%s][Permission] Permissions 2 (Phoenix) found: %s", getDescription().getName(), oPerms.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Permission] Permissions 2 (Phoenix) not found.", getDescription().getName()));
        }
        
        Permission perms = new Permission_SuperPerms(this);
        getServer().getServicesManager().register(net.milkbowl.vault.permission.Permission.class, perms, this, ServicePriority.Lowest);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            // Check if Player
            // If so, ignore command if player is not Op
            Player p = (Player) sender;
            if (!p.isOp()) {
                return true;
            }
        } else if (!(sender instanceof ConsoleCommandSender)) {
            // Check if NOT console
            // Ignore it if not originated from Console!
            return true;
        }

        if (command.getLabel().equals("vault-info")) {

            // Get String of Registered Economy Services
            String registeredEcons = null;
            Collection<RegisteredServiceProvider<Economy>> econs = this.getServer().getServicesManager().getRegistrations(net.milkbowl.vault.economy.Economy.class);
            for (RegisteredServiceProvider<Economy> econ : econs) {
                Economy e = econ.getProvider();
                if (registeredEcons == null) {
                    registeredEcons = e.getName();
                } else {
                    registeredEcons += ", " + e.getName();
                }
            }

            // Get String of Registered Permission Services
            String registeredPerms = null;
            Collection<RegisteredServiceProvider<Permission>> perms = this.getServer().getServicesManager().getRegistrations(net.milkbowl.vault.permission.Permission.class);
            for (RegisteredServiceProvider<Permission> perm : perms) {
                Permission p = perm.getProvider();
                if (registeredPerms == null) {
                    registeredPerms = p.getName();
                } else {
                    registeredPerms += ", " + p.getName();
                }
            }

            // Get Economy & Permission primary Services
            Economy econ = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
            Permission perm = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class).getProvider();

            // Send user some info!
            sender.sendMessage(String.format("[%s] Vault v%s Information", getDescription().getName(), getDescription().getVersion()));
            sender.sendMessage(String.format("[%s] Economy: %s [%s]", getDescription().getName(), econ.getName(), registeredEcons));
            sender.sendMessage(String.format("[%s] Permission: %s [%s]", getDescription().getName(), perm.getName(), registeredPerms));
            return true;
        } else {
            // Show help
            sender.sendMessage("Vault Commands:");
            sender.sendMessage("  /vault-info - Displays information about Vault");
            return true;
        }
    }

    /**
     * Determines if all packages in a String array are within the Classpath
     * This is the best way to determine if a specific plugin exists and will be
     * loaded. If the plugin package isn't loaded, we shouldn't bother waiting
     * for it!
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
