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

import com.flobi.GoldIsMoney.GoldIsMoney;

public class Economy_GoldIsMoney implements Economy {
	private static final Logger log = Logger.getLogger("Minecraft");
	
	private final String name = "GoldIsMoney";
	private Plugin plugin = null;
	protected GoldIsMoney economy = null;
	
	public Economy_GoldIsMoney(Plugin plugin) {
	    this.plugin = plugin;
	    Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
	    // Load Plugin in case it was loaded before
	    if (economy == null) {
	        Plugin ec = plugin.getServer().getPluginManager().getPlugin("GoldIsMoney");
	        if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("com.flobi.GoldIsMoney.GoldIsMoney")) {
	            economy = (GoldIsMoney) ec;
	            log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
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
	
	private double getAccountBalance(String playerName) {
	    return GoldIsMoney.getBalance(playerName);
	}
	
	@Override
	public double getBalance(String playerName) {
	    return getAccountBalance(playerName);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
	    if (amount < 0) {
	        return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
	    }
	
	    if (GoldIsMoney.has(playerName, Math.round(amount))) {
	    	GoldIsMoney.withdrawPlayer(playerName, (long) amount);
	        return new EconomyResponse(amount, getAccountBalance(playerName), ResponseType.SUCCESS, null);
	    } else {
	        return new EconomyResponse(0, getAccountBalance(playerName), ResponseType.FAILURE, "Insufficient funds");
	    }
	}
	
	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
	    if (amount < 0) {
	        return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds");
	    }
	    
	    GoldIsMoney.depositPlayer(playerName, Math.round(amount));
	    return new EconomyResponse(amount, GoldIsMoney.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	public class EconomyServerListener implements Listener {
	    Economy_GoldIsMoney economy = null;
	
	    public EconomyServerListener(Economy_GoldIsMoney economy_GoldIsMoney) {
	        this.economy = economy_GoldIsMoney;
	    }
	
	    @EventHandler(priority = EventPriority.MONITOR)
	    public void onPluginEnable(PluginEnableEvent event) {
	        if (economy.economy == null) {
	            Plugin ec = event.getPlugin();
	
	            if (ec.getDescription().getName().equals("GoldIsMoney") && ec.getClass().getName().equals("com.flobi.GoldIsMoney.GoldIsMoney")) {
	                economy.economy = (GoldIsMoney) ec;
	                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
	            }
	        }
	    }
	
	    @EventHandler(priority = EventPriority.MONITOR)
	    public void onPluginDisable(PluginDisableEvent event) {
	        if (economy.economy != null) {
	            if (event.getPlugin().getDescription().getName().equals("GoldIsMoney")) {
	                economy.economy = null;
	                log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
	            }
	        }
	    }
	}
	
	@Override
	public String format(double amount) {
	    return GoldIsMoney.format(Math.round(amount));
	}
	
	@Override
	public String currencyNameSingular() {
		return GoldIsMoney.currencyNameSingular();
	}
	
	@Override
	public String currencyNamePlural() {
		return GoldIsMoney.currencyNamePlural();
	}
	
	@Override
	public boolean has(String playerName, double amount) {
	    return getBalance(playerName) >= amount;
	}
	
	@Override
	public EconomyResponse createBank(String name, String player) {
	    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney does not support single account banks!");
	}
	
	@Override
	public EconomyResponse deleteBank(String name) {
	    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankHas(String name, double amount) {
	    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney does not support single bank accounts!");
	}
	
	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
	    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney does not support single bank accounts!");
	}
	
	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
	    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney does not support single bank accounts!");
	}
	
	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
	    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney does not support single bank accounts!");
	}
	
	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
	    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney does not support single bank accounts!");
	}
	
	@Override
	public EconomyResponse bankBalance(String name) {
	    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney does not support single bank accounts!");
	}
	
	@Override
	public List<String> getBanks() {
	    return new ArrayList<String>();
	}
	
	@Override
	public boolean hasBankSupport() {
	    return false;
	}
	
	@Override
	public boolean hasAccount(String playerName) {
	    return GoldIsMoney.hasAccount(playerName);
	}
	
	@Override
	public boolean createPlayerAccount(String playerName) {
	    return hasAccount(playerName);
	}

	@Override
	public int fractionalDigits() {
		return 0;
	}

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }
}