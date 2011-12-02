/**
 * Copyright (C) 2011 Morgan Humes <morgan@lanaddict.com>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

package net.milkbowl.vault.economy.plugins;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;

public class Economy_iConomy4 implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private String name = "iConomy 4";
    private Plugin plugin = null;
    private PluginManager pluginManager = null;
    protected iConomy economy = null;
    private EconomyServerListener economyServerListener = null;

    public Economy_iConomy4(Plugin plugin) {
        this.plugin = plugin;
        this.pluginManager = this.plugin.getServer().getPluginManager();

        economyServerListener = new EconomyServerListener(this);

        this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, economyServerListener, Priority.Monitor, plugin);
        this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, economyServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (economy == null) {
            Plugin ec = plugin.getServer().getPluginManager().getPlugin("iConomy");
            if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("com.nijiko.coelho.iConomy.iConomy.class")) {
                economy = (iConomy) ec;
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
    public String format(double amount) {
        return iConomy.getBank().format(amount);
    }

    public String getMoneyNamePlural() {
        return iConomy.getBank().getCurrency() + "s";
    }

    public String getMoneyNameSingular() {
        return iConomy.getBank().getCurrency();
    }

    @Override
    public double getBalance(String playerName) {
        final double balance;

        balance = getAccountBalance(playerName);

        final double fBalance = balance;
        return fBalance;
    }

    private double getAccountBalance(String playerName) {
        Account account = iConomy.getBank().getAccount(playerName);
        if (account == null) {
            iConomy.getBank().addAccount(playerName);
            account = iConomy.getBank().getAccount(playerName);
        }
        return account.getBalance();
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;

        if (amount < 0) {
            errorMessage = "Cannot withdraw negative funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = getAccountBalance(playerName);

            return new EconomyResponse(amount, balance, type, errorMessage);
        }
        balance = getAccountBalance(playerName);
        if (balance >= amount) {
            Account account = iConomy.getBank().getAccount(playerName);
            if (account == null) {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not find account");
            }
            account.subtract(amount);

            type = EconomyResponse.ResponseType.SUCCESS;
            balance = getAccountBalance(playerName);

            return new EconomyResponse(amount, balance, type, errorMessage);
        } else {
            errorMessage = "Error withdrawing funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = getAccountBalance(playerName);

            return new EconomyResponse(amount, balance, type, errorMessage);
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;

        if (amount < 0) {
            errorMessage = "Cannot deposit negative funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = getAccountBalance(playerName);

            return new EconomyResponse(amount, balance, type, errorMessage);
        }

        Account account = iConomy.getBank().getAccount(playerName);
        if (account == null) {
            iConomy.getBank().addAccount(playerName);
            account = iConomy.getBank().getAccount(playerName);
        }
        account.add(amount);
        balance = getAccountBalance(playerName);
        type = EconomyResponse.ResponseType.SUCCESS;

        return new EconomyResponse(amount, balance, type, errorMessage);
    }

    private class EconomyServerListener extends ServerListener {
        Economy_iConomy4 economy = null;

        public EconomyServerListener(Economy_iConomy4 economy) {
            this.economy = economy;
        }

        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin iConomy = plugin.getServer().getPluginManager().getPlugin("iConomy");

                if (iConomy != null && iConomy.isEnabled() && iConomy.getClass().getName().equals("com.nijiko.coelho.iConomy.iConomy")) {
                    economy.economy = (iConomy) iConomy;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("iConomy")) {
                    economy.economy = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }

	@Override
	public boolean has(String playerName, double amount) {
		return getBalance(playerName) >= amount;
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy4 does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy4 does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy4 does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy4 does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy4 does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy4 does not support bank accounts!");
	}
	
	public EconomyResponse remove(String name, String playerName){
		iConomy.getBank().getAccount(name).remove();
		if(iConomy.getBank().hasAccount(name)){
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "There was an error removing the account");
		}else{
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, "");
		}
	}
}