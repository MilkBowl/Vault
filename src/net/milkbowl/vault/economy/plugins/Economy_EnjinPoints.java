/* This file is part of Vault.

    Vault is free software: you can redistribute it and/or modify
    it under the terms of the GNU LenjinPointser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Vault is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU LenjinPointser General Public License for more details.

    You should have received a copy of the GNU LenjinPointser General Public License
    along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.milkbowl.vault.economy.plugins;

import java.util.ArrayList;
import java.util.List;
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

import com.enjin.officialplugin.points.*;
import com.enjin.officialplugin.*;

public class Economy_EnjinPoints implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "Enjin Points";
    private Plugin plugin = null;
    private PointsAPI enjinPoints = null;
    private EnjinMinecraftPlugin enjinPlugin = null;

    public Economy_EnjinPoints(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (enjinPlugin == null) {
            Plugin enjinPlugin = plugin.getServer().getPluginManager().getPlugin("Enjin Minecraft Plugin");
            if (enjinPlugin != null && enjinPlugin.isEnabled()) {
                enjinPlugin = (EnjinMinecraftPlugin) enjinPlugin;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        if (enjinPlugin == null) {
            return false;
        } else {
            return enjinPlugin.isEnabled();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getBalance(String playerName) {
    	double balance;
        try {
			balance = PointsAPI.getPointsForPlayer(playerName);
		} catch (PlayerDoesNotExistException e) {
			balance = 0;
		} catch (ErrorConnectingToEnjinException e) {
			balance = 0;
		}
        return balance;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amountin) {
        if (amountin < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
        }
        int amount = (int)amountin;
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;

        try {
            PointsAPI.modifyPointsToPlayer(playerName, amount, PointsAPI.Type.RemovePoints);
            balance = PointsAPI.getPointsForPlayer(playerName);
            type = EconomyResponse.ResponseType.SUCCESS;
        } catch (PlayerDoesNotExistException e) {
                amount = 0;
                balance = 0;
                type = EconomyResponse.ResponseType.FAILURE;
                errorMessage = "User does not exist";
        } catch (ErrorConnectingToEnjinException e) {
            	amount = 0;
            	balance = 0;
            	type = EconomyResponse.ResponseType.FAILURE;
            	errorMessage = "Could not connect to Enjin";
        }

        return new EconomyResponse(amount, balance, type, errorMessage);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amountin) {
        if (amountin  < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot deposit negative funds");
        }
        int amount = (int)amountin;
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;

        try {
            PointsAPI.modifyPointsToPlayer(playerName, amount, PointsAPI.Type.AddPoints);
            balance = PointsAPI.getPointsForPlayer(playerName);
            type = EconomyResponse.ResponseType.SUCCESS;
        } catch (PlayerDoesNotExistException e) {
                amount = 0;
                balance = 0;
                type = EconomyResponse.ResponseType.FAILURE;
                errorMessage = "User does not exist";
        } catch (ErrorConnectingToEnjinException e) {
            	amount = 0;
            	balance = 0;
            	type = EconomyResponse.ResponseType.FAILURE;
            	errorMessage = "Could not connect to Enjin";
        }

        return new EconomyResponse(amount, balance, type, errorMessage);
    }

    public class EconomyServerListener implements Listener {
        Economy_EnjinPoints economy = null;

        public EconomyServerListener(Economy_EnjinPoints economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.enjinPoints == null) {
                Plugin enjinPlugin = event.getPlugin();

                if (enjinPlugin.getDescription().getName().equals("Enjin Minecraft Plugin")) {
                    economy.enjinPlugin = (EnjinMinecraftPlugin) enjinPlugin;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.enjinPlugin != null) {
                if (event.getPlugin().getDescription().getName().equals("Enjin Minecraft Plugin")) {
                    economy.enjinPlugin = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }

    @Override
    public String format(double amount) {
        return amount + " Points";
    }

    @Override
    public String currencyNameSingular() {
        return "";
    }

    @Override
    public String currencyNamePlural() {
        return "";
    }

    @Override
    public boolean has(String playerName, double amount) {
        int playerCurrentAmount;
		try {
			playerCurrentAmount = PointsAPI.getPointsForPlayer(playerName);
		} catch (PlayerDoesNotExistException e) {
			playerCurrentAmount = 0;
		} catch (ErrorConnectingToEnjinException e) {
			playerCurrentAmount = 0;
		}
		
        if(playerCurrentAmount >= amount) {
        	return true;
        } else {
        	return false;
        }
        
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Enjin does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Enjin does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Enjin does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Enjin does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Enjin does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Enjin does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Enjin does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Enjin does not support bank accounts!");
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
        return true;
    }

	@Override
	public int fractionalDigits() {
		return 0;
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