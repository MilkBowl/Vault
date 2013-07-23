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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.craftconomy3.groups.WorldGroupsManager;
import com.greatmancode.craftconomy3.tools.interfaces.BukkitLoader;

public class Economy_Craftconomy3 implements Economy {
	private static final Logger log = Logger.getLogger("Minecraft");
	private final String name = "Craftconomy3";
	private Plugin plugin = null;
	protected BukkitLoader economy = null;

	public Economy_Craftconomy3(Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

		// Load Plugin in case it was loaded before
		if (economy == null) {
			Plugin ec = plugin.getServer().getPluginManager().getPlugin("Craftconomy3");
			if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("com.greatmancode.craftconomy3.BukkitLoader")) {
				economy = (BukkitLoader) ec;
				log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
			}
		}
	}

	public class EconomyServerListener implements Listener {
		Economy_Craftconomy3 economy = null;

		public EconomyServerListener(Economy_Craftconomy3 economy) {
			this.economy = economy;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			if (economy.economy == null) {
				Plugin ec = plugin.getServer().getPluginManager().getPlugin("Craftconomy3");

				if (ec != null && ec.getClass().getName().equals("com.greatmancode.craftconomy3.tools.interfaces.BukkitLoader")) {
					economy.economy = (BukkitLoader) ec;
					log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			if (economy.economy != null) {
				if (event.getPlugin().getDescription().getName().equals("Craftconomy3")) {
					economy.economy = null;
					log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
				}
			}
		}
	}

	@Override
	public boolean isEnabled() {
		if (economy == null) {
			return false;
		} else {
			return economy.isEnabled();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String format(double amount) {
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
	public double getBalance(String playerName) {
		return getBalance(playerName, WorldGroupsManager.DEFAULT_GROUP_NAME);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		return withdrawPlayer(playerName, WorldGroupsManager.DEFAULT_GROUP_NAME, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		return depositPlayer(playerName, WorldGroupsManager.DEFAULT_GROUP_NAME, amount);
	}

	@Override
	public boolean has(String playerName, double amount) {
		return has(playerName, WorldGroupsManager.DEFAULT_GROUP_NAME, amount);
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		boolean success = false;
		if (!Common.getInstance().getAccountManager().exist(Account.BANK_PREFIX + name)) {
			Common.getInstance().getAccountManager().getAccount(Account.BANK_PREFIX + name).getAccountACL().set(player, true, true, true, true, true);
			success = true;
		}
		if (success) {
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, "");
		}

		return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to create that bank account. It already exists!");
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		boolean success = Common.getInstance().getAccountManager().delete(Account.BANK_PREFIX + name);
		if (success) {
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, "");
		}

		return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to delete that bank account.");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {

		if (Common.getInstance().getAccountManager().exist(Account.BANK_PREFIX + name)) {
			Account account = Common.getInstance().getAccountManager().getAccount(Account.BANK_PREFIX + name);
			if (account.hasEnough(amount, Common.getInstance().getServerCaller().getDefaultWorld(), Common.getInstance().getCurrencyManager().getDefaultCurrency().getName())) {
				return new EconomyResponse(0, bankBalance(Account.BANK_PREFIX + name).balance, ResponseType.SUCCESS, "");
			} else {
				return new EconomyResponse(0, bankBalance(Account.BANK_PREFIX + name).balance, ResponseType.FAILURE, "The bank does not have enough money!");
			}
		}
		return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
		}

		EconomyResponse er = bankHas(name, amount);
		if (!er.transactionSuccess()) {
			return er;
		} else {
			if (Common.getInstance().getAccountManager().exist(Account.BANK_PREFIX + name)) {
				return new EconomyResponse(0, withdrawPlayer(Account.BANK_PREFIX + name, amount).balance, ResponseType.SUCCESS, "");
			}
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
		}
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds");
		}

		if (Common.getInstance().getAccountManager().exist(Account.BANK_PREFIX + name)) {
			return new EconomyResponse(0, depositPlayer(Account.BANK_PREFIX + name, amount).balance, ResponseType.SUCCESS, "");
		}
		return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		if (Common.getInstance().getAccountManager().exist(Account.BANK_PREFIX + name)) {
			if (Common.getInstance().getAccountManager().getAccount(Account.BANK_PREFIX + name).getAccountACL().isOwner(playerName)) {
				return new EconomyResponse(0, bankBalance(Account.BANK_PREFIX + name).balance, ResponseType.SUCCESS, "");
			}
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "This player is not the owner of the bank!");
		}
		return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {

		// Basicly here if the user have access to deposit & withdraw he's a member
		EconomyResponse er = isBankOwner(name, playerName);
		if (er.transactionSuccess()) {
			return er;
		} else {
			if (Common.getInstance().getAccountManager().exist(Account.BANK_PREFIX + name)) {
				Account account = Common.getInstance().getAccountManager().getAccount(Account.BANK_PREFIX + name);
				if (account.getAccountACL().canDeposit(playerName) && account.getAccountACL().canWithdraw(playerName)) {
					return new EconomyResponse(0, bankBalance(name).balance, ResponseType.SUCCESS, "");
				}
			}
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "This player is not a member of the bank!");
		}
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		if (Common.getInstance().getAccountManager().exist(Account.BANK_PREFIX + name)) {
			return new EconomyResponse(0, getBalance(Account.BANK_PREFIX + name), ResponseType.SUCCESS, "");
		}
		return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
	}

	@Override
	public List<String> getBanks() {
		List<AccountTable> accountList = Common.getInstance().getDatabaseManager().getDatabase().select(AccountTable.class).where().contains("name", Account.BANK_PREFIX).execute().find();
		ArrayList<String> list = new ArrayList<String>();
		Iterator<AccountTable> iterator = accountList.iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next().getName().replaceFirst(Account.BANK_PREFIX, ""));
		}
		return list;
	}

	@Override
	public boolean hasBankSupport() {
		return true;
	}

	@Override
	public boolean hasAccount(String playerName) {
		return Common.getInstance().getAccountManager().exist(playerName);
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		if (Common.getInstance().getAccountManager().exist(playerName)) {
			return false;
		}
		Common.getInstance().getAccountManager().getAccount(playerName);
		return true;
	}

	@Override
	public int fractionalDigits() {
		return -1;
	}

	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return hasAccount(playerName);
	}

	@Override
	public double getBalance(String playerName, String world) {
		return Common.getInstance().getAccountManager().getAccount(playerName).getBalance(world, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return Common.getInstance().getAccountManager().getAccount(playerName).hasEnough(amount, worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, getBalance(playerName, worldName), ResponseType.FAILURE, "Cannot withdraw negative funds");
		}

		double balance;
		Account account = Common.getInstance().getAccountManager().getAccount(playerName);
		if (account.hasEnough(amount, worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName())) {
			balance = account.withdraw(amount, worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName(), Cause.VAULT, null);
			return new EconomyResponse(amount, balance, ResponseType.SUCCESS, "");
		} else {
			return new EconomyResponse(0, getBalance(playerName, worldName), ResponseType.FAILURE, "Insufficient funds");
		}
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, getBalance(playerName, worldName), ResponseType.FAILURE, "Cannot desposit negative funds");
		}

		Account account = Common.getInstance().getAccountManager().getAccount(playerName);

		double balance = account.deposit(amount, worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName(), Cause.VAULT, null);
		return new EconomyResponse(amount, balance, ResponseType.SUCCESS, null);
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return createPlayerAccount(playerName);
	}

        @Override
        public EconomyResponse withdrawPlayer(String playerName, double amount, boolean sendMessage) {
                return withdrawPlayer(playerName, amount);
        }

        @Override
        public EconomyResponse depositPlayer(String playerName, double amount, boolean sendMessage) {
    	        return depositPlayer(playerName, amount);
        }
}
