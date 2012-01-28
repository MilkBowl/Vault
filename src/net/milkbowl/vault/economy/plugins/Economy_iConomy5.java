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
import org.bukkit.plugin.java.JavaPlugin;

import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.iConomy.system.Holdings;

public class Economy_iConomy5 implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private String name = "iConomy 5";
    private JavaPlugin plugin = null;
    protected iConomy economy = null;

    public Economy_iConomy5(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (economy == null) {
            Plugin ec = plugin.getServer().getPluginManager().getPlugin("iConomy");
            if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("com.iConomy.iConomy")) {
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

    private double getAccountBalance(String playerName) {
        return iConomy.getAccount(playerName).getHoldings().balance();
    }

    @Override
    public double getBalance(String playerName) {
        return getAccountBalance(playerName);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;

        Account account = iConomy.getAccount(playerName);
        Holdings holdings = account.getHoldings();
        if (holdings.hasEnough(amount)) {
            holdings.subtract(amount);
            balance = getAccountBalance(playerName);
            type = EconomyResponse.ResponseType.SUCCESS;
            return new EconomyResponse(amount, balance, type, errorMessage);
        } else {
            amount = 0;
            balance = getAccountBalance(playerName);
            type = EconomyResponse.ResponseType.FAILURE;
            errorMessage = "Insufficient funds";
            return new EconomyResponse(amount, balance, type, errorMessage);
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;

        Account account = iConomy.getAccount(playerName);
        Holdings holdings = account.getHoldings();
        holdings.add(amount);
        balance = getAccountBalance(playerName);
        type = EconomyResponse.ResponseType.SUCCESS;

        return new EconomyResponse(amount, balance, type, errorMessage);
    }

    public class EconomyServerListener implements Listener {
        Economy_iConomy5 economy = null;

        public EconomyServerListener(Economy_iConomy5 economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin ec = plugin.getServer().getPluginManager().getPlugin("iConomy");

                if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("com.iConomy.iConomy")) {
                    economy.economy = (iConomy) ec;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
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
    public String format(double amount) {
        return iConomy.format(amount);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy5 does not support single account banks!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy5 does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy5 does not support single bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy5 does not support single bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy5 does not support single bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy5 does not support single bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy5 does not support single bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy5 does not support single bank accounts!");
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
        return iConomy.hasAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        if (hasAccount(playerName)) {
            return false;
        }
        iConomy.getAccount(playerName);
        return true;
    }
}