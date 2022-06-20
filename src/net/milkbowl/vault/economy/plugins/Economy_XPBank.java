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

import com.gmail.mirelatrue.xpbank.API;
import com.gmail.mirelatrue.xpbank.Account;
import com.gmail.mirelatrue.xpbank.GroupBank;
import com.gmail.mirelatrue.xpbank.XPBank;
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

public class Economy_XPBank extends AbstractEconomy {
	
	private final Logger log;
	private final String name = "XPBank";
	private XPBank XPB;
	private API api;
	
	public Economy_XPBank(final Plugin plugin) {
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.XPB == null) {
			final Plugin economy = plugin.getServer().getPluginManager().getPlugin("XPBank");
			if (economy != null && economy.isEnabled()) {
				this.XPB = (XPBank) economy;
				this.api = this.XPB.getAPI();
				this.log.info(String.format("[Economy] %s hooked.", this.name));
			}
		}
	}
	
	public class EconomyServerListener implements Listener {
		final Economy_XPBank economy;
		
		public EconomyServerListener(final Economy_XPBank economy_XPBank) {
			economy = economy_XPBank;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.economy.XPB == null) {
				final Plugin eco = event.getPlugin();
				
				if (eco.getDescription().getName().equals("XPBank")) {
					this.economy.XPB = (XPBank) eco;
					Economy_XPBank.this.api = Economy_XPBank.this.XPB.getAPI();
					Economy_XPBank.this.log.info(String.format("[Economy] %s hooked.", this.economy.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.economy.XPB != null) {
				if (event.getPlugin().getDescription().getName().equals("XPBank")) {
					this.economy.XPB = null;
					Economy_XPBank.this.log.info(String.format("[Economy] %s unhooked.", this.economy.name));
				}
			}
		}
	}
	
	@Override
	public boolean isEnabled() {
		return XPB != null;
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
		return 0;
	}
	
	@Override
	public String format(final double amount) {
		return String.format("%d %s", (int) amount, this.api.currencyName((int) amount));
	}
	
	@Override
	public String currencyNamePlural() {
		return this.api.getMsg("CurrencyNamePlural");
	}
	
	@Override
	public String currencyNameSingular() {
		return this.api.getMsg("currencyName");
	}
	
	@Override
	public boolean hasAccount(final String playerName) {
		final Account account = this.api.getAccount(playerName);
		
		return account != null;
	}
	
	@Override
	public double getBalance(final String playerName) {
		final Account account = this.api.getAccount(playerName);
		
		return account.getBalance();
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		final Account account = this.api.getAccount(playerName);
		
		return account.getBalance() >= (int) amount;
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
		final Account account = this.api.getAccount(playerName);
		
		if (account == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("Player doesn't exist."));
		}
		
		final int value = (int) amount;
		final int balance = account.getBalance();
		
		if (value < 1) {
			return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("LessThanZero"));
		}
		
		if (value > balance) {
			return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, String.format(this.api.getMsg("InsufficientXP"), this.api.currencyName(value)));
		}
		
		account.modifyBalance(-value);
		
		return new EconomyResponse(value, balance - value, EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final double amount) {
		final Account account = this.api.getAccount(playerName);
		
		if (account == null) {
			// Stupid plugins that use fake players without creating them first...
			// return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player doesn't exist");
			createPlayerAccount(playerName);
		}
		
		final int value = (int) amount;
		final int balance = account.getBalance();
		
		if (value < 1) {
			return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("LessThanZero"));
		}
		
		account.addTaxableIncome(value);
		
		return new EconomyResponse(value, balance + value, EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		GroupBank groupBank = this.api.getGroupBank(name);
		
		if (groupBank != null) {
			return new EconomyResponse(0, groupBank.getBalance(), EconomyResponse.ResponseType.FAILURE, String.format(this.api.getMsg("GroupBankExists"), name));
		}
		
		final Account account = this.api.getAccount(player);
		
		groupBank = this.api.createGroupBank(name, account);
		
		return new EconomyResponse(0, groupBank.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		final GroupBank groupBank = this.api.getGroupBank(name);
		
		if (groupBank == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("GroupBankNotExists"));
		}
		
		this.api.deleteGroupBank(groupBank, String.format(this.api.getMsg("Disbanded"), groupBank.getName()));
		
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		final GroupBank groupBank = this.api.getGroupBank(name);
		
		if (groupBank == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("GroupBankNotExists"));
		}
		
		return new EconomyResponse(0, groupBank.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		final GroupBank groupBank = this.api.getGroupBank(name);
		
		if (groupBank == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("GroupBankNotExists"));
		}
		
		final int value = (int) amount;
		final int balance = groupBank.getBalance();
		
		if (balance >= value) {
			return new EconomyResponse(0, balance, EconomyResponse.ResponseType.SUCCESS, null);
		}
		
		return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, String.format(this.api.getMsg("InsufficientXP"), this.api.currencyName(value)));
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		final GroupBank groupBank = this.api.getGroupBank(name);
		
		if (groupBank == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("GroupBankNotExists"));
		}
		
		final int value = (int) amount;
		final int balance = groupBank.getBalance();
		
		if (value < 1) {
			return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("LessThanZero"));
		}
		
		if (value > balance) {
			return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, String.format(this.api.getMsg("InsufficientXP"), this.api.currencyName(value)));
		}
		
		groupBank.modifyBalance(-value);
		
		return new EconomyResponse(value, balance - value, EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		final GroupBank groupBank = this.api.getGroupBank(name);
		
		if (groupBank == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("GroupBankNotExists"));
		}
		
		final int value = (int) amount;
		final int balance = groupBank.getBalance();
		
		if (value < 1) {
			return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("LessThanZero"));
		}
		
		groupBank.modifyBalance(value);
		
		return new EconomyResponse(value, balance + value, EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		final GroupBank groupBank = this.api.getGroupBank(name);
		
		if (groupBank == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("GroupBankNotExists"));
		}
		
		final Account account = this.api.getAccount(name);
		
		if (account == null) {
			return new EconomyResponse(0, groupBank.getBalance(), EconomyResponse.ResponseType.FAILURE, this.api.getMsg("PlayerNotExist"));
		}
		
		if (groupBank.getOwner().equalsIgnoreCase(name)) {
			return new EconomyResponse(0, groupBank.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
		}
		
		return new EconomyResponse(0, groupBank.getBalance(), EconomyResponse.ResponseType.FAILURE, String.format(this.api.getMsg("PlayerNotOwner"), account.getName(), groupBank.getName()));
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		final GroupBank groupBank = this.api.getGroupBank(name);
		
		if (groupBank == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("GroupBankNotExists"));
		}
		
		final Account account = this.api.getAccount(name);
		
		if (account == null) {
			return new EconomyResponse(0, groupBank.getBalance(), EconomyResponse.ResponseType.FAILURE, this.api.getMsg("PlayerNotExist"));
		}
		
		if (groupBank.groupMembers.getMembers().containsKey(playerName)) {
			return new EconomyResponse(0, groupBank.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
		}
		
		return new EconomyResponse(0, groupBank.getBalance(), EconomyResponse.ResponseType.FAILURE, String.format(this.api.getMsg("NotAMemberOf"), groupBank.getName(), account.getName()));
	}
	
	@Override
	public List<String> getBanks() {
		return this.api.getAllGroupBanks();
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		this.api.createAccount(playerName);
		
		return true;
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
