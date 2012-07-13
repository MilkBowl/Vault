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

import me.mjolnir.mineconomy.MineConomy;
import me.mjolnir.mineconomy.exceptions.AccountNameConflictException;
import me.mjolnir.mineconomy.exceptions.NoAccountException;
import me.mjolnir.mineconomy.internal.MCCom;
import me.mjolnir.mineconomy.internal.util.MCFormat;
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

public class Economy_MineConomy implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "MineConomy";
    private Plugin plugin = null;
    private MineConomy econ = null;

    public Economy_MineConomy(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (econ == null) {
            Plugin econ = plugin.getServer().getPluginManager().getPlugin("MineConomy");
            if (econ != null && econ.isEnabled()) {
                this.econ = (MineConomy) econ;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class EconomyServerListener implements Listener {
        Economy_MineConomy economy = null;

        public EconomyServerListener(Economy_MineConomy economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.econ == null) {
                Plugin eco = plugin.getServer().getPluginManager().getPlugin("MineConomy");

                if (eco != null && eco.isEnabled()) {
                    economy.econ = (MineConomy) eco;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.econ != null) {
                if (event.getPlugin().getDescription().getName().equals("MineConomy")) {
                    economy.econ = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }

    public boolean isEnabled() {
        return econ != null;
    }

    public String getName() {
        return "MineConomy";
    }

    public String format(double amount) {
        return MCFormat.format(amount);
    }

    public String currencyNameSingular() {
        return "";
    }

    public String currencyNamePlural() {
        return "";
    }

    public double getBalance(String playerName) {
        try
        {
            return MCCom.getVaultBalance(playerName);
        }
        catch (NoAccountException e)
        {
            MCCom.create(playerName);
            return MCCom.getVaultBalance(playerName);
        }
    }

    @Override
    public boolean has(String playerName, double amount) {
        try {
            return MCCom.canVaultAfford(playerName, amount);
        } catch(NoAccountException e) {
            MCCom.create(playerName);
            return MCCom.canVaultAfford(playerName, amount);
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {      
        double balance;
        try {
            balance = MCCom.getVaultBalance(playerName);
        } catch (NoAccountException e) {
            MCCom.create(playerName);
            balance = MCCom.getVaultBalance(playerName);
        }

        if(amount < 0.0D) {
            return new EconomyResponse(0.0D, balance, ResponseType.FAILURE, "Cannot withdraw negative funds");
        }

        if(balance >= amount) {
            double finalBalance = balance - amount;
            MCCom.setVaultBalance(playerName, finalBalance);
            return new EconomyResponse(amount, finalBalance, ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(0.0D, balance, ResponseType.FAILURE, "Insufficient funds");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        double balance;
        try {
            balance = MCCom.getVaultBalance(playerName);
        } catch (NoAccountException e) {
            MCCom.create(playerName);
            balance = MCCom.getVaultBalance(playerName);
        }
        if(amount < 0.0D) {
            return new EconomyResponse(0.0D, 0.0, ResponseType.FAILURE, "Cannot deposit negative funds");
        }

        balance += amount;
        MCCom.setVaultBalance(playerName, balance);
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
        return new ArrayList<String>();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return MCCom.exists(playerName);
    }

    public boolean createPlayerAccount(String playerName) {
        try {
            MCCom.create(playerName);
            return true;
        } catch(AccountNameConflictException e) {
            return false;
        }
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }
}