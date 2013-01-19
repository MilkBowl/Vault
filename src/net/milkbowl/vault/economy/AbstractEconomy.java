package net.milkbowl.vault.economy;

/**
 * Abstract extension of the Economy interface, which offers implementation of several utility methods based on the supported primitives.
 */
public abstract class AbstractEconomy implements Economy {

    /*
     * (non-Javadoc)
     * @see net.milkbowl.vault.economy.Economy#transfer(double, java.lang.String, java.lang.String)
     */
    public EconomyResponse transfer(double amount, String playerFrom, String playerTo) {
        EconomyResponse w = withdrawPlayer(playerFrom, amount);
        if (w.transactionSuccess()) {
            EconomyResponse d = depositPlayer(playerTo, amount);
            if ( ! d.transactionSuccess()) {
                depositPlayer(playerFrom, amount);
            }
            return d;
        }
        return w;
    }
    
    /*
     * (non-Javadoc)
     * @see net.milkbowl.vault.economy.Economy#transferPlayerToBank(double, java.lang.String, java.lang.String)
     */
    public EconomyResponse transferPlayerToBank(double amount, String playerFrom, String bankTo) {
        EconomyResponse w = withdrawPlayer(playerFrom, amount);
        if (w.transactionSuccess()) {
            EconomyResponse d = bankDeposit(playerFrom, amount);
            if ( ! d.transactionSuccess()) {
                depositPlayer(playerFrom, amount);
            }
            return d;
        }
        return w;
    }
    
    /* 
     * (non-Javadoc)
     * @see net.milkbowl.vault.economy.Economy#transferBankToPlayer(double, java.lang.String, java.lang.String)
     */
    public EconomyResponse transferBankToPlayer(double amount, String bankFrom, String playerTo) {
        EconomyResponse w = bankWithdraw(bankFrom, amount);
        if (w.transactionSuccess()) {
            EconomyResponse d = depositPlayer(playerTo, amount);
            if ( ! d.transactionSuccess()) {
                depositPlayer(bankFrom, amount);
            }
            return d;
        }
        return w;
    }
}
