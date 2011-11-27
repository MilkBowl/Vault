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

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.chat.plugins.Chat_GroupManager;
import net.milkbowl.vault.chat.plugins.Chat_Permissions3;
import net.milkbowl.vault.chat.plugins.Chat_PermissionsEx;
import net.milkbowl.vault.chat.plugins.Chat_bPermissions;
import net.milkbowl.vault.chat.plugins.Chat_iChat;
import net.milkbowl.vault.chat.plugins.Chat_mChat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.plugins.Economy_3co;
import net.milkbowl.vault.economy.plugins.Economy_BOSE6;
import net.milkbowl.vault.economy.plugins.Economy_BOSE7;
import net.milkbowl.vault.economy.plugins.Economy_Essentials;
import net.milkbowl.vault.economy.plugins.Economy_MineConomy;
import net.milkbowl.vault.economy.plugins.Economy_MultiCurrency;
import net.milkbowl.vault.economy.plugins.Economy_eWallet;
import net.milkbowl.vault.economy.plugins.Economy_iConomy4;
import net.milkbowl.vault.economy.plugins.Economy_iConomy5;
import net.milkbowl.vault.economy.plugins.Economy_iConomy6;
import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.permission.plugins.Permission_GroupManager;
import net.milkbowl.vault.permission.plugins.Permission_Permissions3;
import net.milkbowl.vault.permission.plugins.Permission_PermissionsBukkit;
import net.milkbowl.vault.permission.plugins.Permission_PermissionsEx;
import net.milkbowl.vault.permission.plugins.Permission_SuperPerms;
import net.milkbowl.vault.permission.plugins.Permission_bPermissions;

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
		loadChat();

		getCommand("vault-info").setExecutor(this);
		getCommand("vault-reload").setExecutor(this);
		log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
	}

	/**
	 * Attempts to load Chat Addons
	 */
	private void loadChat() {
		Permission perms = getServer().getServicesManager().getRegistration(Permission.class).getProvider();
		// Try to load PermissionsEx
		if (packageExists(new String[] { "ru.tehkode.permissions.bukkit.PermissionsEx" })) {
			Chat eChat = new Chat_PermissionsEx(this, perms);
			getServer().getServicesManager().register(Chat.class, eChat, this, ServicePriority.Highest);
			log.info(String.format("[%s][Chat] PermissionsEx found: %s", getDescription().getName(), eChat.isEnabled() ? "Loaded" : "Waiting"));
		}

		//Try loading mChat
		if (packageExists(new String[] {"net.D3GN.MiracleM4n.mChat"} )) {
			Chat mChat = new Chat_mChat(this, perms);
			getServer().getServicesManager().register(Chat.class, mChat, this, ServicePriority.Highest);
			log.info(String.format("[%s][Chat] mChat found: %s", getDescription().getName(), mChat.isEnabled() ? "Loaded" : "Waiting"));
		}

		//try loading bPermissions
		if (packageExists(new String[] {"de.bananaco.permissions.worlds.WorldPermissionsManager"})) {
			Chat bPerms = new Chat_bPermissions(this, perms);
			getServer().getServicesManager().register(Chat.class, bPerms, this, ServicePriority.High);
			log.info(String.format("[%s][Chat] bPermissions found: %s", getDescription().getName(), bPerms.isEnabled() ? "Loaded" : "Waiting"));
		}

		// Try to load GroupManager
		if (packageExists(new String[] { "org.anjocaido.groupmanager.GroupManager" })) {
			Chat gPerms = new Chat_GroupManager(this, perms);
			getServer().getServicesManager().register(Chat.class, gPerms, this, ServicePriority.High);
			log.info(String.format("[%s][Chat] GroupManager found: %s", getDescription().getName(), gPerms.isEnabled() ? "Loaded" : "Waiting"));
		}

		// Try to load Permissions 3 (Yeti)
		if (packageExists(new String[] { "com.nijiko.permissions.ModularControl" })) {
			Chat nPerms = new Chat_Permissions3(this, perms);
			getServer().getServicesManager().register(Chat.class, nPerms, this, ServicePriority.High);
			log.info(String.format("[%s][Chat] Permissions 3 (Yeti) found: %s", getDescription().getName(), nPerms.isEnabled() ? "Loaded" : "Waiting"));
		}
		
		// Try to load iChat
		if (packageExists(new String[] { "net.TheDgtl.iChat.iChat" })) {
			Chat iChat = new Chat_iChat(this, perms);
			getServer().getServicesManager().register(Chat.class, iChat, this, ServicePriority.Lowest);
			log.info(String.format("[%s][Chat] PermissionsEx found: %s", getDescription().getName(), iChat.isEnabled() ? "Loaded" : "Waiting"));
		}
	}

	/**
	 * Attempts to load Economy Addons
	 */
	private void loadEconomy() {
		// Try to load MultiCurrency
		if (packageExists(new String[] { "me.ashtheking.currency.Currency", "me.ashtheking.currency.CurrencyList" })) {
			Economy econ = new Economy_MultiCurrency(this);
			getServer().getServicesManager().register(Economy.class, econ, this, ServicePriority.Normal);
			log.info(String.format("[%s][Economy] MultiCurrency found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
		}
		
		//Try Loading MineConomy
		if (packageExists(new String[] { "me.mjolnir.mineconomy.MineConomy" })) {
			Economy econ = new Economy_MineConomy(this);
			getServer().getServicesManager().register(Economy.class, econ, this, ServicePriority.Normal);
			log.info(String.format("[%s][Economy] MineConomy found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
			
		}
		
		//Try loading eWallet
		if (packageExists(new String[] { "me.ethan.eWallet.ECO" })) {
			Economy econ = new Economy_eWallet(this);
			getServer().getServicesManager().register(Economy.class, econ, this, ServicePriority.Normal);
			log.info(String.format("[%s][Economy] eWallet found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
		}
		
		// Try to load 3co
		if (packageExists(new String[] { "me.ic3d.eco.ECO" })) {
			Economy econ = new Economy_3co(this);
			getServer().getServicesManager().register(Economy.class, econ, this, ServicePriority.Normal);
			log.info(String.format("[%s][Economy] 3co found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
		}

		// Try to load BOSEconomy
		if (packageExists(new String[] { "cosine.boseconomy.BOSEconomy", "cosine.boseconomy.CommandManager" })) {
			Economy bose6 = new Economy_BOSE6(this);
			getServer().getServicesManager().register(Economy.class, bose6, this, ServicePriority.Normal);
			log.info(String.format("[%s][Economy] BOSEconomy6 found: %s", getDescription().getName(), bose6.isEnabled() ? "Loaded" : "Waiting"));
		}

		// Try to load BOSEconomy
		if (packageExists(new String[] { "cosine.boseconomy.BOSEconomy", "cosine.boseconomy.CommandHandler" })) {
			Economy bose7 = new Economy_BOSE7(this);
			getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, bose7, this, ServicePriority.Normal);
			log.info(String.format("[%s][Economy] BOSEconomy7 found: %s", getDescription().getName(), bose7.isEnabled() ? "Loaded" : "Waiting"));
		}

		// Try to load Essentials Economy
		if (packageExists(new String[] { "com.earth2me.essentials.api.Economy", "com.earth2me.essentials.api.NoLoanPermittedException", "com.earth2me.essentials.api.UserDoesNotExistException" })) {
			Economy essentials = new Economy_Essentials(this);
			getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, essentials, this, ServicePriority.Low);
			log.info(String.format("[%s][Economy] Essentials Economy found: %s", getDescription().getName(), essentials.isEnabled() ? "Loaded" : "Waiting"));
		}

		// Try to load iConomy 4
		if (packageExists(new String[] { "com.nijiko.coelho.iConomy.iConomy", "com.nijiko.coelho.iConomy.system.Account" })) {
			Economy icon4 = new Economy_iConomy4(this);
			getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, icon4, this, ServicePriority.High);
			log.info(String.format("[%s][Economy] iConomy 4 found: ", getDescription().getName(), icon4.isEnabled() ? "Loaded" : "Waiting"));
		}

		// Try to load iConomy 5
		if (packageExists(new String[] { "com.iConomy.iConomy", "com.iConomy.system.Account", "com.iConomy.system.Holdings" })) {
			Economy icon5 = new Economy_iConomy5(this);
			getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, icon5, this, ServicePriority.High);
			log.info(String.format("[%s][Economy] iConomy 5 found: %s", getDescription().getName(), icon5.isEnabled() ? "Loaded" : "Waiting"));
		}

		// Try to load iConomy 6
		if (packageExists(new String[] { "com.iCo6.iConomy" })) {
			Economy icon6 = new Economy_iConomy6(this);
			getServer().getServicesManager().register(Economy.class, icon6, this, ServicePriority.High);
			log.info(String.format("[%s][Economy] iConomy 6 found: %s", getDescription().getName(), icon6.isEnabled() ? "Loaded" : "Waiting"));
		}
	}

	/**
	 * Attempts to load Permission Addons
	 */
	private void loadPermission() {
		// Try to load PermissionsEx
		if (packageExists(new String[] { "ru.tehkode.permissions.bukkit.PermissionsEx" })) {
			Permission ePerms = new Permission_PermissionsEx(this);
			getServer().getServicesManager().register(Permission.class, ePerms, this, ServicePriority.Highest);
			log.info(String.format("[%s][Permission] PermissionsEx found: %s", getDescription().getName(), ePerms.isEnabled() ? "Loaded" : "Waiting"));
			log.info(String.format("[%s] - Warning Using PEX can cause SuperPerms compatibility issues with non-Vault enabled plugins.", getDescription().getName()));
		}

		//Try loading PermissionsBukkit
		if (packageExists(new String[] {"com.platymuus.bukkit.permissions.PermissionsPlugin"} )) {
			Permission pPerms = new Permission_PermissionsBukkit(this);
			getServer().getServicesManager().register(Permission.class, pPerms, this, ServicePriority.Highest);
			log.info(String.format("[%s][Permission] PermissionsBukkit found: %s", getDescription().getName(), pPerms.isEnabled() ? "Loaded" : "Waiting"));
		}

		//Try to load bPermissions
		if (packageExists(new String[] {"de.bananaco.permissions.worlds.WorldPermissionsManager"} )) {
			Permission bPerms = new Permission_bPermissions(this);
			getServer().getServicesManager().register(Permission.class, bPerms, this, ServicePriority.Highest);
			log.info(String.format("[%s][Permission] bPermissions found: %s", getDescription().getName(), bPerms.isEnabled() ? "Loaded" : "Waiting"));
		}

		// Try to load GroupManager
		if (packageExists(new String[] { "org.anjocaido.groupmanager.GroupManager" })) {
			Permission gPerms = new Permission_GroupManager(this);
			getServer().getServicesManager().register(Permission.class, gPerms, this, ServicePriority.High);
			log.info(String.format("[%s][Permission] GroupManager found: %s", getDescription().getName(), gPerms.isEnabled() ? "Loaded" : "Waiting"));
		}
		// Try to load Permissions 3 (Yeti)
		if (packageExists(new String[] { "com.nijiko.permissions.ModularControl" })) {
			Permission nPerms = new Permission_Permissions3(this);
			getServer().getServicesManager().register(Permission.class, nPerms, this, ServicePriority.High);
			log.info(String.format("[%s][Permission] Permissions 3 (Yeti) found: %s", getDescription().getName(), nPerms.isEnabled() ? "Loaded" : "Waiting"));
		}

		Permission perms = new Permission_SuperPerms(this);
		getServer().getServicesManager().register(Permission.class, perms, this, ServicePriority.Lowest);
		log.info(String.format("[%s][Permission] SuperPermissions loaded as backup permission system.", getDescription().getName()));

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
			Collection<RegisteredServiceProvider<Economy>> econs = this.getServer().getServicesManager().getRegistrations(Economy.class);
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
			Collection<RegisteredServiceProvider<Permission>> perms = this.getServer().getServicesManager().getRegistrations(Permission.class);
			for (RegisteredServiceProvider<Permission> perm : perms) {
				Permission p = perm.getProvider();
				if (registeredPerms == null) {
					registeredPerms = p.getName();
				} else {
					registeredPerms += ", " + p.getName();
				}
			}

			// Get Economy & Permission primary Services
			Economy econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
			Permission perm = getServer().getServicesManager().getRegistration(Permission.class).getProvider();

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
