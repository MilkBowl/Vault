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
package net.milkbowl.vault;

import com.nijikokun.register.payment.Method;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

@SuppressWarnings("deprecation")
public class VaultEco implements Method {
	
	private Vault vault;
	private Economy economy;
	
	public Vault getPlugin() {
		return vault;
	}
	
	
	@Override
	public boolean createAccount(final String name, final Double amount) {
		if (!economy.createBank(name, "").transactionSuccess()) {
			return false;
		}
		return economy.bankDeposit(name, amount).transactionSuccess();
	}
	
	public String getName() {
		return vault.getDescription().getName();
	}
	
	public String getVersion() {
		return vault.getDescription().getVersion();
	}
	
	public int fractionalDigits() {
		return economy.fractionalDigits();
	}
	
	public String format(final double amount) {
		return economy.format(amount);
	}
	
	public boolean hasBanks() {
		return economy.hasBankSupport();
	}
	
	public boolean hasBank(final String bank) {
		return economy.getBanks().contains(bank);
	}
	
	public boolean hasAccount(final String name) {
		return economy.hasAccount(name);
	}
	
	public boolean hasBankAccount(final String bank, final String name) {
		return economy.isBankOwner(bank, name).transactionSuccess() || economy.isBankMember(bank, name).transactionSuccess();
	}
	
	public boolean createAccount(final String name) {
		return economy.createPlayerAccount(name);
	}
	
	public Method.MethodAccount getAccount(final String name) {
		if (!this.hasAccount(name)) {
			return null;
		}
		
		return new VaultAccount(name, economy);
	}
	
	public Method.MethodBankAccount getBankAccount(final String bank, final String name) {
		if (!this.hasBankAccount(bank, name)) {
			return null;
		}
		
		return new VaultBankAccount(bank, this.economy);
	}
	
	public boolean isCompatible(final Plugin plugin) {
		return plugin instanceof Vault;
	}
	
	public void setPlugin(final Plugin plugin) {
		vault = (Vault) plugin;
		final RegisteredServiceProvider<Economy> economyProvider = vault.getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
	}
	
	public static class VaultAccount implements Method.MethodAccount {
		private final String name;
		private final Economy economy;
		
		public VaultAccount(final String name, final Economy economy) {
			this.name = name;
			this.economy = economy;
		}
		
		public double balance() {
			return economy.getBalance(name);
		}
		
		public boolean set(final double amount) {
			if (!economy.withdrawPlayer(name, balance()).transactionSuccess()) {
				return false;
			}
			
			if (amount == 0) {
				return true;
			}
			return economy.depositPlayer(name, amount).transactionSuccess();
		}
		
		public boolean add(final double amount) {
			return economy.depositPlayer(name, amount).transactionSuccess();
		}
		
		public boolean subtract(final double amount) {
			return economy.withdrawPlayer(name, amount).transactionSuccess();
		}
		
		public boolean multiply(final double amount) {
			final double balance = balance();
			return set(balance * amount);
		}
		
		public boolean divide(final double amount) {
			final double balance = balance();
			return set(balance / amount);
		}
		
		public boolean hasEnough(final double amount) {
			return (balance() >= amount);
		}
		
		public boolean hasOver(final double amount) {
			return (balance() > amount);
		}
		
		public boolean hasUnder(final double amount) {
			return (balance() < amount);
		}
		
		public boolean isNegative() {
			return (balance() < 0);
		}
		
		public boolean remove() {
			return set(0.0);
		}
	}
	
	public static class VaultBankAccount implements Method.MethodBankAccount {
		
		private final String bank;
		private final Economy economy;
		
		public VaultBankAccount(final String bank, final Economy economy) {
			this.bank = bank;
			this.economy = economy;
		}
		
		public String getBankName() {
			return bank;
		}
		
		public int getBankId() {
			return -1;
		}
		
		public double balance() {
			return economy.bankBalance(bank).balance;
		}
		
		public boolean set(final double amount) {
			if (!economy.bankWithdraw(bank, balance()).transactionSuccess()) {
				return false;
			}
			if (amount == 0) {
				return true;
			}
			return economy.bankDeposit(bank, amount).transactionSuccess();
		}
		
		public boolean add(final double amount) {
			return economy.bankDeposit(bank, amount).transactionSuccess();
		}
		
		public boolean subtract(final double amount) {
			return economy.bankWithdraw(bank, amount).transactionSuccess();
		}
		
		public boolean multiply(final double amount) {
			final double balance = balance();
			return set(balance * amount);
		}
		
		public boolean divide(final double amount) {
			final double balance = balance();
			return set(balance / amount);
		}
		
		public boolean hasEnough(final double amount) {
			return (balance() >= amount);
		}
		
		public boolean hasOver(final double amount) {
			return (balance() > amount);
		}
		
		public boolean hasUnder(final double amount) {
			return (balance() < amount);
		}
		
		public boolean isNegative() {
			return (balance() < 0);
		}
		
		public boolean remove() {
			return set(0.0);
		}
		
	}
}