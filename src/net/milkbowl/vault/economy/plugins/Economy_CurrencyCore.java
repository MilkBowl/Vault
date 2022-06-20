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

import is.currency.Currency;
import is.currency.syst.AccountContext;
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

public class Economy_CurrencyCore extends AbstractEconomy {
	
	private Currency currency;
	private final Logger log;
	private final String name = "CurrencyCore";
	
	public Economy_CurrencyCore(final Plugin plugin) {
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.currency == null) {
			final Plugin currencyPlugin = plugin.getServer().getPluginManager().getPlugin("CurrencyCore");
			if (currencyPlugin != null && currencyPlugin.getClass().getName().equals("is.currency.Currency")) {
				currency = (Currency) currencyPlugin;
				this.log.info(String.format("[Economy] %s hooked.", this.name));
			}
		}
	}
	
	public class EconomyServerListener implements Listener {
		
		private final Economy_CurrencyCore economy;
		
		public EconomyServerListener(final Economy_CurrencyCore economy) {
			this.economy = economy;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (economy.currency == null) {
				final Plugin currencyPlugin = event.getPlugin();
				
				if (currencyPlugin.getDescription().getName().equals("CurrencyCore") && currencyPlugin.getClass().getName().equals("is.currency.Currency")) {
					economy.currency = (Currency) currencyPlugin;
					Economy_CurrencyCore.this.log.info(String.format("[Economy] %s hooked.", economy.getName()));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (economy.currency != null) {
				if (event.getPlugin().getDescription().getName().equals("CurrencyCore")) {
					economy.currency = null;
					Economy_CurrencyCore.this.log.info(String.format("[Economy] %s unhooked.", economy.getName()));
				}
			}
		}
	}
	
	@Override
	public boolean isEnabled() {
		return this.currency != null;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public String format(final double amount) {
		return currency.getFormatHelper().format(amount);
	}
	
	@Override
	public String currencyNamePlural() {
		return this.currency.getCurrencyConfig().getCurrencyMajor().get(1);
	}
	
	@Override
	public String currencyNameSingular() {
		return this.currency.getCurrencyConfig().getCurrencyMajor().get(0);
	}
	
	@Override
	public double getBalance(final String playerName) {
		final AccountContext account = currency.getAccountManager().getAccount(playerName);
		if (account == null) {
			return 0.0;
		}
		
		return account.getBalance();
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		final AccountContext account = currency.getAccountManager().getAccount(playerName);
		if (account == null) {
			return false;
		} else {
			return account.hasBalance(amount);
		}
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		
		final AccountContext account = currency.getAccountManager().getAccount(playerName);
		if (account == null) {
			return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "That account does not exist");
		} else if (!account.hasBalance(amount)) {
			return new EconomyResponse(0.0, account.getBalance(), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
		} else {
			account.subtractBalance(amount);
			return new EconomyResponse(amount, account.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
		}
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
		}
		
		final AccountContext account = currency.getAccountManager().getAccount(playerName);
		if (account == null) {
			return new EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "That account does not exist");
		}
		account.addBalance(amount);
		return new EconomyResponse(amount, account.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		if (currency.getAccountManager().hasAccount(name)) {
			return new EconomyResponse(0, this.currency.getAccountManager().getAccount(name).getBalance(), EconomyResponse.ResponseType.FAILURE, "That account already exists.");
		}
		currency.getAccountManager().createAccount(name);
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		if (currency.getAccountManager().hasAccount(name)) {
			currency.getAccountManager().deleteAccount(name);
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
		}
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That account does not exist!");
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		final AccountContext account = currency.getAccountManager().getAccount(name);
		
		if (account == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That account does not exists.");
		}
		return new EconomyResponse(0, account.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		final AccountContext account = currency.getAccountManager().getAccount(name);
		if (account == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That account does not exist!");
		} else if (!account.hasBalance(amount)) {
			return new EconomyResponse(0, account.getBalance(), EconomyResponse.ResponseType.FAILURE, "That account does not have enough!");
		} else {
			return new EconomyResponse(0, account.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
		}
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		
		final AccountContext account = currency.getAccountManager().getAccount(name);
		if (account == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That account does not exist!");
		} else if (!account.hasBalance(amount)) {
			return new EconomyResponse(0, account.getBalance(), EconomyResponse.ResponseType.FAILURE, "That account does not have enough!");
		} else {
			account.subtractBalance(amount);
			return new EconomyResponse(amount, account.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
		}
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
		}
		
		final AccountContext account = currency.getAccountManager().getAccount(name);
		if (account == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That account does not exist!");
		} else {
			account.addBalance(amount);
			return new EconomyResponse(amount, account.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
		}
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Currency does not support Bank members.");
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Currency does not support Bank members.");
	}
	
	@Override
	public List<String> getBanks() {
		return currency.getAccountManager().getAccountList();
	}
	
	@Override
	public boolean hasBankSupport() {
		return true;
	}
	
	@Override
	public boolean hasAccount(final String playerName) {
		return currency.getAccountManager().getAccount(playerName) != null;
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		if (currency.getAccountManager().getAccount(playerName) != null) {
			return false;
		}
		currency.getAccountManager().createAccount(playerName);
		return true;
	}
	
	@Override
	public int fractionalDigits() {
		return -1;
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
