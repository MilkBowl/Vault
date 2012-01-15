package net.milkbowl.vault;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.nijikokun.register.payment.Method;

public class VaultEco implements Method {

    private Vault vault;
    private Economy economy;

    public Vault getPlugin() {
        return this.vault;
    }

    @Override
    public boolean createAccount(String name, Double amount) {
        if (hasAccount(name)) {
            return false;
        }

        return false;
    }

    public String getName() {
        return this.vault.getDescription().getName();
    }

    public String getVersion() {
        return this.vault.getDescription().getVersion();
    }

    public int fractionalDigits() {
        return 0;
    }

    public String format(double amount) {
        return this.economy.format(amount);
    }

    public boolean hasBanks() {
        return this.economy.hasBankSupport();
    }

    public boolean hasBank(String bank) {
        return this.economy.getBanks().contains(bank);
    }

    public boolean hasAccount(String name) {
        return this.economy.hasAccount(name);
    }

    public boolean hasBankAccount(String bank, String name) {
        return this.economy.isBankOwner(bank, name).transactionSuccess()
                || this.economy.isBankMember(bank, name).transactionSuccess();
    }

    public boolean createAccount(String name) {
        return this.economy.createBank(name, "").transactionSuccess();
    }

    public boolean createAccount(String name, double balance) {
        if(!this.economy.createBank(name, "").transactionSuccess()) {
            return false;
        }
        return this.economy.bankDeposit(name, balance).transactionSuccess();
    }

    public MethodAccount getAccount(String name) {
        if(!hasAccount(name)) 
            return null;

        return new VaultAccount(name, this.economy);
    }

    public MethodBankAccount getBankAccount(String bank, String name) {
        if(!hasBankAccount(bank, name)) 
            return null;

        return new VaultBankAccount(bank, economy);
    }

    public boolean isCompatible(Plugin plugin) {
        return plugin instanceof Vault;
    }

    public void setPlugin(Plugin plugin) {
        this.vault = (Vault) plugin;
        RegisteredServiceProvider<Economy> economyProvider = this.vault.getServer().getServicesManager().getRegistration(Economy.class);
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

        public double balance() {
            return this.economy.getBalance(this.name);
        }

        public boolean set(double amount) {
            if(!this.economy.withdrawPlayer(this.name, this.balance()).transactionSuccess()) {
                return false;
            }

            if(amount == 0) {
                return true;
            }
            return this.economy.depositPlayer(this.name, amount).transactionSuccess();
        }

        public boolean add(double amount) {
            return this.economy.depositPlayer(this.name, amount).transactionSuccess();
        }

        public boolean subtract(double amount) {
            return this.economy.withdrawPlayer(this.name, amount).transactionSuccess();
        }

        public boolean multiply(double amount) {
            double balance = this.balance();
            return this.set(balance * amount);
        }

        public boolean divide(double amount) {
            double balance = this.balance();
            return this.set(balance / amount);
        }

        public boolean hasEnough(double amount) {
            return (this.balance() >= amount);
        }

        public boolean hasOver(double amount) {
            return (this.balance() > amount);
        }

        public boolean hasUnder(double amount) {
            return (this.balance() < amount);
        }

        public boolean isNegative() {
            return (this.balance() < 0);
        }

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

        public String getBankName() {
            return this.bank;
        }

        public int getBankId() {
            return -1;
        }

        public double balance() {
            return this.economy.bankBalance(this.bank).balance;
        }

        public boolean set(double amount) {
            if(!this.economy.bankWithdraw(this.bank, this.balance()).transactionSuccess()) {
                return false;
            }
            if(amount == 0) {
                return true;
            }
            return this.economy.bankDeposit(this.bank, amount).transactionSuccess();
        }

        public boolean add(double amount) {
            return this.economy.bankDeposit(this.bank, amount).transactionSuccess();
        }

        public boolean subtract(double amount) {
            return this.economy.bankWithdraw(this.bank, amount).transactionSuccess();
        }

        public boolean multiply(double amount) {
            double balance = this.balance();
            return this.set(balance * amount);
        }

        public boolean divide(double amount) {
            double balance = this.balance();
            return this.set(balance / amount);
        }

        public boolean hasEnough(double amount) {
            return (this.balance() >= amount);
        }

        public boolean hasOver(double amount) {
            return (this.balance() > amount);
        }

        public boolean hasUnder(double amount) {
            return (this.balance() < amount);
        }

        public boolean isNegative() {
            return (this.balance() < 0);
        }

        public boolean remove() {
            return this.set(0.0);
        }

    }
}