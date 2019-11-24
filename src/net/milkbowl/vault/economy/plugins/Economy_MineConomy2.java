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

import de.thejeterlp.mineconomy.MineConomy;
import de.thejeterlp.mineconomy.Utils;
import de.thejeterlp.mineconomy.api.MineConomyHook;
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

public class Economy_MineConomy2 extends AbstractEconomy {

    private final Logger log;
    private final String name = "MineConomy-2";
    private Plugin plugin = null;
    private MineConomy econ = null;

    public Economy_MineConomy2(Plugin plugin) {
        this.plugin = plugin;
        this.log = plugin.getLogger();
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (econ == null) {
            Plugin econ = plugin.getServer().getPluginManager().getPlugin("MineConomy-2");
            if (econ != null && econ.isEnabled()) {
                this.econ = (MineConomy) econ;
                log.info(String.format("[Economy] %s hooked.", name));
            }
        }
    }

    public class EconomyServerListener implements Listener {

        Economy_MineConomy2 economy = null;

        public EconomyServerListener(Economy_MineConomy2 economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.econ == null) {
                Plugin eco = event.getPlugin();

                if (eco.getDescription().getName().equals("MineConomy-2")) {
                    economy.econ = (MineConomy) eco;
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
        return "MineConomy 2";
    }

    @Override
    public String format(double amount) {
        return Utils.format(amount);
    }

    @Override
    public String currencyNameSingular() {

        return MineConomyHook.getCurrencyName();
    }

    @Override
    public String currencyNamePlural() {
        return MineConomyHook.getCurrencyName();
    }

    @Override
    public double getBalance(String playerName) {
        return MineConomyHook.getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return MineConomyHook.canAfford(playerName, amount);

    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        double balance = MineConomyHook.getBalance(playerName);

        if (amount < 0.0D) {
            return new EconomyResponse(0.0D, balance, ResponseType.FAILURE, "Cannot withdraw negative funds");
        }

        if (balance >= amount) {
            double finalBalance = balance - amount;
            MineConomyHook.setBalance(playerName, finalBalance);
            return new EconomyResponse(amount, finalBalance, ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(0.0D, balance, ResponseType.FAILURE, "Insufficient funds");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        double balance = MineConomyHook.getBalance(playerName);

        if (amount < 0.0D) {
            return new EconomyResponse(0.0D, 0.0, ResponseType.FAILURE, "Cannot deposit negative funds");
        }

        balance += amount;
        MineConomyHook.setBalance(playerName, balance);
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
        return MineConomyHook.hasAccount(name);
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        MineConomyHook.create(playerName);
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
