package net.milkbowl.vault.economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Utility methods for common operations based on the Economy API. 
 * These methods do not require reimplementation for each economy plugin.
 * 
 * @author jast
 *
 */
public class EconomyUtil {
    
    private static Economy economy;
    
    static {
        // setup the economy instance
        setupEconomy();
    }

    /**
     * Initiate a money transfer transaction between accounts, either players or banks.
     * Transfers may be done in a simple chain call, for instance:<pre>
     * EconomyUtil.send(23.2).fromPlayer("notch").toPlayer("jeb_")
     * EconomyUtil.send(700).fromPlayer("ebenwert").toBank("Gringotts")
     * </pre>
     * 
     * @param value value to transfer
     * @return
     */
    public static SendTransaction send(double value) {
        return new SendTransaction(value);
    }
    
    /**
     * Initial part of a transaction. Allows specifying a sender.
     */
    public static class SendTransaction {
        private final double value;
        private SendTransaction(double value) {
            this.value = value;
        }
        
        /**
         * Send the money from a player account.
         * @param name name of the player
         * @return final part of transaction, which allows specifying the recipient
         */
        public Sender fromPlayer(String name) {
            return new PlayerSender(value, name);
        }
        
        /**
         * Send the money from a bank account.
         * @param name name of the bank
         * @return final part of transaction, which allows specifying the recipient
         */
        public Sender fromBank(String name) {
            return new BankSender(value, name);
        }
    }
    
    /**
     * Handles sending of money from one account to another.
     */
    public static abstract class Sender {
        protected final double amount;
        protected final String from;
        
        protected Sender(double amount, String from) {
            this.amount = amount;
            this.from = from;
        }
        
        abstract protected EconomyResponse withdraw(double amount);
        abstract protected EconomyResponse deposit(double amount);
        
        /**
         * Send the previously specified amount to a player's account. 
         * This action withdraws the amount from the sender, and deposits it to the recipient's account.
         * If either the withdrawal or the deposit fails, the transaction as a whole also fails.
         * This means that both the sender and the recipient's account balance remains unchanged.
         * 
         * @param to name of player to send to
         * @return outcome of the transaction
         */
        public EconomyResponse toPlayer(String to) {
            EconomyResponse w = withdraw(amount);
            if (w.transactionSuccess()) {
                EconomyResponse d = economy.depositPlayer(to, amount);
                if ( ! d.transactionSuccess()) {
                    deposit(amount);
                }
                return d;
            }
            return w;
        }
        
        /**
         * Send the previously specified amount to a bank account. 
         * This action withdraws the amount from the sender, and deposits it to the recipient's account.
         * If either the withdrawal or the deposit fails, the transaction as a whole also fails.
         * This means that both the sender and the recipient's account balance remains unchanged.
         * 
         * @param to name of bank to send to
         * @return outcome of the transaction
         */
        public EconomyResponse toBank(String to) {
            EconomyResponse w = withdraw(amount);
            if (w.transactionSuccess()) {
                EconomyResponse d = economy.bankDeposit(to, amount);
                if ( ! d.transactionSuccess()) {
                    deposit(amount);
                }
                return d;
            }
            return w;
        }
    }
    
    private static class PlayerSender extends Sender {
        
        private PlayerSender(double amount, String player) {
            super(amount, player);
        }

        @Override
        protected EconomyResponse withdraw(double amount) {
            return economy.withdrawPlayer(from, amount);
        }

        @Override
        protected EconomyResponse deposit(double amount) {
            return economy.depositPlayer(from, amount);
        }
        

    }
    
    private static class BankSender extends Sender {

        private BankSender(double amount, String bank) {
            super(amount, bank);
        }

        @Override
        protected EconomyResponse withdraw(double amount) {
            return economy.bankWithdraw(from, amount);
        }

        @Override
        protected EconomyResponse deposit(double amount) {
            return economy.bankDeposit(from, amount);
        }
    }
    
    
    private static boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = 
                Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
