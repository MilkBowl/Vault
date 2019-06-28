package net.milkbowl.vault.economy.plugins;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import me.coniin.plugins.minefaconomy.Minefaconomy;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Economy_Minefaconomy extends AbstractEconomy {
	private final Logger log;
	private final String name = "Minefaconomy";
	
	private Plugin plugin = null;
	private Minefaconomy economy = null;

	public Economy_Minefaconomy(Plugin plugin) {
		this.plugin = plugin;
		this.log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		Plugin econ = null;
		// Load Plugin in case it was loaded before
		if (economy == null) {
			econ = plugin.getServer().getPluginManager().getPlugin("Minefaconomy");
			log.info("Loading Minefaconomy");	
		}
		if (econ != null && econ.isEnabled()) {
			economy = (Minefaconomy) econ;
			log.info(String.format("[Economy] %s hooked.", this.name));
			return;
		}
		log.info("Error Loading Minefaconomy");
	}

    public class EconomyServerListener implements Listener {
        Economy_Minefaconomy economy_minefaconomy = null;

        public EconomyServerListener(Economy_Minefaconomy economy_minefaconomy) {
            this.economy_minefaconomy = economy_minefaconomy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy_minefaconomy.economy == null) {
                Plugin mfc = event.getPlugin();

                if (mfc.getDescription().getName().equals("Minefaconomy")) {
                    economy_minefaconomy.economy = (Minefaconomy) economy;
                    log.info(String.format("[Economy] %s hooked.", economy_minefaconomy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy_minefaconomy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("Minefaconomy")) {
                    economy_minefaconomy.economy = null;
                    log.info(String.format("[Economy] %s unhooked.", economy_minefaconomy.name));
                }
            }
        }
    }

	@Override
	public boolean isEnabled() {
		return economy != null && economy.isEnabled();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int fractionalDigits() {
		return Minefaconomy.vaultLayer.fractionalDigits();
	}

	@Override
	public String format(double amount) {
		return Minefaconomy.vaultLayer.format(amount);
	}

	@Override
	public String currencyNamePlural() {
		return Minefaconomy.vaultLayer.currencyNamePlural();
	}

	@Override
	public String currencyNameSingular() {
		return Minefaconomy.vaultLayer.currencyNameSingular();
	}

	@Override
	public boolean hasAccount(String playerName) {
		return Minefaconomy.vaultLayer.hasAccount(playerName);
	}

	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return Minefaconomy.vaultLayer.hasAccount(playerName);
	}

	@Override
	public double getBalance(String playerName) {
		return Minefaconomy.vaultLayer.getBalance(playerName);
	}

	@Override
	public double getBalance(String playerName, String world) {
		return Minefaconomy.vaultLayer.getBalance(playerName);
	}

	@Override
	public boolean has(String playerName, double amount) {
		return Minefaconomy.vaultLayer.has(playerName, amount);
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return Minefaconomy.vaultLayer.has(playerName, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		return Minefaconomy.vaultLayer.withdrawPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName,
			double amount) {
		return Minefaconomy.vaultLayer.withdrawPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		return Minefaconomy.vaultLayer.depositPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName,
			double amount) {
		return Minefaconomy.vaultLayer.depositPlayer(playerName, amount);
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		return Minefaconomy.vaultLayer.createPlayerAccount(playerName);
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return Minefaconomy.vaultLayer.createPlayerAccount(playerName);
	}

	@Override
	public boolean hasBankSupport() {
		return Minefaconomy.vaultLayer.hasBankSupport();
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return Minefaconomy.vaultLayer.createBank(name, player);
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return Minefaconomy.vaultLayer.deleteBank(name);
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return Minefaconomy.vaultLayer.bankBalance(name);
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return Minefaconomy.vaultLayer.bankHas(name, amount);
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return Minefaconomy.vaultLayer.bankWithdraw(name, amount);
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return Minefaconomy.vaultLayer.bankDeposit(name, amount);
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return Minefaconomy.vaultLayer.isBankOwner(name, playerName);
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return Minefaconomy.vaultLayer.isBankMember(name, playerName);
	}

	@Override
	public List<String> getBanks() {
		return Minefaconomy.vaultLayer.getBanks();
	}

}
