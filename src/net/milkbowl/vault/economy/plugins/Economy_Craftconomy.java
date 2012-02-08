package net.milkbowl.vault.economy.plugins;

import java.util.List;
import java.util.logging.Logger;

import me.greatman.Craftconomy.Account;
import me.greatman.Craftconomy.AccountHandler;
import me.greatman.Craftconomy.Craftconomy;
import me.greatman.Craftconomy.CurrencyHandler;
import me.greatman.Craftconomy.utils.Config;
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

public class Economy_Craftconomy implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "Craftconomy";
    private JavaPlugin plugin = null;
    protected Craftconomy economy = null;

    public Economy_Craftconomy(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (economy == null) {
            Plugin ec = plugin.getServer().getPluginManager().getPlugin("Craftconomy");
            if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("me.greatman.Craftconomy.Craftconomy")) {
                economy = (Craftconomy) ec;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class EconomyServerListener implements Listener {
        Economy_Craftconomy economy = null;

        public EconomyServerListener(Economy_Craftconomy economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin ec = plugin.getServer().getPluginManager().getPlugin("Craftconomy");

                if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("me.greatman.Craftconomy.Craftconomy")) {
                    economy.economy = (Craftconomy) ec;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
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
        return Craftconomy.format(amount, CurrencyHandler.getCurrency(Config.currencyDefault, true));
    }

    @Override
    public double getBalance(String playerName) {
        if (AccountHandler.exists(playerName)) {
            return AccountHandler.getAccount(playerName).getDefaultBalance();
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
            return new EconomyResponse(0, account.getDefaultBalance(), ResponseType.FAILURE, "Insufficient funds");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Account account = AccountHandler.getAccount(playerName);
        account.addMoney(amount);
        return new EconomyResponse(amount, account.getDefaultBalance(), ResponseType.SUCCESS, null);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Craftconomy does not support Banks.");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Craftconomy does not support Banks.");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Craftconomy does not support Banks.");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Craftconomy does not support Banks.");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Craftconomy does not support Banks.");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Craftconomy does not support Banks.");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Craftconomy does not support Banks.");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Craftconomy does not support Banks.");
    }

    @Override
    public List<String> getBanks() {
        throw new UnsupportedOperationException("Craftconomy does not support listing of bank accounts");
    }

    @Override
    public boolean hasBankSupport() {
        return false;
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