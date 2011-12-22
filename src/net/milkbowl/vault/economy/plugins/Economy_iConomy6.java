package net.milkbowl.vault.economy.plugins;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.iCo6.iConomy;
import com.iCo6.system.Account;
import com.iCo6.system.Accounts;
import com.iCo6.system.Holdings;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_iConomy6 implements Economy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private String name = "iConomy 6";
    private JavaPlugin plugin = null;
    private PluginManager pluginManager = null;
    protected iConomy economy = null;
    private Accounts accounts;
    private EconomyServerListener economyServerListener = null;

    public Economy_iConomy6(JavaPlugin plugin) {
        this.plugin = plugin;
        this.pluginManager = this.plugin.getServer().getPluginManager();

        economyServerListener = new EconomyServerListener(this);

        this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, economyServerListener, Priority.Monitor, plugin);
        this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, economyServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (economy == null) {
            Plugin ec = plugin.getServer().getPluginManager().getPlugin("iConomy");
            if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("com.iCo6.iConomy")) {
                economy = (iConomy) ec;
                accounts = new Accounts();
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    private class EconomyServerListener extends ServerListener {
        Economy_iConomy6 economy = null;

        public EconomyServerListener(Economy_iConomy6 economy) {
            this.economy = economy;
        }

        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin ec = plugin.getServer().getPluginManager().getPlugin("iConomy");

                if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("com.iCo6.iConomy")) {
                    economy.economy = (iConomy) ec;
                    accounts = new Accounts();
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("iConomy")) {
                    economy.economy = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
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
    public String getName() {
        return name;
    }

    @Override
    public String format(double amount) {
        return iConomy.format(amount);
    }

    @Override
    public double getBalance(String playerName) {
        if (accounts.exists(playerName))
            return accounts.get(playerName).getHoldings().getBalance();
        else
            return 0;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;

        Account account = accounts.get(playerName);
        Holdings holdings = account.getHoldings();
        if (holdings.hasEnough(amount)) {
            holdings.subtract(amount);
            balance = holdings.getBalance();
            type = EconomyResponse.ResponseType.SUCCESS;
            return new EconomyResponse(amount, balance, type, errorMessage);
        } else {
            amount = 0;
            balance = holdings.getBalance();
            type = EconomyResponse.ResponseType.FAILURE;
            errorMessage = "Insufficient funds";
            return new EconomyResponse(amount, balance, type, errorMessage);
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        double balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;

        Account account = accounts.get(playerName);
        Holdings holdings = account.getHoldings();
        holdings.add(amount);
        balance = holdings.getBalance();
        type = EconomyResponse.ResponseType.SUCCESS;

        return new EconomyResponse(amount, balance, type, errorMessage);
    }

	@Override
	public boolean has(String playerName, double amount) {
		return getBalance(playerName) >= amount;
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		if (accounts.exists(name))
			return new EconomyResponse(0, accounts.get(name).getHoldings().getBalance(), ResponseType.FAILURE, "That account already exists.");

		boolean created = accounts.create(name);
		if (created)
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, "");
		else 
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "There was an error creating the account");
			
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		if (has(name, amount))
			return new EconomyResponse(0, amount, ResponseType.SUCCESS, "");
		else
			return new EconomyResponse(0, accounts.get(name).getHoldings().getBalance(), ResponseType.FAILURE, "The account does not have enough!");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return withdrawPlayer(name, amount);
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return depositPlayer(name, amount);
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy 6 does not support Bank owners.");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "iConomy 6 does not support Bank members.");
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		if (!accounts.exists(name))
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "There is no bank account with that name");
		else
			return new EconomyResponse(0, accounts.get(name).getHoldings().getBalance(), ResponseType.SUCCESS, null);
	}
	
    @Override
    public List<String> getBanks() {
        throw new UnsupportedOperationException("iConomy does not support listing of bank accounts");
    }
}
