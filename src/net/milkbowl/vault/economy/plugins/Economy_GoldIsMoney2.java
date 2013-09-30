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

import com.flobi.GoldIsMoney2.GoldIsMoney;

public class Economy_GoldIsMoney2 implements Economy {
	private static final Logger log = Logger.getLogger("Minecraft");
	
	private final String name = "GoldIsMoney";
	private Plugin plugin = null;
	protected GoldIsMoney economy = null;
	
	public Economy_GoldIsMoney2(Plugin plugin) {
	    this.plugin = plugin;
	    Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
	    // Load Plugin in case it was loaded before
	    if (economy == null) {
	        Plugin ec = plugin.getServer().getPluginManager().getPlugin("GoldIsMoney");

	        if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("com.flobi.GoldIsMoney2.GoldIsMoney")) {
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
	
	@Override
	public boolean hasBankSupport() {
	    return GoldIsMoney.hasBankSupport();
	}

	@Override
	public int fractionalDigits() {
		return GoldIsMoney.fractionalDigits();
	}
	
	@Override
	public String format(double amount) {
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
	public boolean hasAccount(String playerName) {
	    return GoldIsMoney.hasAccount(playerName);
	}
	
	@Override
	public double getBalance(String playerName) {
	    return GoldIsMoney.getBalance(playerName);
	}
	
	@Override
	public boolean has(String playerName, double amount) {
	    return GoldIsMoney.has(playerName, amount);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
	    if (amount < 0) {
	        return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds!");
	    }
	    if (!GoldIsMoney.hasAccount(playerName)) {
	        return new EconomyResponse(0, 0, ResponseType.FAILURE, "That player does not have an account!");
	    }
	    if (!GoldIsMoney.has(playerName, amount)) {
	        return new EconomyResponse(0, GoldIsMoney.getBalance(playerName), ResponseType.FAILURE, "Insufficient funds");
	    }
	    if (!GoldIsMoney.withdrawPlayer(playerName, amount)) {
	        return new EconomyResponse(0, GoldIsMoney.getBalance(playerName), ResponseType.FAILURE, "Unable to withdraw funds!");
	    }
        return new EconomyResponse(amount, GoldIsMoney.getBalance(playerName), ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
	    if (amount < 0) {
	        return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds!");
	    }
	    if (!GoldIsMoney.hasAccount(playerName)) {
	        return new EconomyResponse(0, 0, ResponseType.FAILURE, "That player does not have an account!");
	    }
	    if (!GoldIsMoney.depositPlayer(playerName, amount)) {
	        return new EconomyResponse(0, GoldIsMoney.getBalance(playerName), ResponseType.FAILURE, "Unable to deposit funds!");
	    }
        return new EconomyResponse(amount, GoldIsMoney.getBalance(playerName), ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse createBank(String name, String player) {
		if (!GoldIsMoney.hasBankSupport()) {
		    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
        if (!GoldIsMoney.createBank(name, player)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to create bank account.");
        }
        return new EconomyResponse(0, GoldIsMoney.bankBalance(name), ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse deleteBank(String name) {
		if (!GoldIsMoney.hasBankSupport()) {
		    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
        if (!GoldIsMoney.deleteBank(name)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to remove bank account.");
        }
        return new EconomyResponse(0, 0, ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse bankBalance(String name) {
		if (!GoldIsMoney.hasBankSupport()) {
		    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
        if (!GoldIsMoney.bankExists(name)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
        }
        return new EconomyResponse(0, GoldIsMoney.bankBalance(name), ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse bankHas(String name, double amount) {
		if (!GoldIsMoney.hasBankSupport()) {
		    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
        if (!GoldIsMoney.bankExists(name)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
        }
        if (GoldIsMoney.bankHas(name, amount)) {
            return new EconomyResponse(0, GoldIsMoney.bankBalance(name), ResponseType.FAILURE, "The bank does not have enough money!");
        }
        return new EconomyResponse(0, GoldIsMoney.bankBalance(name), ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		if (!GoldIsMoney.hasBankSupport()) {
		    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
        if (!GoldIsMoney.bankExists(name)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
        }
        if (!GoldIsMoney.bankHas(name, amount)) {
            return new EconomyResponse(0, GoldIsMoney.bankBalance(name), ResponseType.FAILURE, "The bank does not have enough money!");
        }
    	if (!GoldIsMoney.bankWithdraw(name, amount)) {
            return new EconomyResponse(0, GoldIsMoney.bankBalance(name), ResponseType.FAILURE, "Unable to withdraw from that bank account!");
        }
        return new EconomyResponse(amount, GoldIsMoney.bankBalance(name), ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		if (!GoldIsMoney.hasBankSupport()) {
		    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
        if (!GoldIsMoney.bankExists(name)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
        }
    	if (!GoldIsMoney.bankDeposit(name, amount)) {
            return new EconomyResponse(0, GoldIsMoney.bankBalance(name), ResponseType.FAILURE, "Unable to deposit to that bank account!");
        }
        return new EconomyResponse(amount, GoldIsMoney.bankBalance(name), ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		if (!GoldIsMoney.hasBankSupport()) {
		    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
        if (!GoldIsMoney.bankExists(name)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
        }
        if (!GoldIsMoney.isBankOwner(name, playerName)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That player does not own that bank!");
        }
        return new EconomyResponse(0, GoldIsMoney.bankBalance(name), ResponseType.SUCCESS, "");
	}
	
	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		if (!GoldIsMoney.hasBankSupport()) {
		    return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
		}
        if (!GoldIsMoney.bankExists(name)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
        }
        if (!GoldIsMoney.isBankMember(name, playerName)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That player is not a member of that bank!");
        }
        return new EconomyResponse(0, GoldIsMoney.bankBalance(name), ResponseType.SUCCESS, "");
	}
	
	@Override
	public List<String> getBanks() {
	    return GoldIsMoney.getBanks();
	}
	
	@Override
	public boolean createPlayerAccount(String playerName) {
	    return GoldIsMoney.createPlayerAccount(playerName);
	}
	
	public class EconomyServerListener implements Listener {
		Economy_GoldIsMoney2 economy = null;

		public EconomyServerListener(Economy_GoldIsMoney2 economy_GoldIsMoney2) {
			this.economy = economy_GoldIsMoney2;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			if (economy.economy == null) {
				Plugin ec = event.getPlugin();

				if (ec.getClass().getName().equals("com.flobi.GoldIsMoney2.GoldIsMoney")) {
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