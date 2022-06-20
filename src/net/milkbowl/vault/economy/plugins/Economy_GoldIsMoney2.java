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

import com.flobi.GoldIsMoney2.GoldIsMoney;
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

public class Economy_GoldIsMoney2 extends AbstractEconomy {
	private final Logger log;
	private final String name = "GoldIsMoney";
	protected GoldIsMoney economy;
	
	public Economy_GoldIsMoney2(final Plugin plugin) {
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		// Load Plugin in case it was loaded before
		if (this.economy == null) {
			final Plugin ec = plugin.getServer().getPluginManager().getPlugin("GoldIsMoney");
			
			if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("com.flobi.GoldIsMoney2.GoldIsMoney")) {
				this.economy = (GoldIsMoney) ec;
				this.log.info(String.format("[Economy] %s hooked.", this.name));
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
		return GoldIsMoney.hasBankSupport();
	}
	
	@Override
	public int fractionalDigits() {
		return GoldIsMoney.fractionalDigits();
	}
	
	@Override
	public String format(final double amount) {
		return GoldIsMoney.format(amount);
	}
	
	@Override
	public String currencyNamePlural() {
		return GoldIsMoney.currencyNamePlural();
	}
	
	@Override
	public String currencyNameSingular() {
		return GoldIsMoney.currencyNameSingular();
	}
	
	@Override
	public boolean hasAccount(final String playerName) {
		return GoldIsMoney.hasAccount(playerName);
	}
	
	@Override
	public double getBalance(final String playerName) {
		return GoldIsMoney.getBalance(playerName);
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		return GoldIsMoney.has(playerName, amount);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds!");
		}
		if (!GoldIsMoney.hasAccount(playerName)) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That player does not have an account!");
		}
		if (!GoldIsMoney.has(playerName, amount)) {
			return new EconomyResponse(0, GoldIsMoney.getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
		}
		if (!GoldIsMoney.withdrawPlayer(playerName, amount)) {
			return new EconomyResponse(0, GoldIsMoney.getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Unable to withdraw funds!");
		}
		return new EconomyResponse(amount, GoldIsMoney.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds!");
		}
		if (!GoldIsMoney.hasAccount(playerName)) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That player does not have an account!");
		}
		if (!GoldIsMoney.depositPlayer(playerName, amount)) {
			return new EconomyResponse(0, GoldIsMoney.getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Unable to deposit funds!");
		}
		return new EconomyResponse(amount, GoldIsMoney.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		if (!GoldIsMoney.hasBankSupport()) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
		if (!GoldIsMoney.createBank(name, player)) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Unable to create bank account.");
		}
		return new EconomyResponse(0, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		if (!GoldIsMoney.hasBankSupport()) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
		if (!GoldIsMoney.deleteBank(name)) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Unable to remove bank account.");
		}
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		if (!GoldIsMoney.hasBankSupport()) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
		if (!GoldIsMoney.bankExists(name)) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
		}
		return new EconomyResponse(0, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		if (!GoldIsMoney.hasBankSupport()) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
		if (!GoldIsMoney.bankExists(name)) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
		}
		if (GoldIsMoney.bankHas(name, amount)) {
			return new EconomyResponse(0, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.FAILURE, "The bank does not have enough money!");
		}
		return new EconomyResponse(0, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		if (!GoldIsMoney.hasBankSupport()) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
		if (!GoldIsMoney.bankExists(name)) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
		}
		if (!GoldIsMoney.bankHas(name, amount)) {
			return new EconomyResponse(0, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.FAILURE, "The bank does not have enough money!");
		}
		if (!GoldIsMoney.bankWithdraw(name, amount)) {
			return new EconomyResponse(0, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.FAILURE, "Unable to withdraw from that bank account!");
		}
		return new EconomyResponse(amount, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		if (!GoldIsMoney.hasBankSupport()) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
		if (!GoldIsMoney.bankExists(name)) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
		}
		if (!GoldIsMoney.bankDeposit(name, amount)) {
			return new EconomyResponse(0, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.FAILURE, "Unable to deposit to that bank account!");
		}
		return new EconomyResponse(amount, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		if (!GoldIsMoney.hasBankSupport()) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
		if (!GoldIsMoney.bankExists(name)) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
		}
		if (!GoldIsMoney.isBankOwner(name, playerName)) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That player does not own that bank!");
		}
		return new EconomyResponse(0, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		if (!GoldIsMoney.hasBankSupport()) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
		if (!GoldIsMoney.bankExists(name)) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
		}
		if (!GoldIsMoney.isBankMember(name, playerName)) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "That player is not a member of that bank!");
		}
		return new EconomyResponse(0, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.SUCCESS, "");
	}
	
	@Override
	public List<String> getBanks() {
		return GoldIsMoney.getBanks();
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		return GoldIsMoney.createPlayerAccount(playerName);
	}
	
	public class EconomyServerListener implements Listener {
		final Economy_GoldIsMoney2 economy;
		
		public EconomyServerListener(final Economy_GoldIsMoney2 economy_GoldIsMoney2) {
			economy = economy_GoldIsMoney2;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.economy.economy == null) {
				final Plugin ec = event.getPlugin();
				
				if (ec.getClass().getName().equals("com.flobi.GoldIsMoney2.GoldIsMoney")) {
					this.economy.economy = (GoldIsMoney) ec;
					Economy_GoldIsMoney2.this.log.info(String.format("[Economy] %s hooked.", this.economy.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.economy.economy != null) {
				if (event.getPlugin().getDescription().getName().equals("GoldIsMoney")) {
					this.economy.economy = null;
					Economy_GoldIsMoney2.this.log.info(String.format("[Economy] %s unhooked.", this.economy.name));
				}
			}
		}
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
