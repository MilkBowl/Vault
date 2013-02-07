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
import java.util.logging.Level;
import java.util.logging.Logger;

import me.ic3d.eco.ECO;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Economy_3co implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "3co";
    private Plugin plugin = null;
    private ECO economy = null;

    public Economy_3co(Plugin plugin) {
        this.plugin = plugin;
        log.log(Level.SEVERE, "3co is outdated and WILL BREAK in CB-R5+ - It is highly recommended to update to a new economy plugin and use Vaults conversion!");
        log.log(Level.WARNING, "3co is an integer only economy, you may notice inconsistencies with accounts if you do not setup your other econ using plugins accordingly!");
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (economy == null) {
            Plugin econ = plugin.getServer().getPluginManager().getPlugin("3co");
            if (econ != null && econ.isEnabled()) {
                economy = (ECO) econ;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    @Override
    public String getName() {
        return name;
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
    public double getBalance(String playerName) {
        final double balance;

        balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));

        final double fBalance = balance;
        return fBalance;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;

        if (amount < 0) {
            errorMessage = "Cannot withdraw negative funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));

            return new EconomyResponse(amount, balance, type, errorMessage);
        }

        amount = Math.ceil(amount);
        balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));
        if (balance - amount < 0) {
            errorMessage = "Insufficient funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));

            return new EconomyResponse(amount, balance, type, errorMessage);
        }
        economy.setMoney(plugin.getServer().getPlayer(playerName), (int) (balance - amount));
        type = EconomyResponse.ResponseType.SUCCESS;
        balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));

        return new EconomyResponse(amount, balance, type, errorMessage);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;

        if (amount < 0) {
            errorMessage = "Cannot deposit negative funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));

            return new EconomyResponse(amount, balance, type, errorMessage);
        }
        amount = Math.ceil(amount);
        balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));
        economy.setMoney(plugin.getServer().getPlayer(playerName), (int) (balance + amount));
        type = EconomyResponse.ResponseType.SUCCESS;
        balance = (double) economy.getMoney(plugin.getServer().getPlayer(playerName));

        return new EconomyResponse(amount, balance, type, errorMessage);
    }

    @Override
    public String currencyNamePlural() {
        return economy.getPluralCurrency();
    }

    @Override
    public String currencyNameSingular() {
        return economy.getSingularCurrency();
    }

    public class EconomyServerListener implements Listener {
        Economy_3co economy = null;

        public EconomyServerListener(Economy_3co economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin eco = plugin.getServer().getPluginManager().getPlugin("3co");

                if (eco != null) {
                    economy.economy = (ECO) eco;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("3co")) {
                    economy.economy = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }

    @Override
    public String format(double amount) {
        amount = Math.ceil(amount);
        if (amount == 1) {
            return String.format("%d %s", (int)amount, currencyNameSingular());
        } else {
            return String.format("%d %s", (int)amount, currencyNamePlural());
        }
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "3co does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "3co does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "3co does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "3co does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "3co does not support bank accounts!");
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "3co does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "3co does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "3co does not support bank accounts!");
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
        return economy.hasAccount(plugin.getServer().getPlayer(playerName));
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        Player p = Bukkit.getPlayer(playerName);
        if (p == null) {
            return false;
        }
        economy.createAccount(p, 0);
        return true;
    }

	@Override
	public int fractionalDigits() {
		return 0;
	}
}
