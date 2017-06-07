/*
 * This file is part of Vault.
 *
 * Copyright (c) 2017 Lukas Nehrke
 *
 * Vault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Vault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.milkbowl.vault.economy.plugins;

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

import com.gmail.mirelatrue.xpbank.API;
import com.gmail.mirelatrue.xpbank.Account;
import com.gmail.mirelatrue.xpbank.GroupBank;
import com.gmail.mirelatrue.xpbank.XPBank;

public class Economy_XPBank extends AbstractEconomy {

    private static final Logger log = Logger.getLogger("Minecraft");


    private final String name = "XPBank";
    private Plugin plugin = null;
    private XPBank XPB = null;
    private API api = null;

    public Economy_XPBank (Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (XPB == null) {
            Plugin economy = plugin.getServer().getPluginManager().getPlugin("XPBank");
            if (economy != null && economy.isEnabled()) {
                XPB = (XPBank) economy;
                api = XPB.getAPI();
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class EconomyServerListener implements Listener {
        Economy_XPBank economy = null;

        public EconomyServerListener (Economy_XPBank economy_XPBank) {
            this.economy = economy_XPBank;
        }

        @EventHandler (priority = EventPriority.MONITOR)
        public void onPluginEnable (PluginEnableEvent event) {
            if (economy.XPB == null) {
                Plugin eco = event.getPlugin();

                if (eco.getDescription().getName().equals("XPBank")) {
                    economy.XPB = (XPBank) eco;
                    api = XPB.getAPI();
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler (priority = EventPriority.MONITOR)
        public void onPluginDisable (PluginDisableEvent event) {
            if (economy.XPB != null) {
                if (event.getPlugin().getDescription().getName().equals("XPBank")) {
                    economy.XPB = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }

    @Override
    public boolean isEnabled () {
        return this.XPB != null;
    }

    @Override
    public String getName () {
        return name;
    }

    @Override
    public boolean hasBankSupport () {
        return true;
    }

    @Override
    public int fractionalDigits () {
        return 0;
    }

    @Override
    public String format (double amount) {
        return String.format("%d %s", (int) amount, api.currencyName((int) amount));
    }

    @Override
    public String currencyNamePlural () {
        return api.getMsg("CurrencyNamePlural");
    }

    @Override
    public String currencyNameSingular () {
        return api.getMsg("currencyName");
    }

    @Override
    public boolean hasAccount (String playerName) {
        Account account = api.getAccount(playerName);

        if (account != null) {
            return true;
        }

        return false;
    }

    @Override
    public double getBalance (String playerName) {
        Account account = api.getAccount(playerName);

        return account.getBalance();
    }

    @Override
    public boolean has (String playerName, double amount) {
        Account account = api.getAccount(playerName);

        if (account.getBalance() >= (int) amount) {
            return true;
        }

        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer (String playerName, double amount) {
        Account account = api.getAccount(playerName);

        if (account == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, api.getMsg("Player doesn't exist."));
        }

        int value = (int) amount;
        int balance = account.getBalance();

        if (value < 1) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, api.getMsg("LessThanZero"));
        }

        if (value > balance) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, String.format(api.getMsg("InsufficientXP"), api.currencyName(value)));
        }

        account.modifyBalance(-value);

        return new EconomyResponse(value, balance - value, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer (String playerName, double amount) {
        Account account = api.getAccount(playerName);

        if (account == null) {
            // Stupid plugins that use fake players without creating them first...
            // return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player doesn't exist");
            this.createPlayerAccount(playerName);
        }

        int value = (int) amount;
        int balance = account.getBalance();

        if (value < 1) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, api.getMsg("LessThanZero"));
        }

        account.addTaxableIncome(value);

        return new EconomyResponse(value, balance + value, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse createBank (String name, String player) {
        GroupBank groupBank = api.getGroupBank(name);

        if (groupBank != null) {
            return new EconomyResponse(0, groupBank.getBalance(), ResponseType.FAILURE, String.format(api.getMsg("GroupBankExists"), name));
        }

        Account account = api.getAccount(player);

        groupBank = api.createGroupBank(name, account);

        return new EconomyResponse(0, groupBank.getBalance(), ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse deleteBank (String name) {
        GroupBank groupBank = api.getGroupBank(name);

        if (groupBank == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, api.getMsg("GroupBankNotExists"));
        }

        api.deleteGroupBank(groupBank, String.format(api.getMsg("Disbanded"), groupBank.getName()));

        return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse bankBalance (String name) {
        GroupBank groupBank = api.getGroupBank(name);

        if (groupBank == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, api.getMsg("GroupBankNotExists"));
        }

        return new EconomyResponse(0, groupBank.getBalance(), ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse bankHas (String name, double amount) {
        GroupBank groupBank = api.getGroupBank(name);

        if (groupBank == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, api.getMsg("GroupBankNotExists"));
        }

        int value = (int) amount;
        int balance = groupBank.getBalance();

        if (balance >= value) {
            return new EconomyResponse(0, balance, ResponseType.SUCCESS, null);
        }

        return new EconomyResponse(0, balance, ResponseType.FAILURE, String.format(api.getMsg("InsufficientXP"), api.currencyName(value)));
    }

    @Override
    public EconomyResponse bankWithdraw (String name, double amount) {
        GroupBank groupBank = api.getGroupBank(name);

        if (groupBank == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, api.getMsg("GroupBankNotExists"));
        }

        int value = (int) amount;
        int balance = groupBank.getBalance();

        if (value < 1) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, api.getMsg("LessThanZero"));
        }

        if (value > balance) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, String.format(api.getMsg("InsufficientXP"), api.currencyName(value)));
        }

        groupBank.modifyBalance(-value);

        return new EconomyResponse(value, balance - value, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse bankDeposit (String name, double amount) {
        GroupBank groupBank = api.getGroupBank(name);

        if (groupBank == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, api.getMsg("GroupBankNotExists"));
        }

        int value = (int) amount;
        int balance = groupBank.getBalance();

        if (value < 1) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, api.getMsg("LessThanZero"));
        }

        groupBank.modifyBalance(value);

        return new EconomyResponse(value, balance + value, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse isBankOwner (String name, String playerName) {
        GroupBank groupBank = api.getGroupBank(name);

        if (groupBank == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, api.getMsg("GroupBankNotExists"));
        }

        Account account = api.getAccount(name);

        if (account == null) {
            return new EconomyResponse(0, groupBank.getBalance(), ResponseType.FAILURE, api.getMsg("PlayerNotExist"));
        }

        if (groupBank.getOwner().equalsIgnoreCase(name)) {
            return new EconomyResponse(0, groupBank.getBalance(), ResponseType.SUCCESS, null);
        }

        return new EconomyResponse(0, groupBank.getBalance(), ResponseType.FAILURE, String.format(api.getMsg("PlayerNotOwner"), account.getName(), groupBank.getName()));
    }

    @Override
    public EconomyResponse isBankMember (String name, String playerName) {
        GroupBank groupBank = api.getGroupBank(name);

        if (groupBank == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, api.getMsg("GroupBankNotExists"));
        }

        Account account = api.getAccount(name);

        if (account == null) {
            return new EconomyResponse(0, groupBank.getBalance(), ResponseType.FAILURE, api.getMsg("PlayerNotExist"));
        }

        if (groupBank.groupMembers.getMembers().containsKey(playerName)) {
            return new EconomyResponse(0, groupBank.getBalance(), ResponseType.SUCCESS, null);
        }

        return new EconomyResponse(0, groupBank.getBalance(), ResponseType.FAILURE, String.format(api.getMsg("NotAMemberOf"), groupBank.getName(), account.getName()));
    }

    @Override
    public List<String> getBanks () {
        return api.getAllGroupBanks();
    }

    @Override
    public boolean createPlayerAccount (String playerName) {
        api.createAccount(playerName);

        return true;
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
