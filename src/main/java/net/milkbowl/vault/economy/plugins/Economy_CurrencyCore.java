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

import is.currency.Currency;
import is.currency.syst.AccountContext;

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

public class Economy_CurrencyCore extends AbstractEconomy {

    private Currency currency;
    private static final Logger log = Logger.getLogger("Minecraft");
    private final Plugin plugin;
    private final String name = "CurrencyCore";

    public Economy_CurrencyCore(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if(currency == null) {
            Plugin currencyPlugin = plugin.getServer().getPluginManager().getPlugin("CurrencyCore");
            if(currencyPlugin != null && currencyPlugin.getClass().getName().equals("is.currency.Currency")) {
                this.currency = (Currency) currencyPlugin;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));  
            }
        }
    }

    public class EconomyServerListener implements Listener {

        private Economy_CurrencyCore economy = null;

        public EconomyServerListener(Economy_CurrencyCore economy) {
            this.economy = economy;     
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if(this.economy.currency == null) {
                Plugin currencyPlugin = event.getPlugin();
                
                if(currencyPlugin.getDescription().getName().equals("CurrencyCore") && currencyPlugin.getClass().getName().equals("is.currency.Currency")) {
                    this.economy.currency = (Currency) currencyPlugin;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), this.economy.getName()));  
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (this.economy.currency != null) {
                if (event.getPlugin().getDescription().getName().equals("CurrencyCore")) {
                    this.economy.currency = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), this.economy.getName()));
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return currency != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String format(double amount) {
        return this.currency.getFormatHelper().format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return currency.getCurrencyConfig().getCurrencyMajor().get(1);
    }

    @Override
    public String currencyNameSingular() {
        return currency.getCurrencyConfig().getCurrencyMajor().get(0);
    }

    @Override
    public double getBalance(String playerName) {
        AccountContext account = this.currency.getAccountManager().getAccount(playerName);
        if (account == null) {
            return 0.0;     
        }

        return account.getBalance();
    }

    @Override
    public boolean has(String playerName, double amount) {
        AccountContext account = this.currency.getAccountManager().getAccount(playerName);
        if (account == null) {
            return false;
        } else {
            return account.hasBalance(amount);
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
        }

        AccountContext account = this.currency.getAccountManager().getAccount(playerName);
        if (account == null) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "That account does not exist");
        } else if (!account.hasBalance(amount)) {
            return new EconomyResponse(0.0, account.getBalance(), ResponseType.FAILURE, "Insufficient funds");  
        } else {
            account.subtractBalance(amount);
            return new EconomyResponse(amount, account.getBalance(), ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds");
        }

        AccountContext account = this.currency.getAccountManager().getAccount(playerName);
        if (account == null) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "That account does not exist");
        }   
        account.addBalance(amount);
        return new EconomyResponse(amount, account.getBalance(), ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        if (this.currency.getAccountManager().hasAccount(name)) {
            return new EconomyResponse(0, currency.getAccountManager().getAccount(name).getBalance(), ResponseType.FAILURE, "That account already exists.");
        }
        this.currency.getAccountManager().createAccount(name);
        return new EconomyResponse(0, 0, ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        if (this.currency.getAccountManager().hasAccount(name)) {
            this.currency.getAccountManager().deleteAccount(name);
            return new EconomyResponse(0, 0, ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(0, 0, ResponseType.FAILURE, "That account does not exist!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        AccountContext account = this.currency.getAccountManager().getAccount(name);

        if (account == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That account does not exists.");
        }
        return new EconomyResponse(0, account.getBalance(), ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        AccountContext account = this.currency.getAccountManager().getAccount(name);
        if (account == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That account does not exist!");
        } else if (!account.hasBalance(amount)) {
            return new EconomyResponse(0, account.getBalance(), ResponseType.FAILURE, "That account does not have enough!");
        } else {
            return new EconomyResponse(0, account.getBalance(), ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
        }

        AccountContext account = this.currency.getAccountManager().getAccount(name);
        if (account == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That account does not exist!");
        } else if (!account.hasBalance(amount)) {
            return new EconomyResponse(0, account.getBalance(), ResponseType.FAILURE, "That account does not have enough!");
        } else {
            account.subtractBalance(amount);
            return new EconomyResponse(amount, account.getBalance(), ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds");
        }

        AccountContext account = this.currency.getAccountManager().getAccount(name);
        if (account == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That account does not exist!");
        } else {
            account.addBalance(amount);
            return new EconomyResponse(amount, account.getBalance(), ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Currency does not support Bank members.");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Currency does not support Bank members.");
    }

    @Override
    public List<String> getBanks() {
        return this.currency.getAccountManager().getAccountList();
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return this.currency.getAccountManager().getAccount(playerName) != null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        if (this.currency.getAccountManager().getAccount(playerName) != null) {
            return false;
        }
        this.currency.getAccountManager().createAccount(playerName);
        return true;
    }

	@Override
	public int fractionalDigits() {
		return -1;
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
