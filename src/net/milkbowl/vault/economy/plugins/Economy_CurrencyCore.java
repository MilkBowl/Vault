package net.milkbowl.vault.economy.plugins;

import is.currency.Currency;
import is.currency.syst.AccountContext;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_CurrencyCore implements Economy {

    private Currency currency;
    private static final Logger log = Logger.getLogger("Minecraft");
    private final Plugin plugin;
    private EconomyServerListener economyServerListener = null;
    private final String name = "CurrencyCore";

    public Economy_CurrencyCore(Plugin plugin) {
        this.plugin = plugin;
        economyServerListener = new EconomyServerListener(this);

        this.plugin.getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE, economyServerListener, Priority.Monitor, plugin);
        this.plugin.getServer().getPluginManager().registerEvent(Type.PLUGIN_DISABLE, economyServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if(currency == null) {
            Plugin currencyPlugin = plugin.getServer().getPluginManager().getPlugin("CurrencyCore");
            if(currencyPlugin != null && currencyPlugin.getClass().getName().equals("is.currency.Currency")) {
                this.currency = (Currency) currencyPlugin;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));  
            }
        }
    }

    private class EconomyServerListener extends ServerListener {

        private Economy_CurrencyCore economy = null;

        public EconomyServerListener(Economy_CurrencyCore economy) {
            this.economy = economy;     
        }

        public void onPluginEnable(PluginEnableEvent event) {
            if(this.economy.currency == null) {
                Plugin currencyPlugin = plugin.getServer().getPluginManager().getPlugin("CurrencyCore");
                if(currencyPlugin != null && currencyPlugin.getClass().getName().equals("is.currency.Currency")) {
                    this.economy.currency = (Currency) currencyPlugin;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), this.economy.getName()));  
                }
            }
        }

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
    public double getBalance(String playerName) {
        AccountContext account = this.currency.getAccountManager().getAccount(playerName);
        if (account == null)
            return 0.0;     

        return account.getBalance();
    }

    @Override
    public boolean has(String playerName, double amount) {
        AccountContext account = this.currency.getAccountManager().getAccount(playerName);
        if (account == null)
            return false;
        else
            return account.hasBalance(amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
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
        return Arrays.asList(this.currency.getAccountManager().getAccounts());
    }
    
    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return this.currency.getAccountManager().getAccount(playerName) != null;
    }
}
