/* This file is part of Vault.

    Vault is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Vault is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.milkbowl.vault.economy.plugins;

import com.gmail.bleedobsidian.miconomy.Main;
import com.gmail.bleedobsidian.miconomy.MiConomy;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Economy_MiConomy extends AbstractEconomy {
	private final Logger log;
	
	private final String name = "MiConomy";
	
	private final Plugin plugin;
	private MiConomy economy;
	private Main miConomy;
	
	public Economy_MiConomy(final Plugin plugin) {
		this.plugin = plugin;
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.miConomy == null) {
			final Plugin miConomyPlugin = plugin.getServer().getPluginManager().getPlugin("MiConomy");
			
			if (this.miConomy != null) {
				this.miConomy = (Main) miConomyPlugin;
				this.economy = this.miConomy.getInstance();
				this.log.info(String.format("[Economy] %s hooked.", this.name));
			}
		}
	}
	
	@Override
	public boolean isEnabled() {
		if (this.miConomy == null) {
			return false;
		} else {
			return this.miConomy.isEnabled();
		}
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean hasBankSupport() {
		return true;
	}
	
	@Override
	public int fractionalDigits() {
		return 2;
	}
	
	@Override
	public String format(final double amount) {
		return this.economy.getFormattedValue(amount);
	}
	
	@Override
	public String currencyNamePlural() {
		return this.miConomy.getPluginConfig().MoneyNamePlural;
	}
	
	@Override
	public String currencyNameSingular() {
		return this.miConomy.getPluginConfig().MoneyName;
	}
	
	@Override
	public boolean hasAccount(final String playerName) {
		final List<World> worlds = this.plugin.getServer().getWorlds();
		
		return this.hasAccount(playerName, worlds.get(0).getName());
	}
	
	@Override
	public boolean hasAccount(final String playerName, final String worldName) {
		final OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(playerName);
		final World world = this.plugin.getServer().getWorld(worldName);
		
		return this.economy.isAccountCreated(player, world);
	}
	
	@Override
	public double getBalance(final String playerName) {
		final List<World> worlds = this.plugin.getServer().getWorlds();
		
		return this.getBalance(playerName, worlds.get(0).getName());
	}
	
	@Override
	public double getBalance(final String playerName, final String worldName) {
		final OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(playerName);
		final World world = this.plugin.getServer().getWorld(worldName);
		
		return this.economy.getAccountBalance(player, world);
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		final List<World> worlds = this.plugin.getServer().getWorlds();
		
		return this.has(playerName, worlds.get(0).getName(), amount);
	}
	
	@Override
	public boolean has(final String playerName, final String worldName, final double amount) {
		final OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(playerName);
		final World world = this.plugin.getServer().getWorld(worldName);
		
		final double playerBalance = this.economy.getAccountBalance(player, world);
		
		return playerBalance >= amount;
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
		final List<World> worlds = this.plugin.getServer().getWorlds();
		
		return this.withdrawPlayer(playerName, worlds.get(0).getName(), amount);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final String worldName, final double amount) {
		final OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(playerName);
		final World world = this.plugin.getServer().getWorld(worldName);
		
		double balance = this.economy.getAccountBalance(player, world);
		
		if (this.getBalance(playerName, worldName) < amount) {
			return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
		} else {
			if (this.economy.removeAccountBalance(player, amount, world)) {
				balance = this.economy.getAccountBalance(player, world);
				
				return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
			} else {
				return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Failed to remove funds from account");
			}
		}
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final double amount) {
		final List<World> worlds = this.plugin.getServer().getWorlds();
		
		return this.depositPlayer(playerName, worlds.get(0).getName(), amount);
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final String worldName, final double amount) {
		final OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(playerName);
		final World world = this.plugin.getServer().getWorld(worldName);
		
		double balance = this.economy.getAccountBalance(player, world);
		
		if (this.economy.addAccountBalance(player, amount, world)) {
			balance = this.economy.getAccountBalance(player, world);
			
			return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
		} else {
			return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Failed to add funds to account");
		}
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		final OfflinePlayer owner = this.plugin.getServer().getOfflinePlayer(player);
		
		final ArrayList<OfflinePlayer> owners = new ArrayList<>();
		owners.add(owner);
		
		if (!this.economy.isBankCreated(name)) {
			this.economy.createBank(name, owners, new ArrayList<>(), false);
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
		} else {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "A bank with this name already exists");
		}
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		if (this.economy.isBankCreated(name)) {
			this.economy.deleteBank(name);
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
		} else {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
		}
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		if (this.economy.isBankCreated(name)) {
			final double balance = this.economy.getBankBalance(name);
			return new EconomyResponse(0, balance, EconomyResponse.ResponseType.SUCCESS, "");
		} else {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
		}
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		if (this.economy.isBankCreated(name)) {
			final double balance = this.economy.getBankBalance(name);
			
			if (balance >= amount) {
				return new EconomyResponse(0, balance, EconomyResponse.ResponseType.SUCCESS, "");
			} else {
				return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "The bank does not have enough money!");
			}
		} else {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
		}
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		if (this.economy.isBankCreated(name)) {
			this.economy.removeBankBalance(name, amount);
			
			final double balance = this.economy.getBankBalance(name);
			
			return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
		} else {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
		}
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		if (this.economy.isBankCreated(name)) {
			this.economy.addBankBalance(name, amount);
			
			final double balance = this.economy.getBankBalance(name);
			
			return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
		} else {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
		}
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		final OfflinePlayer owner = this.plugin.getServer().getOfflinePlayer(playerName);
		
		if (this.economy.isBankCreated(name)) {
			if (this.economy.isPlayerBankOwner(name, owner)) {
				return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
			} else {
				return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "The player is not a bank owner");
			}
		} else {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
		}
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		final OfflinePlayer owner = this.plugin.getServer().getOfflinePlayer(playerName);
		
		if (this.economy.isBankCreated(name)) {
			if (this.economy.isPlayerBankMember(name, owner)) {
				return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
			} else {
				return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "The player is not a bank member");
			}
		} else {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
		}
	}
	
	@Override
	public List<String> getBanks() {
		return this.economy.getBanks();
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		final List<World> worlds = this.plugin.getServer().getWorlds();
		
		return this.createPlayerAccount(playerName, worlds.get(0).getName());
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName, final String worldName) {
		final OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(playerName);
		final World world = this.plugin.getServer().getWorld(worldName);
		
		if (!this.economy.isAccountCreated(player, world)) {
			this.economy.createAccount(player, 0, world);
			
			return true;
		} else {
			return false;
		}
	}
	
	public class EconomyServerListener implements Listener {
		final Economy_MiConomy economy;
		
		public EconomyServerListener(final Economy_MiConomy economy) {
			this.economy = economy;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.economy.economy == null) {
				final Plugin miConomyPlugin = event.getPlugin();
				
				if (miConomyPlugin.getDescription().getName().equals("MiConomy")) {
					this.economy.miConomy = (Main) miConomyPlugin;
					
					this.economy.economy = Economy_MiConomy.this.miConomy.getInstance();
					
					Economy_MiConomy.this.log.info(String.format("[Economy] %s hooked.", Economy_MiConomy.this.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.economy.economy != null) {
				if (event.getPlugin().getDescription().getName().equals("MiConomy")) {
					this.economy.miConomy = null;
					this.economy.economy = null;
					
					Economy_MiConomy.this.log.info(String.format("[Economy] %s unhooked.", this.economy.name));
				}
			}
		}
	}
}
