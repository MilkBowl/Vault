
package net.milkbowl.vault.econmoy.plugins;

import java.util.ArrayList;
import java.util.List;

import de.beam.Eco.Main.Eco;
import de.beam.Eco.Mysql.Mysql_Eco;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Economy_DotEco extends AbstractEconomy {
	
	
	   private final Logger log;

	    private final String name = "Eco";
	    private Plugin plugin = null;
	    private Eco economy = null;

	    public Economy_DotEco(Plugin plugin) {
	        this.plugin = plugin;
	        this.log = plugin.getLogger();
	        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

	        // Load Plugin in case it was loaded before
	        if (economy == null) {
	            Plugin econ = plugin.getServer().getPluginManager().getPlugin("Eco");
	            if (econ != null && econ.isEnabled()) {
	                economy = Eco.eco;
	                log.info(String.format("[Economy] %s hooked.", name));
	            }
	        }
	    }
	    
	    public class EconomyServerListener implements Listener {
	        Economy_DotEco economy = null;

	        public EconomyServerListener(Economy_DotEco economy) {
	            this.economy = economy;
	        }

	        @EventHandler(priority = EventPriority.MONITOR)
	        public void onPluginEnable(PluginEnableEvent event) {
	            if (economy.economy == null) {
	                Plugin eco = event.getPlugin();

	                if (eco.getDescription().getName().equals("Eco")) {
	                    economy.economy = Eco.eco;
	                    log.info(String.format("[Economy] %s hooked.", economy.name));
	                }
	            }
	        }

	        @EventHandler(priority = EventPriority.MONITOR)
	        public void onPluginDisable(PluginDisableEvent event) {
	            if (economy.economy != null) {
	                if (event.getPlugin().getDescription().getName().equals("Eco")) {
	                    economy.economy = null;
	                    log.info(String.format("[Economy] %s unhooked.", economy.name));
	                }
	            }
	        }
	    }
	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
	}

	@Override
	public EconomyResponse bankDeposit(String p, double num) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
	}

	@Override
	public EconomyResponse bankHas(String arg0, double arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
		
	}

	@Override
	public EconomyResponse bankWithdraw(String p, double num) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
	}

	@Override
	public EconomyResponse createBank(String arg0, String arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
	}
	@Override
	public String currencyNamePlural() {
		
		return "$";
	}

	@Override
	public String currencyNameSingular() {
		// TODO Auto-generated method stub
		return "$";
	}

	@Override
	public EconomyResponse deleteBank(String arg0) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
	}
	@Override
	public EconomyResponse depositPlayer(String name, String world, double num) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
	}

	@Override
	public int fractionalDigits() {
		return -1;
	}

	@Override
	public double getBalance(String p) {
		return Mysql_Eco.getKonto(Bukkit.getPlayer(p));
	}

	@Override
	public List<String> getBanks() {
		 return new ArrayList<String>();
	}

	@Override
	public String getName() {
		return "Eco";
	}
	@Override
	public EconomyResponse isBankMember(String arg0, String arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
	}

	@Override
	public EconomyResponse isBankOwner(String arg0, String arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
	}
	@Override
	public EconomyResponse withdrawPlayer(String arg0, String arg1, double arg2) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "");
	}

	@Override
	public boolean createPlayerAccount(String arg0) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(String arg0, String arg1) {
		return false;
	}

	@Override
	public EconomyResponse depositPlayer(String p, double num) {
		Player player = Bukkit.getPlayer(p);
		int balance = Mysql_Eco.getKonto(player);
		
		if(num < 0) {
			return new EconomyResponse(0, balance, ResponseType.FAILURE, "Cannot deposit negative funds");
			
		}else {
			Mysql_Eco.AddKonto(player, (int) num);
			return new EconomyResponse(num, balance, ResponseType.SUCCESS, null);
		}
		
		
		
		 
		 
	}

	@Override
	public String format(double arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getBalance(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean has(String p, double num) {
		int playerkonto = Mysql_Eco.getKonto(Bukkit.getPlayer(p));
		if(playerkonto > num) {
			return true;
		}else{
			return false;	
		}
	}

	@Override
	public boolean has(String arg0, String arg1, double arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAccount(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAccount(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasBankSupport() {		
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public EconomyResponse withdrawPlayer(String p, double num) {
		Player player = Bukkit.getPlayer(p);
		int balance = Mysql_Eco.getKonto(player);
		
		
		if(balance >= num) {
			Mysql_Eco.removeKonto(player, (int) num);
			return new EconomyResponse(num, balance, ResponseType.SUCCESS, null);
		}else {
			return new EconomyResponse(0, balance, ResponseType.FAILURE, "Cannot Witdraw");
			
		}
		
		
		
	}

}
