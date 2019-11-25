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

import de.thejeterlp.onlineconomy.OnlineConomy;
import de.thejeterlp.onlineconomy.Utils;
import de.thejeterlp.onlineconomy.api.OnlineConomyHook; 
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Economy_OnlineConomy extends AbstractEconomy {

    private final Logger log;
    private final String name = "OnlineConomy";
    private Plugin plugin = null;
    private OnlineConomy econ = null;

    public Economy_OnlineConomy(Plugin plugin) {
        this.plugin = plugin;
        this.log = plugin.getLogger();
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (econ == null) {
            Plugin econ = plugin.getServer().getPluginManager().getPlugin("OnlineConomy");
            if (econ != null && econ.isEnabled()) {
                this.econ = (OnlineConomy) econ;
                log.info(String.format("[Economy] %s hooked.", name));
            }
        }
    }

    public class EconomyServerListener implements Listener {

        Economy_OnlineConomy economy = null;

        public EconomyServerListener(Economy_OnlineConomy economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.econ == null) {
                Plugin eco = event.getPlugin();

                if (eco.getDescription().getName().equals("MineConomy-2")) {
                    economy.econ = (OnlineConomy) eco;
                    log.info(String.format("[Economy] %s hooked.", economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.econ != null) {
                if (event.getPlugin().getDescription().getName().equals("MineConomy-2")) {
                    economy.econ = null;
                    log.info(String.format("[Economy] %s unhooked.", economy.name));
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
        return "OnlineConomy";
    }

    @Override
    public String format(double amount) {
        return Utils.format(amount);
    }

    @Override
    public String currencyNameSingular() {

        return OnlineConomyHook.getCurrencyName();
    }

    @Override
    public String currencyNamePlural() {
        return OnlineConomyHook.getCurrencyName();
    }

    @Override
    public double getBalance(String playerName) {
        return OnlineConomyHook.getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return OnlineConomyHook.canAfford(playerName, amount);

    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        double balance = OnlineConomyHook.getBalance(playerName);

        if (amount < 0.0D) {
            return new EconomyResponse(0.0D, balance, ResponseType.FAILURE, "Cannot withdraw negative funds");
        }

        if (balance >= amount) {
            double finalBalance = balance - amount;
            OnlineConomyHook.setBalance(playerName, finalBalance);
            return new EconomyResponse(amount, finalBalance, ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(0.0D, balance, ResponseType.FAILURE, "Insufficient funds");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        double balance = OnlineConomyHook.getBalance(playerName);

        if (amount < 0.0D) {
            return new EconomyResponse(0.0D, 0.0, ResponseType.FAILURE, "Cannot deposit negative funds");
        }

        balance += amount;
        OnlineConomyHook.setBalance(playerName, balance);
        return new EconomyResponse(amount, balance, ResponseType.SUCCESS, null);

    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0.0D, 0.0D, ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
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
    public boolean hasAccount(String playerName) {
        return OnlineConomyHook.hasAccount(name);
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        OnlineConomyHook.create(playerName);
        return true;

    }

    @Override
    public int fractionalDigits() {
        return 2;
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
