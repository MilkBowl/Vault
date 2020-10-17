package net.milkbowl.vault.economy.plugins;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;

public class Economy_EssentialsX extends Economy_Essentials {

    public Economy_EssentialsX(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return Economy.playerExists(player.getUniqueId());
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        double balance;
        try {
            balance = Economy.getMoneyExact(player.getUniqueId()).doubleValue();
        } catch (UserDoesNotExistException e) {
            createPlayerAccount(player);
            balance = 0;
        }
        return balance;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        try {
            return Economy.hasEnough(player.getUniqueId(), BigDecimal.valueOf(amount));
        } catch (UserDoesNotExistException e) {
            return false;
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player can not be null.");
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
        }

        BigDecimal balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;

        try {
            com.earth2me.essentials.api.Economy.subtract(player.getUniqueId(), BigDecimal.valueOf(amount));
            balance = com.earth2me.essentials.api.Economy.getMoneyExact(player.getUniqueId());
            type = EconomyResponse.ResponseType.SUCCESS;
        } catch (UserDoesNotExistException e) {
            if (createPlayerAccount(player)) {
                return withdrawPlayer(player, amount);
            } else {
                amount = 0;
                balance = BigDecimal.ZERO;
                type = EconomyResponse.ResponseType.FAILURE;
                errorMessage = "User does not exist";
            }
        } catch (NoLoanPermittedException e) {
            try {
                balance = com.earth2me.essentials.api.Economy.getMoneyExact(player.getUniqueId());
                amount = 0;
                type = EconomyResponse.ResponseType.FAILURE;
                errorMessage = "Loan was not permitted";
            } catch (UserDoesNotExistException e1) {
                amount = 0;
                balance = BigDecimal.ZERO;
                type = EconomyResponse.ResponseType.FAILURE;
                errorMessage = "User does not exist";
            }
        }

        return new EconomyResponse(amount, balance.doubleValue(), type, errorMessage);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return tryDepositPlayer(player, amount, 2);
    }

    public EconomyResponse tryDepositPlayer(OfflinePlayer player, double amount, int tries) {
        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player can not be null.");
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
        }
        if (tries <= 0) {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Failed to deposit amount.");
        }

        BigDecimal balance;
        EconomyResponse.ResponseType type;
        String errorMessage = null;

        try {
            com.earth2me.essentials.api.Economy.add(player.getUniqueId(), BigDecimal.valueOf(amount));
            balance = com.earth2me.essentials.api.Economy.getMoneyExact(player.getUniqueId());
            type = EconomyResponse.ResponseType.SUCCESS;
        } catch (UserDoesNotExistException e) {
            if (createPlayerAccount(player)) {
                return tryDepositPlayer(player, amount, --tries);
            } else {
                amount = 0;
                balance = BigDecimal.ZERO;
                type = EconomyResponse.ResponseType.FAILURE;
                errorMessage = "User does not exist";
            }
        } catch (NoLoanPermittedException e) {
            try {
                balance = com.earth2me.essentials.api.Economy.getMoneyExact(player.getUniqueId());
                amount = 0;
                type = EconomyResponse.ResponseType.FAILURE;
                errorMessage = "Loan was not permitted";
            } catch (UserDoesNotExistException e1) {
                balance = BigDecimal.ZERO;
                amount = 0;
                type = EconomyResponse.ResponseType.FAILURE;
                errorMessage = "Loan was not permitted";
            }
        }

        return new EconomyResponse(amount, balance.doubleValue(), type, errorMessage);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return createPlayerAccount(player.getName());
    }
}
