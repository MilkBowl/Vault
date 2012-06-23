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

import java.util.List;
import java.util.logging.Level;
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

import cosine.boseconomy.BOSEconomy;

@SuppressWarnings("deprecation")
public class Economy_BOSE6 implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "BOSEconomy";
    private Plugin plugin = null;
    private BOSEconomy economy = null;

    public Economy_BOSE6(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
        log.log(Level.SEVERE, "BOSEconomy6 is an extremely outdated plugin and can not be used reliably for economy! You should update the more recent and maintained BOSEconomy7 for compatibility!");
        // Load Plugin in case it was loaded before
        if (economy == null) {
            Plugin bose = plugin.getServer().getPluginManager().getPlugin("BOSEconomy");
            if (bose != null && bose.isEnabled() && bose.getDescription().getVersion().startsWith("0.6")) {
                economy = (BOSEconomy) bose;
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
        return (double) economy.getPlayerMoney(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
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
            balance = (double) economy.getPlayerMoney(playerName);

            return new EconomyResponse(balance, balance, type, errorMessage);
        }

        amount = Math.ceil(amount);
        balance = (double) economy.getPlayerMoney(playerName);
        if (balance - amount < 0) {
            errorMessage = "Insufficient funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;

            return new EconomyResponse(balance, balance, type, errorMessage);
        }
        if (economy.setPlayerMoney(playerName, (int) (balance - amount), false)) {
            type = EconomyResponse.ResponseType.SUCCESS;
            balance = (double) economy.getPlayerMoney(playerName);

            return new EconomyResponse(amount, balance, type, errorMessage);
        } else {
            errorMessage = "Error withdrawing funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = (double) economy.getPlayerMoney(playerName);

            return new EconomyResponse(amount, balance, type, errorMessage);
        }
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
            balance = (double) economy.getPlayerMoney(playerName);

            return new EconomyResponse(balance, balance, type, errorMessage);
        }
        amount = Math.ceil(amount);
        balance = (double) economy.getPlayerMoney(playerName);
        if (economy.setPlayerMoney(playerName, (int) (balance + amount), false)) {
            type = EconomyResponse.ResponseType.SUCCESS;
            balance = (double) economy.getPlayerMoney(playerName);

            return new EconomyResponse(amount, balance, type, errorMessage);
        } else {
            errorMessage = "Error withdrawing funds";
            type = EconomyResponse.ResponseType.FAILURE;
            amount = 0;
            balance = (double) economy.getPlayerMoney(playerName);

            return new EconomyResponse(balance, balance, type, errorMessage);
        }
    }

    @Override
    public String currencyNamePlural() {
        return economy.getMoneyNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return economy.getMoneyName();
    }

    public class EconomyServerListener implements Listener {
        Economy_BOSE6 economy = null;

        public EconomyServerListener(Economy_BOSE6 economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin bose = plugin.getServer().getPluginManager().getPlugin("BOSEconomy");

                if (bose != null && bose.isEnabled() && bose.getDescription().getVersion().startsWith("0.6")) {
                    economy.economy = (BOSEconomy) bose;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("BOSEconomy") && event.getPlugin().getDescription().getVersion().startsWith("0.6")) {
                    economy.economy = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }

    @Override
    public String format(double amount) {
        if (amount == 1) {
            return String.format("%.0f %s", amount, currencyNameSingular());
        } else {
            return String.format("%.2f %s", amount, currencyNamePlural());
        }
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        boolean success = economy.addBankOwner(name, player, false);
        if (success) 
            return new EconomyResponse(0, economy.getBankMoney(name), ResponseType.SUCCESS, "");

        return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to create that bank account.");
    }
    @Override
    public EconomyResponse deleteBank(String name) {
        boolean success = economy.removeBank(name);
        if (success) {
            return new EconomyResponse(0, 0, ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to remove that bank account.");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        if (!economy.bankExists(name))
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");

        double bankMoney = economy.getBankMoney(name);
        if (bankMoney < amount)
            return new EconomyResponse(0, bankMoney, ResponseType.FAILURE, "The bank does not have enough money!");
        else
            return new EconomyResponse(0, bankMoney, ResponseType.SUCCESS, "");

    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        EconomyResponse er = bankHas(name, amount);
        if (!er.transactionSuccess())
            return er;
        else {
            economy.addBankMoney(name, (int) -amount, true);
            return new EconomyResponse((int) amount, economy.getBankMoney(name), ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        if (!economy.bankExists(name))
            return new EconomyResponse(amount, 0, ResponseType.FAILURE, "That bank does not exist!");
        else {
            economy.addBankMoney(name, (int) amount, true);
            return new EconomyResponse((int) amount, economy.getBankMoney(name), ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        if (!economy.bankExists(name))
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
        else if (economy.isBankOwner(name, playerName)) {
            return new EconomyResponse(0, economy.getBankMoney(name), ResponseType.SUCCESS, "");
        } else
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That player is not a bank owner!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        if (!economy.bankExists(name))
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
        else if (economy.isBankMember(name, playerName)) {
            return new EconomyResponse(0, economy.getBankMoney(name), ResponseType.SUCCESS, "");
        } else
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That player is not a bank member!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        if (!economy.bankExists(name))
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");

        double bankMoney = economy.getBankMoney(name);
        return new EconomyResponse(0, bankMoney, ResponseType.SUCCESS, null);
    }

    @Override
    public List<String> getBanks() {
        return economy.getBankList();
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return economy.playerRegistered(playerName, false);
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        if (economy.playerRegistered(playerName, false)) {
            return false;
        }
        return economy.registerPlayer(playerName);
    }

	@Override
	public int fractionalDigits() {
		return 0;
	}
}
