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

import ca.agnate.EconXP.EconXP;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Economy_EconXP extends AbstractEconomy {
	private final Logger log;
	
	private final String name = "EconXP";
	private EconXP econ;
	
	public Economy_EconXP(final Plugin plugin) {
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		this.log.log(Level.WARNING, "EconXP is an integer only economy, you may notice inconsistencies with accounts if you do not setup your other econ using plugins accordingly!");
		// Load Plugin in case it was loaded before
		if (this.econ == null) {
			final Plugin econ = plugin.getServer().getPluginManager().getPlugin("EconXP");
			if (econ != null && econ.isEnabled()) {
				this.econ = (EconXP) econ;
				this.log.info(String.format("[Economy] %s hooked.", this.name));
			}
		}
	}
	
	public class EconomyServerListener implements Listener {
		final Economy_EconXP economy;
		
		public EconomyServerListener(final Economy_EconXP economy) {
			this.economy = economy;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.economy.econ == null) {
				final Plugin eco = event.getPlugin();
				
				if (eco.getDescription().getName().equals("EconXP")) {
					this.economy.econ = (EconXP) eco;
					Economy_EconXP.this.log.info(String.format("[Economy] %s hooked.", this.economy.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.economy.econ != null) {
				if (event.getPlugin().getDescription().getName().equals("EconXP")) {
					this.economy.econ = null;
					Economy_EconXP.this.log.info(String.format("[Economy] %s unhooked.", this.economy.name));
				}
			}
		}
	}
	
	@Override
	public boolean isEnabled() {
		return econ != null;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public String format(double amount) {
		amount = Math.ceil(amount);
		
		return String.format("%d %s", (int) amount, "experience");
	}
	
	@Override
	public String currencyNamePlural() {
		return "experience";
	}
	
	@Override
	public String currencyNameSingular() {
		return "experience";
	}
	
	@Override
	public double getBalance(final String playerName) {
		final OfflinePlayer player = this.econ.getPlayer(playerName);
		
		if (player == null) {
			return 0;
		}
		
		return this.econ.getExp(player);
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		final OfflinePlayer player = this.econ.getPlayer(playerName);
		
		if (player == null) {
			return false;
		}
		
		return this.econ.hasExp(player, (int) Math.ceil(amount));
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, double amount) {
		final OfflinePlayer player = this.econ.getPlayer(playerName);
		
		if (player == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player does not exist");
		}
		
		final double balance = this.econ.getExp(player);
		amount = Math.ceil(amount);
		
		if (amount < 0) {
			return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		
		if (!this.econ.hasExp(player, (int) amount)) {
			return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
		}
		
		this.econ.removeExp(player, (int) amount);
		
		final double finalBalance = this.econ.getExp(player);
		
		return new EconomyResponse(amount, finalBalance, EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, double amount) {
		final OfflinePlayer player = this.econ.getPlayer(playerName);
		
		if (player == null) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player does not exist");
		}
		
		double balance = this.econ.getExp(player);
		amount = Math.ceil(amount);
		
		if (amount < 0) {
			return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		
		this.econ.addExp(player, (int) amount);
		balance = this.econ.getExp(player);
		
		return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
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
		return this.econ.getPlayer(playerName) != null;
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		return false;
	}
	
	@Override
	public int fractionalDigits() {
		return 0;
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
