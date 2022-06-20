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

import me.ashtheking.currency.Currency;
import me.ashtheking.currency.CurrencyList;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Economy_MultiCurrency extends AbstractEconomy {
	private final Logger log;
	private final String name = "MultiCurrency";
	private Currency economy;
	
	public Economy_MultiCurrency(final Plugin plugin) {
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.economy == null) {
			final Plugin multiCurrency = plugin.getServer().getPluginManager().getPlugin("MultiCurrency");
			if (multiCurrency != null && multiCurrency.isEnabled()) {
				this.economy = (Currency) multiCurrency;
				this.log.info(String.format("[Economy] %s hooked.", this.name));
			}
		}
	}
	
	@Override
	public String getName() {
		return this.name;
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
	public double getBalance(final String playerName) {
		double balance;
		
		balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);
		
		return balance;
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, double amount) {
		final double balance;
		final EconomyResponse.ResponseType type;
		String errorMessage = null;
		
		if (amount < 0) {
			errorMessage = "Cannot withdraw negative funds";
			type = EconomyResponse.ResponseType.FAILURE;
			amount = 0;
			balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);
			
			return new EconomyResponse(amount, balance, type, errorMessage);
		}
		
		if (!CurrencyList.hasEnough(playerName, amount)) {
			errorMessage = "Insufficient funds";
			type = EconomyResponse.ResponseType.FAILURE;
			amount = 0;
			balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);
			
			return new EconomyResponse(amount, balance, type, errorMessage);
		}
		
		if (CurrencyList.subtract(playerName, amount)) {
			type = EconomyResponse.ResponseType.SUCCESS;
			balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);
			
			return new EconomyResponse(amount, balance, type, errorMessage);
		} else {
			errorMessage = "Error withdrawing funds";
			type = EconomyResponse.ResponseType.FAILURE;
			amount = 0;
			balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);
			
			return new EconomyResponse(amount, balance, type, errorMessage);
		}
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, double amount) {
		final double balance;
		final EconomyResponse.ResponseType type;
		String errorMessage = null;
		
		if (amount < 0) {
			errorMessage = "Cannot deposit negative funds";
			type = EconomyResponse.ResponseType.FAILURE;
			amount = 0;
			balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);
			
			return new EconomyResponse(amount, balance, type, errorMessage);
		}
		
		if (CurrencyList.add(playerName, amount)) {
			type = EconomyResponse.ResponseType.SUCCESS;
			balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);
			
			return new EconomyResponse(amount, balance, type, errorMessage);
		} else {
			errorMessage = "Error withdrawing funds";
			type = EconomyResponse.ResponseType.FAILURE;
			amount = 0;
			balance = CurrencyList.getValue((String) CurrencyList.maxCurrency(playerName)[0], playerName);
			
			return new EconomyResponse(amount, balance, type, errorMessage);
		}
	}
	
	public class EconomyServerListener implements Listener {
		final Economy_MultiCurrency economy;
		
		public EconomyServerListener(final Economy_MultiCurrency economy) {
			this.economy = economy;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.economy.economy == null) {
				final Plugin mcur = event.getPlugin();
				
				if (mcur.getDescription().getName().equals("MultiCurrency")) {
					this.economy.economy = (Currency) mcur;
					Economy_MultiCurrency.this.log.info(String.format("[Economy] %s hooked.", this.economy.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.economy.economy != null) {
				if (event.getPlugin().getDescription().getName().equals("MultiCurrency")) {
					this.economy.economy = null;
					Economy_MultiCurrency.this.log.info(String.format("[Economy] %s unhooked.", this.economy.name));
				}
			}
		}
	}
	
	@Override
	public String format(final double amount) {
		return String.format("%.2f %s", amount, "currency");
	}
	
	@Override
	public String currencyNameSingular() {
		return "currency";
	}
	
	@Override
	public String currencyNamePlural() {
		return "currency";
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		return this.getBalance(playerName) >= amount;
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
	}
	
	@Override
	public List<String> getBanks() {
		return new ArrayList<>();
	}
	
	@Override
	public boolean hasBankSupport() {
		return false;
	}
	
	@Override
	public boolean hasAccount(final String playerName) {
		return true;
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		return false;
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
