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

import net.milkbowl.vault.economy.Economy;
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

import ca.agnate.EconXP.EconXP;

public class Economy_EconXP implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "EconXP";
    private Plugin plugin = null;
    private EconXP econ = null;

    public Economy_EconXP(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
        log.log(Level.WARNING, "EconXP is an integer only economy, you may notice inconsistencies with accounts if you do not setup your other econ using plugins accordingly!");
        // Load Plugin in case it was loaded before
        if (econ == null) {
            Plugin econ = plugin.getServer().getPluginManager().getPlugin("EconXP");
            if (econ != null && econ.isEnabled()) {
                this.econ = (EconXP) econ;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class EconomyServerListener implements Listener {
        Economy_EconXP economy = null;

        public EconomyServerListener(Economy_EconXP economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.econ == null) {
                Plugin eco = plugin.getServer().getPluginManager().getPlugin("EconXP");

                if (eco != null && eco.isEnabled()) {
                    economy.econ = (EconXP) eco;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.econ != null) {
                if (event.getPlugin().getDescription().getName().equals("EconXP")) {
                    economy.econ = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return this.econ != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String format(double amount) {
        amount = Math.ceil(amount);

        return String.format("%d %s", (int)amount, "experience");
    }

    @Override
    public String currencyNamePlural() {
        return "experience";
    }

    @Override
    public String currencyNameSingular() {
        return "experience";
    }

    @Override
    public double getBalance(String playerName) {
        OfflinePlayer player = econ.getPlayer(playerName);

        if ( player == null ) { return 0; }

        return econ.getExp(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        OfflinePlayer player = econ.getPlayer(playerName);

        if ( player == null ) { return false; }

        return econ.hasExp(player, (int) Math.ceil(amount) );
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        OfflinePlayer player = econ.getPlayer(playerName);

        if ( player == null ) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player does not exist");
        }

        double balance = econ.getExp(player);
        amount = Math.ceil(amount);

        if (amount < 0) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, "Cannot withdraw negative funds");
        }

        if ( econ.hasExp(player, (int) amount) == false ) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, "Insufficient funds");
        }

        econ.removeExp(player, (int) amount);

        double finalBalance = econ.getExp(player);

        return new EconomyResponse(amount, finalBalance, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        OfflinePlayer player = econ.getPlayer(playerName);

        if ( player == null ) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player does not exist");
        }

        double balance = econ.getExp(player);
        amount = Math.ceil(amount);

        if (amount < 0) {
            return new EconomyResponse(0, balance, ResponseType.FAILURE, "Cannot withdraw negative funds");
        }

        econ.addExp(player, (int) amount );
        balance = econ.getExp(player);

        return new EconomyResponse(amount, balance, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
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
        return econ.getPlayer(playerName) != null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

	@Override
	public int fractionalDigits() {
		return 0;
	}
}
