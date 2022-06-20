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

import me.igwb.GoldenChest.GoldenChestEconomy;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.logging.Logger;

public class Economy_GoldenChestEconomy extends AbstractEconomy {
	private final Logger log;
	
	private final String name = "GoldenChestEconomy";
	private GoldenChestEconomy economy;
	
	
	public Economy_GoldenChestEconomy(final Plugin plugin) {
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		// Load Plugin in case it was loaded before
		if (this.economy == null) {
			final Plugin ec = plugin.getServer().getPluginManager().getPlugin("GoldenChestEconomy");
			if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("me.igwb.GoldenChest.GoldenChestEconomy")) {
				this.economy = (GoldenChestEconomy) ec;
				this.log.info(String.format("[Economy] %s hooked.", this.name));
			}
		}
	}
	
	public class EconomyServerListener implements Listener {
		final Economy_GoldenChestEconomy economy;
		
		public EconomyServerListener(final Economy_GoldenChestEconomy economy_GoldenChestEconomy) {
			economy = economy_GoldenChestEconomy;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.economy.economy == null) {
				final Plugin ec = event.getPlugin();
				
				if (ec.getDescription().getName().equals("GoldenChestEconomy") && ec.getClass().getName().equals("me.igwb.GoldenChest.GoldenChestEconomy")) {
					this.economy.economy = (GoldenChestEconomy) ec;
					Economy_GoldenChestEconomy.this.log.info(String.format("[Economy] %s hooked.", this.economy.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.economy.economy != null) {
				if (event.getPlugin().getDescription().getName().equals("GoldenChestEconomy")) {
					this.economy.economy = null;
					Economy_GoldenChestEconomy.this.log.info(String.format("[Economy] %s unhooked.", this.economy.name));
				}
			}
		}
	}
	
	
	@Override
	public boolean isEnabled() {
		if (this.economy == null) {
			return false;
		} else {
			return this.economy.isEnabled();
		}
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean hasBankSupport() {
		return false;
	}
	
	@Override
	public int fractionalDigits() {
		return this.economy.getVaultConnector().fractionalDigits();
	}
	
	@Override
	public String format(final double amount) {
		return this.economy.getVaultConnector().format(amount);
	}
	
	@Override
	public String currencyNamePlural() {
		return this.economy.getVaultConnector().currencyNamePlural();
	}
	
	@Override
	public String currencyNameSingular() {
		return this.economy.getVaultConnector().currencyNameSingular();
	}
	
	@Override
	public boolean hasAccount(final String playerName) {
		return this.economy.getVaultConnector().hasAccount(playerName);
	}
	
	@Override
	public boolean hasAccount(final String playerName, final String worldName) {
		return this.economy.getVaultConnector().hasAccount(playerName, worldName);
	}
	
	@Override
	public double getBalance(final String playerName) {
		return this.economy.getVaultConnector().getBalance(playerName);
	}
	
	@Override
	public double getBalance(final String playerName, final String world) {
		return this.economy.getVaultConnector().getBalance(playerName, world);
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		return this.economy.getVaultConnector().has(playerName, amount);
	}
	
	@Override
	public boolean has(final String playerName, final String worldName, final double amount) {
		return this.economy.getVaultConnector().has(playerName, worldName, amount);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
		
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		
		if (this.has(playerName, amount)) {
			this.economy.getVaultConnector().withdrawPlayer(playerName, amount);
			return new EconomyResponse(amount, this.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
		} else {
			return new EconomyResponse(0, this.getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
		}
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final String worldName,
	                                      final double amount) {
		return this.withdrawPlayer(playerName, amount);
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
		}
		
		this.economy.getVaultConnector().depositPlayer(playerName, amount);
		return new EconomyResponse(amount, this.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final String worldName,
	                                     final double amount) {
		return this.depositPlayer(playerName, amount);
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
	}
	
	@Override
	public List<String> getBanks() {
		return null;
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		return this.economy.getVaultConnector().createPlayerAccount(playerName);
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName, final String worldName) {
		return this.economy.getVaultConnector().createPlayerAccount(playerName, worldName);
	}
	
}
