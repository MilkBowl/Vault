package net.milkbowl.vault.economy.plugins;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.api.economy.Economy;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Economy_CommandsEX extends AbstractEconomy {
	private final Logger log;
	private final String name = "CommandsEX Economy";
	private CommandsEX economy;
	
	public Economy_CommandsEX(final Plugin plugin) {
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		
		if (this.economy == null) {
			final Plugin commandsex = plugin.getServer().getPluginManager().getPlugin("CommandsEX");
			
			if (commandsex != null && commandsex.isEnabled()) {
				this.economy = (CommandsEX) commandsex;
				this.log.info(String.format("[Economy] %s hooked.", this.name));
			}
		}
	}
	
	public class EconomyServerListener implements Listener {
		final Economy_CommandsEX economy;
		
		public EconomyServerListener(final Economy_CommandsEX economy) {
			this.economy = economy;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.economy.economy == null) {
				final Plugin cex = event.getPlugin();
				
				if (cex.getDescription().getName().equals("CommandsEX")) {
					this.economy.economy = (CommandsEX) cex;
					Economy_CommandsEX.this.log.info(String.format("[Economy] %s hooked.", this.economy.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.economy.economy != null) {
				if (event.getPlugin().getDescription().getName().equals("CommandsEX")) {
					this.economy.economy = null;
					Economy_CommandsEX.this.log.info(String.format("[Economy] %s unhooked.", this.economy.name));
				}
			}
		}
	}
	
	@Override
	public boolean isEnabled() {
		if (this.economy == null) {
			return false;
		} else {
			return Economy.isEnabled();
		}
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
		return 2;
	}
	
	@Override
	public String format(final double amount) {
		return Economy.getCurrencySymbol() + amount;
	}
	
	@Override
	public String currencyNamePlural() {
		return Economy.getCurrencyPlural();
	}
	
	@Override
	public String currencyNameSingular() {
		return Economy.getCurrencySingular();
	}
	
	@Override
	public boolean hasAccount(final String playerName) {
		return Economy.hasAccount(playerName);
	}
	
	@Override
	public double getBalance(final String playerName) {
		return Economy.getBalance(playerName);
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		return Economy.has(playerName, amount);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
		final EconomyResponse.ResponseType rt;
		final String message;
		
		if (Economy.has(playerName, amount)) {
			Economy.withdraw(playerName, amount);
			rt = EconomyResponse.ResponseType.SUCCESS;
			message = null;
		} else {
			rt = EconomyResponse.ResponseType.FAILURE;
			message = "Not enough money";
		}
		
		return new EconomyResponse(amount, Economy.getBalance(playerName), rt, message);
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final double amount) {
		Economy.deposit(playerName, amount);
		return new EconomyResponse(amount, Economy.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "Successfully deposited");
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
	}
	
	@Override
	public List<String> getBanks() {
		return new ArrayList<>();
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		if (Economy.hasAccount(playerName)) {
			return false;
		} else {
			Economy.createAccount(playerName);
			return true;
		}
	}
	
	@Override
	public boolean hasAccount(final String playerName, final String worldName) {
		return this.hasAccount(playerName);
	}
	
	@Override
	public double getBalance(final String playerName, final String world) {
		return this.getBalance(playerName);
	}
	
	@Override
	public boolean has(final String playerName, final String worldName, final double amount) {
		return this.has(playerName, amount);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final String worldName, final double amount) {
		return this.withdrawPlayer(playerName, amount);
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final String worldName, final double amount) {
		return this.depositPlayer(playerName, amount);
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName, final String worldName) {
		return this.createPlayerAccount(playerName);
	}
}
