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

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.nijikokun.register.payment.Method;

@SuppressWarnings("deprecation")
public class VaultEco implements Method {

	private Vault vault;
	private Economy economy;

	@Override
	public Vault getPlugin() {
		return this.vault;
	}

	@Override
	public boolean createAccount(String name, Double amount) {
		if (!this.economy.createBank(name, "").transactionSuccess()) {
			return false;
		}
		return this.economy.bankDeposit(name, amount).transactionSuccess();
	}

	@Override
	public String getName() {
		return this.vault.getDescription().getName();
	}

	@Override
	public String getVersion() {
		return this.vault.getDescription().getVersion();
	}

	@Override
	public int fractionalDigits() {
		return this.economy.fractionalDigits();
	}

	@Override
	public String format(double amount) {
		return this.economy.format(amount);
	}

	@Override
	public boolean hasBanks() {
		return this.economy.hasBankSupport();
	}

	@Override
	public boolean hasBank(String bank) {
		return this.economy.getBanks().contains(bank);
	}

	@Override
	public boolean hasAccount(String name) {
		return this.economy.hasAccount(name);
	}

	@Override
	public boolean hasBankAccount(String bank, String name) {
		return this.economy.isBankOwner(bank, name).transactionSuccess()
				|| this.economy.isBankMember(bank, name).transactionSuccess();
	}

	@Override
	public boolean createAccount(String name) {
		return this.economy.createPlayerAccount(name);
	}

	@Override
	public MethodAccount getAccount(String name) {
		if (!hasAccount(name)) {
			return null;
		}

		return new VaultAccount(name, this.economy);
	}

	@Override
	public MethodBankAccount getBankAccount(String bank, String name) {
		if (!hasBankAccount(bank, name)) {
			return null;
		}

		return new VaultBankAccount(bank, economy);
	}

	@Override
	public boolean isCompatible(Plugin plugin) {
		return plugin instanceof Vault;
	}

	@Override
	public void setPlugin(Plugin plugin) {
		this.vault = (Vault) plugin;
		RegisteredServiceProvider<Economy> economyProvider = this.vault.getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (economyProvider != null) {
			this.economy = economyProvider.getProvider();
		}
	}

	public class VaultAccount implements MethodAccount {
		private final String name;
		private final Economy economy;

		public VaultAccount(String name, Economy economy) {
			this.name = name;
			this.economy = economy;
		}

		@Override
		public double balance() {
			return this.economy.getBalance(this.name);
		}

		@Override
		public boolean set(double amount) {
			if (!this.economy.withdrawPlayer(this.name, this.balance()).transactionSuccess()) {
				return false;
			}

			if (amount == 0) {
				return true;
			}
			return this.economy.depositPlayer(this.name, amount).transactionSuccess();
		}

		@Override
		public boolean add(double amount) {
			return this.economy.depositPlayer(this.name, amount).transactionSuccess();
		}

		@Override
		public boolean subtract(double amount) {
			return this.economy.withdrawPlayer(this.name, amount).transactionSuccess();
		}

		@Override
		public boolean multiply(double amount) {
			double balance = this.balance();
			return this.set(balance * amount);
		}

		@Override
		public boolean divide(double amount) {
			double balance = this.balance();
			return this.set(balance / amount);
		}

		@Override
		public boolean hasEnough(double amount) {
			return (this.balance() >= amount);
		}

		@Override
		public boolean hasOver(double amount) {
			return (this.balance() > amount);
		}

		@Override
		public boolean hasUnder(double amount) {
			return (this.balance() < amount);
		}

		@Override
		public boolean isNegative() {
			return (this.balance() < 0);
		}

		@Override
		public boolean remove() {
			return this.set(0.0);
		}
	}

	public class VaultBankAccount implements MethodBankAccount {

		private final String bank;
		private final Economy economy;

		public VaultBankAccount(String bank, Economy economy) {
			this.bank = bank;
			this.economy = economy;
		}

		@Override
		public String getBankName() {
			return this.bank;
		}

		@Override
		public int getBankId() {
			return -1;
		}

		@Override
		public double balance() {
			return this.economy.bankBalance(this.bank).balance;
		}

		@Override
		public boolean set(double amount) {
			if (!this.economy.bankWithdraw(this.bank, this.balance()).transactionSuccess()) {
				return false;
			}
			if (amount == 0) {
				return true;
			}
			return this.economy.bankDeposit(this.bank, amount).transactionSuccess();
		}

		@Override
		public boolean add(double amount) {
			return this.economy.bankDeposit(this.bank, amount).transactionSuccess();
		}

		@Override
		public boolean subtract(double amount) {
			return this.economy.bankWithdraw(this.bank, amount).transactionSuccess();
		}

		@Override
		public boolean multiply(double amount) {
			double balance = this.balance();
			return this.set(balance * amount);
		}

		@Override
		public boolean divide(double amount) {
			double balance = this.balance();
			return this.set(balance / amount);
		}

		@Override
		public boolean hasEnough(double amount) {
			return (this.balance() >= amount);
		}

		@Override
		public boolean hasOver(double amount) {
			return (this.balance() > amount);
		}

		@Override
		public boolean hasUnder(double amount) {
			return (this.balance() < amount);
		}

		@Override
		public boolean isNegative() {
			return (this.balance() < 0);
		}

		@Override
		public boolean remove() {
			return this.set(0.0);
		}

	}
}
