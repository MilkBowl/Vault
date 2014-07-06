package net.milkbowl.vault.economy.plugins;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.royaldev.royaleconomy.RoyalEconomy;
import org.royaldev.royaleconomy.RoyalEconomyAPI.Account;

import java.util.List;
import java.util.logging.Logger;

public class Economy_RoyalEconomy extends AbstractEconomy {

    private static final Logger log = Logger.getLogger("Minecraft");
    protected RoyalEconomy economy = null;
    private String name = "RoyalEconomy ";
    private Plugin plugin = null;

    public Economy_RoyalEconomy(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
        if (this.economy == null) {
            Plugin ec = plugin.getServer().getPluginManager().getPlugin("RoyalEconomy");
            if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("org.royaldev.royaleconomy.RoyalEconomy")) {
                String version = ec.getDescription().getVersion();
                this.name += version;
                this.economy = (RoyalEconomy) ec;
                Economy_RoyalEconomy.log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), this.name));
            }
        }
    }

    private OfflinePlayer getOfflinePlayer(String s) {
        return this.plugin.getServer().getOfflinePlayer(s);
    }

    @Override
    public boolean isEnabled() {
        return this.economy != null && this.economy.isEnabled();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double v) {
        return this.economy.getAPI().getFormattedAmount(v);
    }

    @Override
    public String currencyNamePlural() {
        return this.economy.getAPI().getMajorCurrencyNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return this.economy.getAPI().getMajorCurrencyNameSingular();
    }

    @Override
    public boolean hasAccount(String s) {
        return this.hasAccount(this.getOfflinePlayer(s));
    }

    @Override
    public boolean hasAccount(String s, String s2) {
        return this.hasAccount(s);
    }

    @Override
    public double getBalance(String s) {
        return this.getBalance(this.getOfflinePlayer(s));
    }

    @Override
    public double getBalance(String s, String s2) {
        return this.getBalance(s);
    }

    @Override
    public boolean has(String s, double v) {
        return this.getBalance(s) >= v;
    }

    @Override
    public boolean has(String s, String s2, double v) {
        return this.has(s, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        return this.withdrawPlayer(this.getOfflinePlayer(s), v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s2, double v) {
        return this.withdrawPlayer(s, v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        return this.depositPlayer(this.getOfflinePlayer(s), v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s2, double v) {
        return this.depositPlayer(s, v);
    }

    @Override
    public EconomyResponse createBank(String s, String s2) {
        return new EconomyResponse(0D, 0D, ResponseType.NOT_IMPLEMENTED, "Banks are not implemented.");
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0D, 0D, ResponseType.NOT_IMPLEMENTED, "Banks are not implemented.");
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0D, 0D, ResponseType.NOT_IMPLEMENTED, "Banks are not implemented.");
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0D, 0D, ResponseType.NOT_IMPLEMENTED, "Banks are not implemented.");
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0D, 0D, ResponseType.NOT_IMPLEMENTED, "Banks are not implemented.");
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0D, 0D, ResponseType.NOT_IMPLEMENTED, "Banks are not implemented.");
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s2) {
        return new EconomyResponse(0D, 0D, ResponseType.NOT_IMPLEMENTED, "Banks are not implemented.");
    }

    @Override
    public EconomyResponse isBankMember(String s, String s2) {
        return new EconomyResponse(0D, 0D, ResponseType.NOT_IMPLEMENTED, "Banks are not implemented.");
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return this.createPlayerAccount(this.getOfflinePlayer(s));
    }

    @Override
    public boolean createPlayerAccount(String s, String s2) {
        return this.createPlayerAccount(s);
    }

    @Override
    public boolean hasAccount(OfflinePlayer op) {
        return this.economy.getAPI().getAccount(op).exists();
    }

    @Override
    public boolean hasAccount(OfflinePlayer op, String world) {
        return this.hasAccount(op);
    }

    @Override
    public double getBalance(OfflinePlayer op) {
        return this.economy.getAPI().getAccount(op).getBalance();
    }

    @Override
    public double getBalance(OfflinePlayer op, String world) {
        return this.getBalance(op);
    }

    @Override
    public boolean has(OfflinePlayer op, double v) {
        return this.getBalance(op) >= v;
    }

    @Override
    public boolean has(OfflinePlayer op, String world, double v) {
        return this.has(op, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer op, double v) {
        final Account a = this.economy.getAPI().getAccount(op);
        if (!this.has(op, v)) return new EconomyResponse(0D, a.getBalance(), ResponseType.FAILURE, "Balance too low");
        a.modifyBalance(-v);
        return new EconomyResponse(v, a.getBalance(), ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer op, String world, double v) {
        return this.withdrawPlayer(op, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer op, double v) {
        final Account a = this.economy.getAPI().getAccount(op);
        a.modifyBalance(v);
        return new EconomyResponse(v, a.getBalance(), ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer op, String world, double v) {
        return this.depositPlayer(op, v);
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer op) {
        return new EconomyResponse(0D, 0D, ResponseType.NOT_IMPLEMENTED, "Banks are not implemented.");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer op) {
        return new EconomyResponse(0D, 0D, ResponseType.NOT_IMPLEMENTED, "Banks are not implemented.");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer op) {
        return new EconomyResponse(0D, 0D, ResponseType.NOT_IMPLEMENTED, "Banks are not implemented.");
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer op) {
        final Account a = this.economy.getAPI().getAccount(op);
        if (a.exists()) return false;
        a.create();
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer op, String world) {
        return this.createPlayerAccount(op);
    }

    public class EconomyServerListener implements Listener {
        Economy_RoyalEconomy economy = null;

        public EconomyServerListener(Economy_RoyalEconomy economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (this.economy.economy == null) {
                final Plugin ec = event.getPlugin();
                if (ec.getClass().getName().equals("org.royaldev.royaleconomy.RoyalEconomy")) {
                    String version = ec.getDescription().getVersion();
                    Economy_RoyalEconomy.this.name += version;
                    this.economy.economy = (RoyalEconomy) ec;
                    Economy_RoyalEconomy.log.info(String.format("[%s][Economy] %s hooked.", Economy_RoyalEconomy.this.plugin.getDescription().getName(), this.economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (this.economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("RoyalEconomy")) {
                    this.economy.economy = null;
                    Economy_RoyalEconomy.log.info(String.format("[%s][Economy] %s unhooked.", Economy_RoyalEconomy.this.plugin.getDescription().getName(), this.economy.name));
                }
            }
        }
    }
}
