package net.milkbowl.vault.economy.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.api.EconomyAPI;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_CommandsEX implements Economy {
	private static final Logger log = Logger.getLogger("Minecraft");
	
	private final String name = "CommandsEX Economy";
    private Plugin plugin = null;
    private CommandsEX economy = null;
    
	public Economy_CommandsEX(Plugin plugin){
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		
		if (economy == null) {
            Plugin commandsex = plugin.getServer().getPluginManager().getPlugin("CommandsEx");
            
            if (commandsex != null && commandsex.isEnabled()) {
                economy = (CommandsEX) commandsex;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
	}
	
	public class EconomyServerListener implements Listener {
        Economy_CommandsEX economy = null;

        public EconomyServerListener(Economy_CommandsEX economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin cex = plugin.getServer().getPluginManager().getPlugin("CommandsEX");

                if (cex != null && cex.isEnabled()) {
                    economy.economy = (CommandsEX) cex;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("CommandsEX")) {
                    economy.economy = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }
	
	@Override
	public boolean isEnabled() {
		if (economy == null){
			return false;
		} else {
			return EconomyAPI.isEnabled();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public int fractionalDigits() {
		return 2;
	}

	@Override
	public String format(double amount) {
		return EconomyAPI.getCurrencySymbol() + amount;
	}

	@Override
	public String currencyNamePlural() {
		return EconomyAPI.getCurrencyPlural();
	}

	@Override
	public String currencyNameSingular() {
		return EconomyAPI.getCurrencySingular();
	}

	@Override
	public boolean hasAccount(String playerName) {
		return EconomyAPI.hasAccount(playerName);
	}

	@Override
	public double getBalance(String playerName) {
		return EconomyAPI.getBalance(playerName);
	}

	@Override
	public boolean has(String playerName, double amount) {
		return EconomyAPI.has(playerName, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		ResponseType rt;
		String message;
		
		if (EconomyAPI.has(playerName, amount)){
			EconomyAPI.withdraw(playerName, amount);
			rt = ResponseType.SUCCESS;
			message = null;
		} else {
			rt = ResponseType.FAILURE;
			message = "Not enough money";
		}
		
		return new EconomyResponse(amount, EconomyAPI.getBalance(playerName), rt, message);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		EconomyAPI.deposit(playerName, amount);
		return new EconomyResponse(amount, EconomyAPI.getBalance(playerName), ResponseType.SUCCESS, "Successfully deposited");
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}

	@Override
	public List<String> getBanks() {
		return new ArrayList<String>();
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		if (EconomyAPI.hasAccount(playerName)){
			return false;
		} else {
			EconomyAPI.createAccount(playerName);
			return true;
		}
	}

}
