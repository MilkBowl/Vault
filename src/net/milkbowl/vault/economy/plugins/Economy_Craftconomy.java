package net.milkbowl.vault.economy.plugins;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.greatman.Craftconomy.Account;
import me.greatman.Craftconomy.AccountHandler;
import me.greatman.Craftconomy.Craftconomy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_Craftconomy implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private String name = "Craftconomy";
    private JavaPlugin plugin = null;
    private PluginManager pluginManager = null;
    protected Craftconomy economy = null;
    private EconomyServerListener economyServerListener = null;

    public Economy_Craftconomy(JavaPlugin plugin) {
        this.plugin = plugin;
        this.pluginManager = this.plugin.getServer().getPluginManager();

        economyServerListener = new EconomyServerListener(this);

        this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, economyServerListener, Priority.Monitor, plugin);
        this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, economyServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (economy == null) {
            Plugin ec = plugin.getServer().getPluginManager().getPlugin("Craftconomy");
            if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("me.greatman.Craftconomy.Craftconomy")) {
                economy = (Craftconomy) ec;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    private class EconomyServerListener extends ServerListener {
        Economy_Craftconomy economy = null;

        public EconomyServerListener(Economy_Craftconomy economy) {
            this.economy = economy;
        }

        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin ec = plugin.getServer().getPluginManager().getPlugin("Craftconomy");

                if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("me.greatman.Craftconomy.Craftconomy")) {
                    economy.economy = (Craftconomy) ec;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("Craftconomy")) {
                    economy.economy = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
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
        return Craftconomy.format(amount);
    }

    @Override
    public double getBalance(String playerName) {
        if (AccountHandler.exists(playerName)) {
            return AccountHandler.getAccount(playerName).getBalance();
        } else {
            return 0;
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        double balance;
        Account account = AccountHandler.getAccount(playerName);
        if (account.hasEnough(amount)) {
            balance = account.substractMoney(amount);
            return new EconomyResponse(amount, balance, ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(0, account.getBalance(), ResponseType.FAILURE, "Insufficient funds");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Account account = AccountHandler.getAccount(playerName);
        account.addMoney(amount);
        return new EconomyResponse(amount, account.getBalance(), ResponseType.SUCCESS, null);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        if (AccountHandler.exists(name)) {
            return new EconomyResponse(0, AccountHandler.getAccount(player).getBalance(), ResponseType.FAILURE, "That account already exists.");
        }
        AccountHandler.getAccount(name);
        return new EconomyResponse(0, 0, ResponseType.SUCCESS, "");

    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        if (!AccountHandler.exists(name)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Account does not exists!");
        }
        
        double balance = AccountHandler.getAccount(name).getBank().getBalance();
        if ( balance >= amount) {
            return new EconomyResponse(0, balance, ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, "The account does not have enough!");
        }
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        double balance;
        if (!AccountHandler.exists(name)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Account does not exists!");
        }

        Account account = AccountHandler.getAccount(name);
        if (account.getBank().hasEnough(amount)) {
            balance = account.getBank().substractMoney(amount);
            return new EconomyResponse(amount, balance, ResponseType.SUCCESS, "");
        } else {
            balance = account.getBank().getBalance();
            return new EconomyResponse(0, balance, ResponseType.FAILURE, "Insufficient funds");
        }
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        if (!AccountHandler.exists(name)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Account does not exists!");
        }
        Account account = AccountHandler.getAccount(name);
        return new EconomyResponse(amount, account.getBank().addMoney(amount), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Craftconomy does not support Bank owners.");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Craftconomy does not support Bank members.");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        if (!AccountHandler.exists(name)) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "There is no bank account with that name");
        } else {
            return new EconomyResponse(0, AccountHandler.getAccount(name).getBank().getBalance(), ResponseType.SUCCESS, null);
        }
    }

    @Override
    public List<String> getBanks() {
        throw new UnsupportedOperationException("Craftconomy does not support listing of bank accounts");
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return AccountHandler.exists(playerName);
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        if (AccountHandler.exists(playerName)) {
            return false;
        }
        AccountHandler.getAccount(playerName);
        return true;
    }
}