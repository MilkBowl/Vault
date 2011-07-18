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

import me.ic3d.eco.ECO;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class Economy_3co implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");
    
    private String name = "3co";
    private Plugin plugin = null;
    private PluginManager pluginManager = null;
    private ECO economy = null;
    private EconomyServerListener economyServerListener = null;

    public Economy_3co(Plugin plugin) {
        this.plugin = plugin;
        pluginManager = this.plugin.getServer().getPluginManager();

        economyServerListener = new EconomyServerListener(this);

        this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, economyServerListener, Priority.Monitor, plugin);
        this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, economyServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (economy == null) {
            Plugin econ = plugin.getServer().getPluginManager().getPlugin("3co");
            if (econ != null && econ.isEnabled()) {
                economy = (ECO) econ;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        if(economy == null) {
            return false;
        } else {
            return economy.isEnabled();
        }
    }

    @Override
    public double getBalance(String playerName) {
        final double balance;
        
        balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));

        final double fBalance = balance;
        return fBalance;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;
        
        if(amount < 0) {
            errorMessage = "Cannot withdraw negative funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));
            
            return new EconomyResponse(balance, balance, type, errorMessage);
        }
        
        amount = Math.ceil(amount);
        balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));
        if(balance - amount < 0) {
            errorMessage = "Insufficient funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));
            
            return new EconomyResponse(balance, balance, type, errorMessage);
        }
        economy.setMoney(plugin.getServer().getPlayer(playerName), (int) (balance - amount));
        type = EconomyResponse.ResponseType.SUCCESS;
        balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));

        return new EconomyResponse(amount, balance, type, errorMessage);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;
        
        if(amount < 0) {
            errorMessage = "Cannot deposit negative funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));
            
            return new EconomyResponse(balance, balance, type, errorMessage);
        }
        amount = Math.ceil(amount);
        balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));
        economy.setMoney(plugin.getServer().getPlayer(playerName), (int) (balance + amount));
        type = EconomyResponse.ResponseType.SUCCESS;
        balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));

        return new EconomyResponse(amount, balance, type, errorMessage);
    }

    public String getMoneyNamePlural() {
        return economy.getPluralCurrency();
    }

    public String getMoneyNameSingular() {
        return economy.getSingularCurrency();
    }
    
    private class EconomyServerListener extends ServerListener {
        Economy_3co economy = null;
        
        public EconomyServerListener(Economy_3co economy) {
            this.economy = economy;
        }
        
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin eco = plugin.getServer().getPluginManager().getPlugin("3co");

                if (eco != null && eco.isEnabled()) {
                    economy.economy = (ECO) eco;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
        
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("Essentials")) {
                    economy.economy = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }

    @Override
    public String format(double amount) {
        if (amount == 1) {
            return String.format("%f %s", amount, getMoneyNameSingular());
        } else {
            return String.format("%f %s", amount, getMoneyNamePlural());
        }
    }
}
