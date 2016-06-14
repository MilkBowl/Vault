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

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.appledash.saneeconomy.SaneEconomy;
import org.appledash.saneeconomy.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Economy_SaneEconomy extends AbstractEconomy {

    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "SaneEconomy";
    private Plugin plugin = null;
    private SaneEconomy saneEconomy = null;

    public Economy_SaneEconomy(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
        // Load Plugin in case it was loaded before
        if (saneEconomy == null) {
            Plugin saneEconomyPlugin = plugin.getServer().getPluginManager().getPlugin("SaneEconomy");
            if (saneEconomyPlugin != null && saneEconomyPlugin.isEnabled()) {
                saneEconomy = (SaneEconomy) saneEconomyPlugin;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class EconomyServerListener implements Listener {
        Economy_SaneEconomy economy = null;

        public EconomyServerListener(Economy_SaneEconomy economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.saneEconomy == null) {
                Plugin saneEconomyPlugin = event.getPlugin();

                if (saneEconomyPlugin.getDescription().getName().equals("SaneEconomy")) {
                    economy.saneEconomy = (SaneEconomy) saneEconomyPlugin;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.saneEconomy != null) {
                if (event.getPlugin().getDescription().getName().equals("SaneEconomy")) {
                    economy.saneEconomy = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }

    @Override
    public boolean isEnabled(){
        return saneEconomy != null && saneEconomy.isEnabled();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasBankSupport(){
        return false;
    }

    @Override
    public int fractionalDigits(){
        return SaneEconomy.getInstance().getEconomyManager().getCurrency().getFormat().getMaximumFractionDigits();
    }

    @Override
    public String format(double amount) {
        return SaneEconomy.getInstance().getEconomyManager().getCurrency().formatAmount(amount);
    }

    @Override
    public String currencyNamePlural(){
        return SaneEconomy.getInstance().getEconomyManager().getCurrency().getPluralName();
    }

    @Override
    public String currencyNameSingular(){
        return SaneEconomy.getInstance().getEconomyManager().getCurrency().getSingularName();
    }

    @Override
    public boolean hasAccount(String playerName) {
        return SaneEconomy.getInstance().getEconomyManager().accountExists(Bukkit.getServer().getPlayer(playerName));
    }

    @Override
    public double getBalance(String playerName){
        return SaneEconomy.getInstance().getEconomyManager().getBalance(Bukkit.getServer().getPlayer(playerName));
    }

    @Override
    public boolean has(String playerName, double amount) {
        return SaneEconomy.getInstance().getEconomyManager().hasBalance(Bukkit.getServer().getPlayer(playerName), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw a negative amount.");
        }

        Player player = Bukkit.getServer().getPlayer(playerName);
        EconomyManager ecoMan = SaneEconomy.getInstance().getEconomyManager();

        if (ecoMan.hasBalance(player, amount)) {
            double newBalance = ecoMan.subtractBalance(player, amount);

            return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
        }

        return new EconomyResponse(0, 0, ResponseType.FAILURE, "Insufficient funds.");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount){
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit a negative amount.");
        }

        double newBalance = SaneEconomy.getInstance().getEconomyManager().addBalance(Bukkit.getServer().getPlayer(playerName), amount);

        return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "SaneEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "SaneEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "SaneEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "SaneEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "SaneEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "SaneEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "SaneEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "SaneEconomy does not support bank accounts!");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<String>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return hasAccount(playerName);
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
