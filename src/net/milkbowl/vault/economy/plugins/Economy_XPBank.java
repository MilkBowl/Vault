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

import com.gmail.mirelatrue.xpbank.XPBank;

public class Economy_XPBank extends Economy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "XPBank";
    private Plugin plugin = null;
    private XPBank XPB = null;

    public Economy_XPBank(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
        
        // Load Plugin in case it was loaded before
        if (XPB == null) {
            Plugin economy = plugin.getServer().getPluginManager().getPlugin("XPBank");
            if (economy != null && economy.isEnabled()) {
                XPB = (XPBank) economy;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class EconomyServerListener implements Listener {
        Economy_XPBank economy = null;

        public EconomyServerListener(Economy_XPBank economy_XPBank) {
            this.economy = economy_XPBank;
        }
        

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.XPB == null) {
                Plugin eco = plugin.getServer().getPluginManager().getPlugin("XPBank");

                if (eco != null && eco.isEnabled()) {
                    economy.XPB = (XPBank) eco;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.XPB != null) {
                if (event.getPlugin().getDescription().getName().equals("XPBank")) {
                    economy.XPB = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return this.XPB != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String format(double amount) {
        return String.format("%d %s", (int) amount, XPBank.currencyName);
    }

    @Override
    public String currencyNamePlural() {
        return XPBank.currencyName;
    }

    @Override
    public String currencyNameSingular() {
        return XPBank.currencyName;
    }

    @Override
    public double getBalance(String playerName) {
        return XPB.getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        if (XPB.getBalance(playerName) >= (int) amount) { return true; }
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (!XPB.playerExists(playerName)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player doesn't exist");
        }
        
        int value = (int) amount;
        int balance = XPB.getBalance(playerName);

        if (value < 1) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, "Amount must be greater than zero");
        }

        if (value > balance) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, String.format("Insufficient %s", XPBank.currencyName));
        }

        XPB.addToBalance(playerName, -value);
        
        return new EconomyResponse(value, balance - value, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (!XPB.playerExists(playerName)) {
        	// Stupid plugins that use fake players without creating them first...
            // return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player doesn't exist");
        	this.createPlayerAccount(playerName);
        }
        
        int value = (int) amount;
        int balance = XPB.getBalance(playerName);

        if (value < 1) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, "Amount must be greater than zero");
        }

        XPB.addToBalance(playerName, value);

        return new EconomyResponse(value, balance + value, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
    	if (XPB.bankExists(name)) {
    		return new EconomyResponse(0, XPB.getBankBalance(name), ResponseType.FAILURE, "That account already exists");
    	}
    	
    	Boolean created = XPB.addBank(name);
    	
    	if (!created) {
    		return new EconomyResponse(0, 0, ResponseType.FAILURE, "There was an error creating the account");
    	}
    	
    	return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse deleteBank(String name) {
    	if (!XPB.bankExists(name)) {
    		return new EconomyResponse(0, 0, ResponseType.FAILURE, "There is no account with that name");
    	}
    	
    	Boolean deleted = XPB.deleteBank(name);
    	
    	if (!deleted) {
    		return new EconomyResponse(0, 0, ResponseType.FAILURE, "There was an error deleting the account");
    	}
    	
    	return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
    	if (!XPB.bankExists(name)) {
    		return new EconomyResponse(0, 0, ResponseType.FAILURE, "There is no account with that name");
    	}
    	
    	int balance = XPB.getBankBalance(name);
    	
    	if (balance >= (int) amount) {
    		return new EconomyResponse(0, balance, ResponseType.SUCCESS, null);
    	}
    	
    	return new EconomyResponse(0, balance, ResponseType.FAILURE, String.format("The account does not have that much %s", XPBank.currencyName));
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
    	if (!XPB.bankExists(name)) {
    		return new EconomyResponse(0, 0, ResponseType.FAILURE, "There is no account with that name");
    	}
    	
        int value = (int) amount;
        int balance = XPB.getBankBalance(name);

        if (value < 1) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, "Amount must be greater than zero");
        }

        if (value > balance) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, String.format("Insufficient %s", XPBank.currencyName));
        }

        XPB.addToBankBalance(name, -value);
        
        return new EconomyResponse(value, balance - value, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
    	if (!XPB.bankExists(name)) {
    		return new EconomyResponse(0, 0, ResponseType.FAILURE, "There is no account with that name");
    	}
    	
    	int value = (int) amount;
        int balance = XPB.getBankBalance(name);

        if (value < 1) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, "Amount must be greater than zero");
        }

        XPB.addToBankBalance(name, value);

        return new EconomyResponse(value, balance + value, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "XPBank does not support assigning bank owners");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
    	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "XPBank does not support assigning bank members");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
    	if (!XPB.bankExists(name)) {
    		return new EconomyResponse(0, 0, ResponseType.FAILURE, "There is no account with that name");
    	}
    	
    	return new EconomyResponse(0, XPB.getBankBalance(name), ResponseType.SUCCESS, null);
    }

    @Override
    public List<String> getBanks() {
        return XPB.getBanks();
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return XPB.playerExists(playerName);
    }
    
    @Override
    public boolean createPlayerAccount(String playerName) {
    	return XPB.addPlayer(playerName);
    }

	@Override
	public int fractionalDigits() {
		return 0;
	}
}