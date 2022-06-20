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
import net.teamalpha.taecon.TAEcon;
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

public class Economy_TAEcon extends AbstractEconomy {
	private final Logger log;
	private final String name = "TAEcon";
	private TAEcon economy;
	
	public Economy_TAEcon(final Plugin plugin) {
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		
		if (this.economy == null) {
			final Plugin taecon = plugin.getServer().getPluginManager().getPlugin(this.name);
			
			if (taecon != null && taecon.isEnabled()) {
				this.economy = (TAEcon) taecon;
				this.log.info(String.format("[Economy] %s hooked.", this.name));
			}
		}
	}
	
	public class EconomyServerListener implements Listener {
		final Economy_TAEcon economy;
		
		public EconomyServerListener(final Economy_TAEcon economy) {
			this.economy = economy;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.economy.economy == null) {
				final Plugin taecon = event.getPlugin();
				
				if (taecon.getDescription().getName().equals(this.economy.name)) {
					this.economy.economy = (TAEcon) taecon;
					Economy_TAEcon.this.log.info(String.format("[Economy] %s hooked.", this.economy.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.economy.economy != null) {
				if (event.getPlugin().getDescription().getName().equals(this.economy.name)) {
					this.economy.economy = null;
					Economy_TAEcon.this.log.info(String.format("[Economy] %s unhooked.", this.economy.name));
				}
			}
		}
	}
	
	@Override
	public boolean isEnabled() {
		return this.economy != null;
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
		return 0;
	}
	
	@Override
	public String format(double amount) {
		amount = Math.ceil(amount);
		if (amount == 1) {
			return String.format("%d %s", 1, this.currencyNameSingular());
		} else {
			return String.format("%d %s", (int) amount, this.currencyNamePlural());
		}
	}
	
	@Override
	public String currencyNamePlural() {
		return this.economy.getCurrencyName(true);
	}
	
	@Override
	public String currencyNameSingular() {
		return this.economy.getCurrencyName(false);
	}
	
	@Override
	public boolean hasAccount(final String playerName) {
		return true;
	}
	
	@Override
	public double getBalance(final String playerName) {
		return this.economy.getBalance(playerName);
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		return this.getBalance(playerName) >= amount;
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
		final EconomyResponse.ResponseType rt;
		final String message;
		final int iamount = (int) Math.ceil(amount);
		
		if (this.has(playerName, amount)) {
			if (this.economy.removeBalance(playerName, iamount)) {
				rt = EconomyResponse.ResponseType.SUCCESS;
				message = null;
			} else {
				rt = EconomyResponse.ResponseType.SUCCESS;
				message = "ERROR";
			}
		} else {
			rt = EconomyResponse.ResponseType.FAILURE;
			message = "Not enough money";
		}
		
		return new EconomyResponse(iamount, this.getBalance(playerName), rt, message);
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final double amount) {
		final EconomyResponse.ResponseType rt;
		final String message;
		final int iamount = (int) Math.floor(amount);
		
		if (this.economy.addBalance(playerName, iamount)) {
			rt = EconomyResponse.ResponseType.SUCCESS;
			message = null;
		} else {
			rt = EconomyResponse.ResponseType.SUCCESS;
			message = "ERROR";
		}
		
		return new EconomyResponse(iamount, this.getBalance(playerName), rt, message);
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
	}
	
	@Override
	public List<String> getBanks() {
		return new ArrayList<>();
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		return false;
	}
	
	@Override
	public boolean hasAccount(final String playerName, final String worldName) {
		return true;
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
		return false;
	}
}
