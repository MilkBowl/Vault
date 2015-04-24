package src.net.milkbowl.vault.economy.plugins;

import java.util.List;
import java.util.logging.Logger;

import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.blockbank.VaultEconomy;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;


public class Economy_BlockBank extends AbstractEconomy implements Economy {

	private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "BlockBank";
    private Plugin plugin = null;
    private BlockBank econ = null;
    private VaultEconomy api = null;

    public Economy_BlockBank(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (econ == null) {
            Plugin econ = plugin.getServer().getPluginManager().getPlugin(this.name);
            if (econ != null && econ.isEnabled()) {
                this.econ = (BlockBank) econ;
                this.api = this.econ.getVaultAPI();
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class EconomyServerListener implements Listener {
        Economy_BlockBank economy = null;

        public EconomyServerListener(Economy_BlockBank economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
        	log.info("Enable check:" + event.getPlugin().getDescription().getName());
            if (economy.econ == null) {
                Plugin eco = event.getPlugin();

                if (eco.getDescription().getName().equals("BlockBank")) {
                	log.info("Getting BlockBank API");
                    economy.econ = (BlockBank) eco;
                    economy.api = economy.econ.getVaultAPI();
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.econ != null) {
                if (event.getPlugin().getDescription().getName().equals("BlockBank")) {
                    economy.econ = null;
                    economy.api = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), name));
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return this.api != null;
    }
	    
	@Override
	public EconomyResponse bankBalance(String arg0) {
		return this.api.bankBalance(arg0);
	}

	@Override
	public EconomyResponse bankDeposit(String arg0, double arg1) {		
		return this.api.bankDeposit(arg0, arg1);
	}

	@Override
	public EconomyResponse bankHas(String arg0, double arg1) {
		return this.api.bankHas(arg0, arg1);
	}

	@Override
	public EconomyResponse bankWithdraw(String arg0, double arg1) {
		return this.api.bankWithdraw(arg0, arg1);
	}

	@Override
	public EconomyResponse createBank(String arg0, String arg1) {
		return this.api.createBank(arg0, arg1);
	}

	@Override
	public boolean createPlayerAccount(String arg0) {
		return this.api.createPlayerAccount(arg0);
	}

	@Override
	public boolean createPlayerAccount(String arg0, String arg1) {
		return this.api.createPlayerAccount(arg0, arg1);
	}

	@Override
	public String currencyNamePlural() {
		return this.api.currencyNamePlural();
	}

	@Override
	public String currencyNameSingular() {
		return this.api.currencyNameSingular();
	}

	@Override
	public EconomyResponse deleteBank(String arg0) {
		return this.api.deleteBank(arg0);
	}

	@Override
	public EconomyResponse depositPlayer(String arg0, double arg1) {
		return this.api.depositPlayer(arg0, arg1);
	}

	@Override
	public EconomyResponse depositPlayer(String arg0, String arg1, double arg2) {
		return this.api.depositPlayer(arg0, arg1, arg2);
	}

	@Override
	public String format(double arg0) {
		return this.api.format(arg0);
	}

	@Override
	public int fractionalDigits() {
		return this.api.fractionalDigits();
	}

	@Override
	public double getBalance(String arg0) {
		return this.api.getBalance(arg0);
	}

	@Override
	public double getBalance(String arg0, String arg1) {
		return this.api.getBalance(arg0, arg1);
	}

	@Override
	public List<String> getBanks() {
		return this.api.getBanks();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean has(String arg0, double arg1) {
		return this.api.has(arg0, arg1);
	}

	@Override
	public boolean has(String arg0, String arg1, double arg2) {
		return this.api.has(arg0, arg1, arg2);
	}

	@Override
	public boolean hasAccount(String arg0) {
		return this.api.hasAccount(arg0);
	}

	@Override
	public boolean hasAccount(String arg0, String arg1) {
		return this.api.hasAccount(arg0, arg1);
	}

	@Override
	public boolean hasBankSupport() {
		return this.api.hasBankSupport();
	}

	@Override
	public EconomyResponse isBankMember(String arg0, String arg1) {
		return this.api.isBankMember(arg1, arg1);
	}

	@Override
	public EconomyResponse isBankOwner(String arg0, String arg1) {
		return this.api.isBankOwner(arg0, arg1);
	}

	@Override
	public EconomyResponse withdrawPlayer(String arg0, double arg1) {
		return this.api.withdrawPlayer(arg0, arg1);
	}

	@Override
	public EconomyResponse withdrawPlayer(String arg0, String arg1, double arg2) {
		return this.api.withdrawPlayer(arg0, arg1, arg2);
	}

}
