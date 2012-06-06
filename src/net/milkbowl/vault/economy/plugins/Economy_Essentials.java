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

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

public class Economy_Essentials implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "Essentials Economy";
    private Plugin plugin = null;
    private Essentials ess = null;

    public Economy_Essentials(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (ess == null) {
            Plugin essentials = plugin.getServer().getPluginManager().getPlugin("Essentials");
            if (essentials != null && essentials.isEnabled()) {
                ess = (Essentials) essentials;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        if (ess == null) {
            return false;
        } else {
            return ess.isEnabled();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getBalance(String playerName) {
        double balance;

        try {
            balance = com.earth2me.essentials.api.Economy.getMoney(playerName);
        } catch (UserDoesNotExistException e) {
            createPlayerAccount(playerName);
            balance = 0;
        }

        return balance;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        if (hasAccount(playerName)) {
            return false;
        }
        return com.earth2me.essentials.api.Economy.createNPC(playerName);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
        }
        
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;

        try {
            com.earth2me.essentials.api.Economy.subtract(playerName, amount);
            balance = com.earth2me.essentials.api.Economy.getMoney(playerName);
            type = EconomyResponse.ResponseType.SUCCESS;
        } catch (UserDoesNotExistException e) {
            if (createPlayerAccount(playerName)) {
                return withdrawPlayer(playerName, amount);
            } else {
                amount = 0;
                balance = 0;
                type = EconomyResponse.ResponseType.FAILURE;
                errorMessage = "User does not exist";
            }
        } catch (NoLoanPermittedException e) {
            try {
                balance = com.earth2me.essentials.api.Economy.getMoney(playerName);
                amount = 0;
                type = EconomyResponse.ResponseType.FAILURE;
                errorMessage = "Loan was not permitted";
            } catch (UserDoesNotExistException e1) {
                amount = 0;
                balance = 0;
                type = EconomyResponse.ResponseType.FAILURE;
                errorMessage = "User does not exist";
            }
        }

        return new EconomyResponse(amount, balance, type, errorMessage);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds");
        }
        
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;

        try {
            com.earth2me.essentials.api.Economy.add(playerName, amount);
            balance = com.earth2me.essentials.api.Economy.getMoney(playerName);
            type = EconomyResponse.ResponseType.SUCCESS;
        } catch (UserDoesNotExistException e) {
            if (createPlayerAccount(playerName)) {
                return depositPlayer(playerName, amount);
            } else {
                amount = 0;
                balance = 0;
                type = EconomyResponse.ResponseType.FAILURE;
                errorMessage = "User does not exist";
            }
        } catch (NoLoanPermittedException e) {
            try {
                balance = com.earth2me.essentials.api.Economy.getMoney(playerName);
                amount = 0;
                type = EconomyResponse.ResponseType.FAILURE;
                errorMessage = "Loan was not permitted";
            } catch (UserDoesNotExistException e1) {
                balance = 0;
                amount = 0;
                type = EconomyResponse.ResponseType.FAILURE;
                errorMessage = "Loan was not permitted";
            }
        }

        return new EconomyResponse(amount, balance, type, errorMessage);
    }

    public class EconomyServerListener implements Listener {
        Economy_Essentials economy = null;

        public EconomyServerListener(Economy_Essentials economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.ess == null) {
                Plugin essentials = plugin.getServer().getPluginManager().getPlugin("Essentials");

                if (essentials != null && essentials.isEnabled()) {
                    economy.ess = (Essentials) essentials;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.ess != null) {
                if (event.getPlugin().getDescription().getName().equals("Essentials")) {
                    economy.ess = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }

    @Override
    public String format(double amount) {
        return com.earth2me.essentials.api.Economy.format(amount);
    }

    @Override
    public String currencyNameSingular() {
        return "";
    }

    @Override
    public String currencyNamePlural() {
        return "";
    }

    @Override
    public boolean has(String playerName, double amount) {
        try {
            return com.earth2me.essentials.api.Economy.hasEnough(playerName, amount);
        } catch (UserDoesNotExistException e) {
            return false;
        }
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
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
        return com.earth2me.essentials.api.Economy.playerExists(playerName);
    }
}