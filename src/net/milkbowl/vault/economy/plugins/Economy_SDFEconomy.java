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

import com.github.omwah.SDFEconomy.SDFEconomy;
import com.github.omwah.SDFEconomy.SDFEconomyAPI;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.logging.Logger;

public class Economy_SDFEconomy extends AbstractEconomy {
	private final Logger log;
	private final String name = "SDFEconomy";
	private final Plugin plugin;
	private SDFEconomyAPI api;
	
	public Economy_SDFEconomy(final Plugin plugin) {
		this.plugin = plugin;
		log = plugin.getLogger();
		// Register a listener to wait for plugin being loaded
		plugin.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		
		// Try and Load API in case plugin was loaded before Vault
		this.load_api();
	}
	
	public void load_api() {
		final SDFEconomy pluginSDF = (SDFEconomy) this.plugin.getServer().getPluginManager().getPlugin("SDFEconomy");
		if (!this.isEnabled() && pluginSDF != null) {
			this.api = pluginSDF.getAPI();
			this.log.info(String.format("[Economy] %s hooked.", this.name));
		}
	}
	
	public void unload_api() {
		final SDFEconomy pluginSDF = (SDFEconomy) this.plugin.getServer().getPluginManager().getPlugin("SDFEconomy");
		if (this.isEnabled() && pluginSDF != null) {
			this.api = null;
			this.log.info(String.format("[Economy] %s unhooked.", this.name));
		}
	}
	
	public static class EconomyServerListener implements Listener {
		final Economy_SDFEconomy economy;
		
		public EconomyServerListener(final Economy_SDFEconomy economy) {
			this.economy = economy;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (event.getPlugin().getDescription().getName().equals("SDFEconomy")) {
				this.economy.load_api();
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (event.getPlugin().getDescription().getName().equals("SDFEconomy")) {
				this.economy.unload_api();
			}
		}
	}
	
	
	@Override
	public boolean isEnabled() {
		return this.api != null;
	}
	
	@Override
	public String getName() {
		return "SDFEconomy";
	}
	
	@Override
	public boolean hasBankSupport() {
		return this.api.hasBankSupport();
	}
	
	@Override
	public int fractionalDigits() {
		return this.api.fractionalDigits();
	}
	
	@Override
	public String format(final double amount) {
		return this.api.format(amount);
	}
	
	@Override
	public String currencyNamePlural() {
		return this.api.currencyNamePlural();
	}
	
	@Override
	public String currencyNameSingular() {
		return this.api.currencyNameSingular();
	}
	
	@Override
	public boolean hasAccount(final String playerName) {
		return this.api.hasAccount(playerName);
	}
	
	@Override
	public double getBalance(final String playerName) {
		return this.api.getBalance(playerName);
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		return this.api.has(playerName, amount);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
		return this.api.withdrawPlayer(playerName, amount);
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final double amount) {
		return this.api.depositPlayer(playerName, amount);
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		return this.api.createBank(name, player);
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		return this.api.deleteBank(name);
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		return this.api.bankBalance(name);
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		return this.api.bankHas(name, amount);
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		return this.api.bankWithdraw(name, amount);
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		return this.api.bankDeposit(name, amount);
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		return this.api.isBankOwner(name, playerName);
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		return this.api.isBankMember(name, playerName);
	}
	
	@Override
	public List<String> getBanks() {
		return this.api.getBankNames();
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		return this.api.createPlayerAccount(playerName);
	}
	
	@Override
	public boolean hasAccount(final String playerName, final String worldName) {
		return this.hasAccount(playerName);
	}
	
	@Override
	public double getBalance(final String playerName, final String world) {
		return this.getBalance(playerName);
	}
	
	@Override
	public boolean has(final String playerName, final String worldName, final double amount) {
		return this.has(playerName, amount);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final String worldName, final double amount) {
		return this.withdrawPlayer(playerName, amount);
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final String worldName, final double amount) {
		return this.depositPlayer(playerName, amount);
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName, final String worldName) {
		return this.createPlayerAccount(playerName);
	}
}
