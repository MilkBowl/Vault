package net.milkbowl.vault.economy.plugins;

import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import ca.agnate.EconXP.EconXP;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_EconXP implements Economy {
	private static final Logger log = Logger.getLogger("Minecraft");

	private String name = "EconXP";
	private Plugin plugin = null;
	private PluginManager pluginManager = null;
	private EconXP econ = null;
	private EconomyServerListener economyServerListener = null;

	public Economy_EconXP(Plugin plugin) {
		this.plugin = plugin;
		pluginManager = this.plugin.getServer().getPluginManager();

		economyServerListener = new EconomyServerListener(this);

		this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, economyServerListener, Priority.Monitor, plugin);
		this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, economyServerListener, Priority.Monitor, plugin);

		// Load Plugin in case it was loaded before
		if (econ == null) {
			Plugin econ = plugin.getServer().getPluginManager().getPlugin("EconXP");
			if (econ != null && econ.isEnabled()) {
				this.econ = (EconXP) econ;
				log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
			}
		}
	}

	private class EconomyServerListener extends ServerListener {
		Economy_EconXP economy = null;

		public EconomyServerListener(Economy_EconXP economy) {
			this.economy = economy;
		}

		public void onPluginEnable(PluginEnableEvent event) {
			if (economy.econ == null) {
				Plugin eco = plugin.getServer().getPluginManager().getPlugin("EconXP");

				if (eco != null && eco.isEnabled()) {
					economy.econ = (EconXP) eco;
					log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
				}
			}
		}

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
}
