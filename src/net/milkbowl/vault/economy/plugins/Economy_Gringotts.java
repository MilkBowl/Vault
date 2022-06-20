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

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.gestern.gringotts.Account;
import org.gestern.gringotts.AccountHolder;
import org.gestern.gringotts.Gringotts;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Economy_Gringotts extends AbstractEconomy {
	
	private final Logger log;
	
	private final String name = "Gringotts";
	private Gringotts gringotts;
	
	public Economy_Gringotts(final Plugin plugin) {
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		// Load Plugin in case it was loaded before
		if (this.gringotts == null) {
			final Plugin grngts = plugin.getServer().getPluginManager().getPlugin("Gringotts");
			if (grngts != null && grngts.isEnabled()) {
				this.gringotts = (Gringotts) grngts;
				this.log.info(String.format("[Economy] %s hooked.", this.name));
			}
		}
	}
	
	public class EconomyServerListener implements Listener {
		final Economy_Gringotts economy;
		
		public EconomyServerListener(final Economy_Gringotts economy_Gringotts) {
			economy = economy_Gringotts;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.economy.gringotts == null) {
				final Plugin grngts = event.getPlugin();
				
				if (grngts.getDescription().getName().equals("Gringotts")) {
					this.economy.gringotts = (Gringotts) grngts;
					Economy_Gringotts.this.log.info(String.format("[Economy] %s hooked.", this.economy.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.economy.gringotts != null) {
				if (event.getPlugin().getDescription().getName().equals("Gringotts")) {
					this.economy.gringotts = null;
					Economy_Gringotts.this.log.info(String.format("[Economy] %s unhooked.", this.economy.name));
				}
			}
		}
	}
	
	@Override
	public boolean isEnabled() {
		return this.gringotts != null && this.gringotts.isEnabled();
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
		return 2;
	}
	
	@Override
	public String format(final double amount) {
		return Double.toString(amount);
	}
	
	@Override
	public String currencyNamePlural() {
		return org.gestern.gringotts.Configuration.config.currencyNamePlural;
	}
	
	@Override
	public String currencyNameSingular() {
		return org.gestern.gringotts.Configuration.config.currencyNameSingular;
	}
	
	@Override
	public boolean hasAccount(final String playerName) {
		final AccountHolder owner = this.gringotts.accountHolderFactory.getAccount(playerName);
		if (owner == null) {
			return false;
		}
		
		return this.gringotts.accounting.getAccount(owner) != null;
	}
	
	@Override
	public double getBalance(final String playerName) {
		final AccountHolder owner = this.gringotts.accountHolderFactory.getAccount(playerName);
		if (owner == null) {
			return 0;
		}
		final Account account = this.gringotts.accounting.getAccount(owner);
		return account.balance();
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		return this.getBalance(playerName) >= amount;
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
		
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw a negative amount.");
		}
		
		final AccountHolder accountHolder = this.gringotts.accountHolderFactory.getAccount(playerName);
		if (accountHolder == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, playerName + " is not a valid account holder.");
		}
		
		final Account account = this.gringotts.accounting.getAccount(accountHolder);
		
		if (account.balance() >= amount && account.remove(amount)) {
			//We has mulah!
			return new EconomyResponse(amount, account.balance(), EconomyResponse.ResponseType.SUCCESS, null);
		} else {
			//Not enough money to withdraw this much.
			return new EconomyResponse(0, account.balance(), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
		}
		
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
		}
		
		final AccountHolder accountHolder = this.gringotts.accountHolderFactory.getAccount(playerName);
		if (accountHolder == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, playerName + " is not a valid account holder.");
		}
		
		final Account account = this.gringotts.accounting.getAccount(accountHolder);
		
		if (account.add(amount)) {
			return new EconomyResponse(amount, account.balance(), EconomyResponse.ResponseType.SUCCESS, null);
		} else {
			return new EconomyResponse(0, account.balance(), EconomyResponse.ResponseType.FAILURE, "Not enough capacity to store that amount!");
		}
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
	}
	
	@Override
	public List<String> getBanks() {
		return new ArrayList<>();
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		return this.hasAccount(playerName);
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
