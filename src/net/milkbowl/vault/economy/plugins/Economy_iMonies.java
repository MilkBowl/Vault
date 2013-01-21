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
package com.mciseries.iMonies;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_iMonies implements Economy {
	private static final Logger log = Logger.getLogger("Minecraft");
	private final String name = "iMonies";
	private Plugin plugin = null;
	private iMonies imonies = null;
	
	public Economy_iMonies(Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		// Load plugin in case it was loaded before
		if(imonies == null) {
			Plugin imon = plugin.getServer().getPluginManager().getPlugin("iMonies");
			if(imon != null && imon.isEnabled()) {
				imonies = (iMonies) imon;
				log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
			}
		}
	}
	
	public class EconomyServerListener implements Listener {
		Economy_iMonies economy = null;
		
		public EconomyServerListener(Economy_iMonies economy_iMonies) {
			this.economy = economy_iMonies;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			if(economy.imonies == null) {
				Plugin imon = plugin.getServer().getPluginManager().getPlugin("iMonies");
				
				if(imon != null && imon.isEnabled()) {
					economy.imonies = (iMonies) imon;
					log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.nate));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			if(economy.imonies != null) {
				if(event.getPlugin().getDiscription().getName().equals("iMonies")) {
					economy.imonies = null;
					log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
				}
			}
		}
	}
	
	@Override
	public boolean isEnabled() {
		return imonies != null && imonies.isEnabled();
	}
	
	@Override
	public String getName() {
		return name;
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
		return "" + (int)amount;
	}
	
	@Override
	public String currencyNamePlural() {
		return "Dollars";
	}
	
	@Override
	public String currencyNameSingular() {
		return "Dollar";
	}
	
	@Override
	public boolean hasAccount(String playerName) {
		if(Bukkit.getPluginManager().getPlugin("iMonies").getConfig().getInt("Accounts." + playerName + ".Balance") != 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public double getBalance(String playerName) {
		return (double) Bukkit.getPluginManager().getPlugin("iMonies").getConfig().getInt("Accounts." + name + ".Balance");
	}
	
	@Override
	public boolean has(String playerName, double amount) {
		if(Bukkit.getPluginManager().getPlugin("iMonies").getConfig().getInt("Accounts." + playerName + ".Balance") >= (int) amount) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		Bukkit.getPluginManager().getPlugin("iMonies").getConfig().set("Accounts." + playerName + ".Balance", Bukkit.getPluginManager().getPlugin("iMonies").getConfig().getInt("Accounts." + playerName + ".Balance") - (int)amount);
		Bukkit.getPluginManager().getPlugin("iMonies").saveConfig();
		Bukkit.getPluginManager().getPlugin("iMonies").reloadConfig();
		return new EconomyResponse(amount, Bukkit.getPluginManager().getPlugin("iMonies").getConfig().getInt("Accounts." + playerName + ".Balance"), ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		Bukkit.getPluginManager().getPlugin("iMonies").getConfig().set("Accounts." + playerName + ".Balance", Bukkit.getPluginManager().getPlugin("iMonies").getConfig().getInt("Accounts." + playerName + ".Balance") + (int)amount);
		Bukkit.getPluginManager().getPlugin("iMonies").saveConfig();
		Bukkit.getPluginManager().getPlugin("iMonies").reloadConfig();
		return new EconomyResponse(amount, Bukkit.getPluginManager().getPlugin("iMonies").getConfig().getInt("Accounts." + playerName + ".Balance"), ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "iMonies doesn't support banks!");
	}
	
	@Override
	public EconomyResponse deleteBank(String name) {
		return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "iMonies doesn't support banks!");
	}
	
	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "iMonies doesn't support banks!");
	}
	
	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "iMonies doesn't support banks!");
	}
	
	@Override
		public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "iMonies doesn't support banks!");
		}

		@Override
		public EconomyResponse bankDeposit(String name, double amount) {
			return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "iMonies doesn't support banks!");
		}

		@Override
		public EconomyResponse isBankOwner(String name, String playerName) {
			return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "iMonies doesn't support banks!");
		}

		@Override
		public EconomyResponse isBankMember(String name, String playerName) {
			return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "iMonies doesn't support banks!");
		}

		@Override
		public List<String> getBanks() {
			return new ArrayList<String>();
		}

		@Override
		public boolean createPlayerAccount(String playerName) {
				return hasAccount(playerName);
		}
}
