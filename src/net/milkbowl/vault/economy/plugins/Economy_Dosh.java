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

import com.gravypod.Dosh.Dosh;
import com.gravypod.Dosh.MoneyUtils;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.plugin.Plugin;

import java.util.List;


public class Economy_Dosh extends AbstractEconomy {
	
	
	final Plugin plugin;
	Dosh doshPlugin;
	DoshAPIHandler apiHandle;
	
	public Economy_Dosh(final Plugin _plugin) {
		this.plugin = _plugin;
		
		if (this.plugin.getServer().getPluginManager().isPluginEnabled("Dosh")) {
			this.doshPlugin = (Dosh) this.plugin.getServer().getPluginManager().getPlugin("Dosh");
			this.apiHandle = new DoshAPIHandler();
		}
	}
	
	@Override
	public boolean isEnabled() {
		return this.apiHandle != null;
	}
	
	@Override
	public String getName() {
		return "Dosh";
	}
	
	@Override
	public boolean hasBankSupport() {
		return false;
	}
	
	@Override
	public int fractionalDigits() {
		return 0;
	}
	
	@Override
	public String format(final double amount) {
		return null;
	}
	
	@Override
	public String currencyNamePlural() {
		return Dosh.getSettings().moneyName + "s";
	}
	
	@Override
	public String currencyNameSingular() {
		return Dosh.getSettings().moneyName;
	}
	
	@Override
	public boolean hasAccount(final String playerName) {
		return true;
	}
	
	@Override
	public double getBalance(final String playerName) {
		return MoneyUtils.getUserBal(playerName);
	}
	
	@Override
	public boolean has(final String playerName, final double amount) {
		return (this.getBalance(playerName) - amount) > 0;
	}
	
	@Override
	public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
		
		if (MoneyUtils.subtractMoney(playerName, amount)) {
			return new EconomyResponse(amount, this.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "Worked!");
		}
		
		return new EconomyResponse(amount, this.getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Didnt work!");
		
	}
	
	@Override
	public EconomyResponse depositPlayer(final String playerName, final double amount) {
		MoneyUtils.addUserBal(playerName, amount);
		return new EconomyResponse(amount, this.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "It worked!");
	}
	
	@Override
	public EconomyResponse createBank(final String name, final String player) {
		return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
	}
	
	@Override
	public EconomyResponse deleteBank(final String name) {
		return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
	}
	
	@Override
	public EconomyResponse bankBalance(final String name) {
		return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
	}
	
	@Override
	public EconomyResponse bankHas(final String name, final double amount) {
		return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
	}
	
	@Override
	public EconomyResponse bankWithdraw(final String name, final double amount) {
		return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
	}
	
	@Override
	public EconomyResponse bankDeposit(final String name, final double amount) {
		return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
	}
	
	@Override
	public EconomyResponse isBankOwner(final String name, final String playerName) {
		return null;
	}
	
	@Override
	public EconomyResponse isBankMember(final String name, final String playerName) {
		return null;
	}
	
	@Override
	public List<String> getBanks() {
		return null;
	}
	
	@Override
	public boolean createPlayerAccount(final String playerName) {
		return false;
	}
	
	public static class DoshAPIHandler extends MoneyUtils {
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
