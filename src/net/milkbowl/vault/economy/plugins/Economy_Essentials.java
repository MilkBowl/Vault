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

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
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

public class Economy_Essentials extends AbstractEconomy {
	
	private final String name = "Essentials Economy";
	private final Logger log;
	private Essentials ess;
	
	public Economy_Essentials(final Plugin plugin) {
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.ess == null) {
			final Plugin essentials = plugin.getServer().getPluginManager().getPlugin("Essentials");
			if (essentials != null && essentials.isEnabled()) {
				this.ess = (Essentials) essentials;
				this.log.info(String.format("[Economy] %s hooked.", this.name));
			}
		}
	}
	
	@Override
	public boolean isEnabled() {
		if (this.ess == null) {
			return false;
		} else {
			return this.ess.isEnabled();
		}
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public double getBalance(final String playerName) {
		double balance;
		
		try {
			balance = com.earth2me.essentials.api.Economy.getMoney(playerName);
		} catch (final UserDoesNotExistException e) {
			this.createPlayerAccount(playerName);
			balance = 0;
		}
		
		return balance;
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		if (this.hasAccount(playerName)) {
			return false;
		}
		return com.earth2me.essentials.api.Economy.createNPC(playerName);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, double amount) {
		if (playerName == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player name can not be null.");
		}
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		
		double balance;
		EconomyResponse.ResponseType type;
		String errorMessage = null;
		
		try {
			com.earth2me.essentials.api.Economy.subtract(playerName, amount);
			balance = com.earth2me.essentials.api.Economy.getMoney(playerName);
			type = EconomyResponse.ResponseType.SUCCESS;
		} catch (final UserDoesNotExistException e) {
			if (this.createPlayerAccount(playerName)) {
				return this.withdrawPlayer(playerName, amount);
			} else {
				amount = 0;
				balance = 0;
				type = EconomyResponse.ResponseType.FAILURE;
				errorMessage = "User does not exist";
			}
		} catch (final NoLoanPermittedException e) {
			try {
				balance = com.earth2me.essentials.api.Economy.getMoney(playerName);
				amount = 0;
				type = EconomyResponse.ResponseType.FAILURE;
				errorMessage = "Loan was not permitted";
			} catch (final UserDoesNotExistException e1) {
				amount = 0;
				balance = 0;
				type = EconomyResponse.ResponseType.FAILURE;
				errorMessage = "User does not exist";
			}
		}
		
		return new EconomyResponse(amount, balance, type, errorMessage);
	}
	
	public EconomyResponse tryDepositPlayer(final String playerName, double amount, int tries) {
		if (playerName == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player name can not be null.");
		}
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
		}
		if (tries <= 0) {
			return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Failed to deposit amount.");
		}
		
		double balance;
		EconomyResponse.ResponseType type;
		String errorMessage = null;
		
		try {
			com.earth2me.essentials.api.Economy.add(playerName, amount);
			balance = com.earth2me.essentials.api.Economy.getMoney(playerName);
			type = EconomyResponse.ResponseType.SUCCESS;
		} catch (final UserDoesNotExistException e) {
			if (this.createPlayerAccount(playerName)) {
				return this.tryDepositPlayer(playerName, amount, tries--);
			} else {
				amount = 0;
				balance = 0;
				type = EconomyResponse.ResponseType.FAILURE;
				errorMessage = "User does not exist";
			}
		} catch (final NoLoanPermittedException e) {
			try {
				balance = com.earth2me.essentials.api.Economy.getMoney(playerName);
				amount = 0;
				type = EconomyResponse.ResponseType.FAILURE;
				errorMessage = "Loan was not permitted";
			} catch (final UserDoesNotExistException e1) {
				balance = 0;
				amount = 0;
				type = EconomyResponse.ResponseType.FAILURE;
				errorMessage = "Loan was not permitted";
			}
		}
		
		return new EconomyResponse(amount, balance, type, errorMessage);
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final double amount) {
		return this.tryDepositPlayer(playerName, amount, 2);
	}
	
	public class EconomyServerListener implements Listener {
		final Economy_Essentials economy;
		
		public EconomyServerListener(final Economy_Essentials economy) {
			this.economy = economy;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.economy.ess == null) {
				final Plugin essentials = event.getPlugin();
				
				if (essentials.getDescription().getName().equals("Essentials")) {
					this.economy.ess = (Essentials) essentials;
					Economy_Essentials.this.log.info(String.format("[Economy] %s hooked.", this.economy.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.economy.ess != null) {
				if (event.getPlugin().getDescription().getName().equals("Essentials")) {
					this.economy.ess = null;
					Economy_Essentials.this.log.info(String.format("[Economy] %s unhooked.", this.economy.name));
				}
			}
		}
	}
	
	@Override
	public String format(final double amount) {
		return com.earth2me.essentials.api.Economy.format(amount);
	}
	
	@Override
	public String currencyNameSingular() {
		return "";
	}
	
	@Override
	public String currencyNamePlural() {
		return "";
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		try {
			return com.earth2me.essentials.api.Economy.hasEnough(playerName, amount);
		} catch (final UserDoesNotExistException e) {
			return false;
		}
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
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
		return com.earth2me.essentials.api.Economy.playerExists(playerName);
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
