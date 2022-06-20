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

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.groups.WorldGroupsManager;
import com.greatmancode.craftconomy3.tools.interfaces.BukkitLoader;
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

public class Economy_Craftconomy3 extends AbstractEconomy {
	private final Logger log;
	private final String name = "Craftconomy3";
	protected BukkitLoader economy;
	
	public Economy_Craftconomy3(final Plugin plugin) {
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.economy == null) {
			final Plugin ec = plugin.getServer().getPluginManager().getPlugin("Craftconomy3");
			if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("com.greatmancode.craftconomy3.BukkitLoader")) {
				this.economy = (BukkitLoader) ec;
				this.log.info(String.format("[Economy] %s hooked.", this.name));
			}
		}
	}
	
	public class EconomyServerListener implements Listener {
		final Economy_Craftconomy3 economy;
		
		public EconomyServerListener(final Economy_Craftconomy3 economy) {
			this.economy = economy;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.economy.economy == null) {
				final Plugin ec = event.getPlugin();
				
				if (ec.getDescription().getName().equals("Craftconomy3") && ec.getClass().getName().equals("com.greatmancode.craftconomy3.tools.interfaces.BukkitLoader")) {
					this.economy.economy = (BukkitLoader) ec;
					Economy_Craftconomy3.this.log.info(String.format("[Economy] %s hooked.", this.economy.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.economy.economy != null) {
				if (event.getPlugin().getDescription().getName().equals("Craftconomy3")) {
					this.economy.economy = null;
					Economy_Craftconomy3.this.log.info(String.format("[Economy] %s unhooked.", this.economy.name));
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
	public String format(final double amount) {
		return Common.getInstance().format(null, Common.getInstance().getCurrencyManager().getDefaultCurrency(), amount);
	}
	
	@Override
	public String currencyNameSingular() {
		return Common.getInstance().getCurrencyManager().getDefaultCurrency().getName();
	}
	
	@Override
	public String currencyNamePlural() {
		return Common.getInstance().getCurrencyManager().getDefaultCurrency().getPlural();
	}
	
	@Override
	public double getBalance(final String playerName) {
		return this.getBalance(playerName, WorldGroupsManager.DEFAULT_GROUP_NAME);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
		return this.withdrawPlayer(playerName, WorldGroupsManager.DEFAULT_GROUP_NAME, amount);
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final double amount) {
		return this.depositPlayer(playerName, WorldGroupsManager.DEFAULT_GROUP_NAME, amount);
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		return this.has(playerName, WorldGroupsManager.DEFAULT_GROUP_NAME, amount);
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		boolean success = false;
		if (!Common.getInstance().getAccountManager().exist(name, true)) {
			Common.getInstance().getAccountManager().getAccount(name, true).getAccountACL().set(player, true, true, true, true, true);
			success = true;
		}
		if (success) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
		}
		
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Unable to create that bank account. It already exists!");
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		final boolean success = Common.getInstance().getAccountManager().delete(name, true);
		if (success) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
		}
		
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Unable to delete that bank account.");
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		
		if (Common.getInstance().getAccountManager().exist(name, true)) {
			final Account account = Common.getInstance().getAccountManager().getAccount(name, true);
			if (account.hasEnough(amount, Common.getInstance().getServerCaller().getDefaultWorld(), Common.getInstance().getCurrencyManager().getDefaultCurrency().getName())) {
				return new EconomyResponse(0, this.bankBalance(name).balance, EconomyResponse.ResponseType.SUCCESS, "");
			} else {
				return new EconomyResponse(0, this.bankBalance(name).balance, EconomyResponse.ResponseType.FAILURE, "The bank does not have enough money!");
			}
		}
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		
		final EconomyResponse er = this.bankHas(name, amount);
		if (!er.transactionSuccess()) {
			return er;
		} else {
			if (Common.getInstance().getAccountManager().exist(name, true)) {
				return new EconomyResponse(0, Common.getInstance().getAccountManager().getAccount(name, true).withdraw(amount, WorldGroupsManager.DEFAULT_GROUP_NAME, Common.getInstance().getCurrencyManager().getDefaultBankCurrency().getName(), Cause.VAULT, null), EconomyResponse.ResponseType.SUCCESS, "");
			}
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
		}
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
		}
		
		if (Common.getInstance().getAccountManager().exist(name, true)) {
			return new EconomyResponse(0, Common.getInstance().getAccountManager().getAccount(name, true).deposit(amount, WorldGroupsManager.DEFAULT_GROUP_NAME, Common.getInstance().getCurrencyManager().getDefaultBankCurrency().getName(), Cause.VAULT, null), EconomyResponse.ResponseType.SUCCESS, "");
		}
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		if (Common.getInstance().getAccountManager().exist(name, true)) {
			if (Common.getInstance().getAccountManager().getAccount(name, true).getAccountACL().isOwner(playerName)) {
				return new EconomyResponse(0, this.bankBalance(name).balance, EconomyResponse.ResponseType.SUCCESS, "");
			}
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "This player is not the owner of the bank!");
		}
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		
		// Basicly here if the user have access to deposit & withdraw he's a member
		final EconomyResponse er = this.isBankOwner(name, playerName);
		if (er.transactionSuccess()) {
			return er;
		} else {
			if (Common.getInstance().getAccountManager().exist(name, true)) {
				final Account account = Common.getInstance().getAccountManager().getAccount(name, true);
				if (account.getAccountACL().canDeposit(playerName) && account.getAccountACL().canWithdraw(playerName)) {
					return new EconomyResponse(0, this.bankBalance(name).balance, EconomyResponse.ResponseType.SUCCESS, "");
				}
			}
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "This player is not a member of the bank!");
		}
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		if (Common.getInstance().getAccountManager().exist(name, true)) {
			return new EconomyResponse(0, Common.getInstance().getAccountManager().getAccount(name, true).getBalance(WorldGroupsManager.DEFAULT_GROUP_NAME, Common.getInstance().getCurrencyManager().getDefaultBankCurrency().getName()), EconomyResponse.ResponseType.SUCCESS, "");
		}
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
	}
	
	@Override
	public List<String> getBanks() {
		return Common.getInstance().getAccountManager().getAllAccounts(true);
	}
	
	@Override
	public boolean hasBankSupport() {
		return true;
	}
	
	@Override
	public boolean hasAccount(final String playerName) {
		return Common.getInstance().getAccountManager().exist(playerName, false);
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		if (Common.getInstance().getAccountManager().exist(playerName, false)) {
			return false;
		}
		Common.getInstance().getAccountManager().getAccount(playerName, false);
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
		return Common.getInstance().getAccountManager().getAccount(playerName, false).getBalance(world, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
	}
	
	@Override
	public boolean has(final String playerName, final String worldName, final double amount) {
		return Common.getInstance().getAccountManager().getAccount(playerName, false).hasEnough(amount, worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final String worldName, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, this.getBalance(playerName, worldName), EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		
		final double balance;
		final Account account = Common.getInstance().getAccountManager().getAccount(playerName, false);
		if (account.hasEnough(amount, worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName())) {
			balance = account.withdraw(amount, worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName(), Cause.VAULT, null);
			return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
		} else {
			return new EconomyResponse(0, this.getBalance(playerName, worldName), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
		}
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final String worldName, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, this.getBalance(playerName, worldName), EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
		}
		
		final Account account = Common.getInstance().getAccountManager().getAccount(playerName, false);
		
		final double balance = account.deposit(amount, worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName(), Cause.VAULT, null);
		return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName, final String worldName) {
		return this.createPlayerAccount(playerName);
	}
}
