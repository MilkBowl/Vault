package net.milkbowl.vault.economy.plugins;

import com.gravypod.Dosh.Dosh;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.plugin.Plugin;
import org.curlybrace.plugins.pconomy.pConomy;
import org.curlybrace.plugins.pconomy.pConomyAPI;

/**
 * @author YoshiGenius
 */
public class Economy_pConomy implements Economy {
    
    Plugin plugin;
    pConomy pConomy;
    boolean isEnabled = false;
    pConomyAPI api;
    
    public Economy_pConomy(Plugin _plugin) {
	plugin = _plugin;
	Plugin pcon = plugin.getServer().getPluginManager().getPlugin("pConomy");
	if (pcon != null && pcon.isEnabled() && pcon instanceof pConomy) {
            pConomy = (pConomy) pcon;
            api = pConomy.getAPI();
	    isEnabled = true;
	}
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getName() {
        return "pConomy";
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
        return null;
    }

    @Override
    public String currencyNamePlural() {
        return "$";
    }

    @Override
    public String currencyNameSingular() {
        return "$";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return api.isRegistered(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        return api.getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return api.hasBalance(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (api.removeFromBalance(playerName, amount)) {
            return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "Worked!");
        }
        return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Didnt work!");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        api.addToBalance(playerName, amount);
        return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "Worked!");
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        api.addPlayer(playerName);
        return true;
    }

}
