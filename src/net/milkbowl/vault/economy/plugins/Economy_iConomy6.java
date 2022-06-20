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

import com.iCo6.Constants;
import com.iCo6.iConomy;
import com.iCo6.system.Accounts;
import com.iCo6.system.Holdings;
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

public class Economy_iConomy6 extends AbstractEconomy {
	private final Logger log;
	
	private String name = "iConomy ";
	protected iConomy economy;
	private Accounts accounts;
	
	public Economy_iConomy6(final Plugin plugin) {
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		this.log.warning("iConomy - If you are using Flatfile storage be aware that versions 6, 7 and 8 have a CRITICAL bug which can wipe ALL iconomy data.");
		this.log.warning("if you're using Votifier, or any other plugin which handles economy data in a threaded manner your server is at risk!");
		this.log.warning("it is highly suggested to use SQL with iCo6 or to use an alternative economy plugin!");
		// Load Plugin in case it was loaded before
		if (this.economy == null) {
			final Plugin ec = plugin.getServer().getPluginManager().getPlugin("iConomy");
			if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("com.iCo6.iConomy")) {
				final String version = ec.getDescription().getVersion().split("\\.")[0];
				this.name += version;
				this.economy = (iConomy) ec;
				this.accounts = new Accounts();
				this.log.info(String.format("[Economy] %s hooked.", this.name));
			}
		}
	}
	
	public class EconomyServerListener implements Listener {
		final Economy_iConomy6 economy;
		
		public EconomyServerListener(final Economy_iConomy6 economy) {
			this.economy = economy;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.economy.economy == null) {
				final Plugin ec = event.getPlugin();
				if (ec.getClass().getName().equals("com.iCo6.iConomy")) {
					final String version = ec.getDescription().getVersion().split("\\.")[0];
					Economy_iConomy6.this.name += version;
					this.economy.economy = (iConomy) ec;
					Economy_iConomy6.this.accounts = new Accounts();
					Economy_iConomy6.this.log.info(String.format("[Economy] %s hooked.", this.economy.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.economy.economy != null) {
				if (event.getPlugin().getDescription().getName().equals("iConomy")) {
					this.economy.economy = null;
					Economy_iConomy6.this.log.info(String.format("[Economy] %s unhooked.", this.economy.name));
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
		return iConomy.format(amount);
	}
	
	@Override
	public String currencyNameSingular() {
		return Constants.Nodes.Major.getStringList().get(0);
	}
	
	@Override
	public String currencyNamePlural() {
		return Constants.Nodes.Major.getStringList().get(1);
	}
	
	@Override
	public double getBalance(final String playerName) {
		if (this.accounts.exists(playerName)) {
			return this.accounts.get(playerName).getHoldings().getBalance();
		} else {
			return 0;
		}
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		
		final Holdings holdings = this.accounts.get(playerName).getHoldings();
		if (holdings.hasEnough(amount)) {
			holdings.subtract(amount);
			return new EconomyResponse(amount, holdings.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
		} else {
			return new EconomyResponse(0, holdings.getBalance(), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
		}
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
		}
		
		final Holdings holdings = this.accounts.get(playerName).getHoldings();
		holdings.add(amount);
		return new EconomyResponse(amount, holdings.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		return this.getBalance(playerName) >= amount;
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		if (this.accounts.exists(name)) {
			return new EconomyResponse(0, this.accounts.get(name).getHoldings().getBalance(), EconomyResponse.ResponseType.FAILURE, "That account already exists.");
		}
		final boolean created = this.accounts.create(name);
		if (created) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
		} else {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "There was an error creating the account");
		}
		
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		if (this.accounts.exists(name)) {
			this.accounts.remove(name);
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
		}
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That bank account does not exist.");
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		if (this.has(name, amount)) {
			return new EconomyResponse(0, amount, EconomyResponse.ResponseType.SUCCESS, "");
		} else {
			return new EconomyResponse(0, this.accounts.get(name).getHoldings().getBalance(), EconomyResponse.ResponseType.FAILURE, "The account does not have enough!");
		}
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		
		return this.withdrawPlayer(name, amount);
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
		}
		
		return this.depositPlayer(name, amount);
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "iConomy 6 does not support Bank owners.");
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "iConomy 6 does not support Bank members.");
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		if (!this.accounts.exists(name)) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "There is no bank account with that name");
		} else {
			return new EconomyResponse(0, this.accounts.get(name).getHoldings().getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
		}
	}
	
	@Override
	public List<String> getBanks() {
		throw new UnsupportedOperationException("iConomy does not support listing of bank accounts");
	}
	
	@Override
	public boolean hasBankSupport() {
		return true;
	}
	
	@Override
	public boolean hasAccount(final String playerName) {
		return this.accounts.exists(playerName);
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		if (this.hasAccount(playerName)) {
			return false;
		}
		return this.accounts.create(playerName);
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
