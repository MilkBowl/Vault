/**
 * Copyright (C) 2011 Morgan Humes <morgan@lanaddict.com>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

package net.milkbowl.vault.economy;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.plugins.Economy_3co;
import net.milkbowl.vault.economy.plugins.Economy_BOSE;
import net.milkbowl.vault.economy.plugins.Economy_Essentials;
import net.milkbowl.vault.economy.plugins.Economy_iConomy4;
import net.milkbowl.vault.economy.plugins.Economy_iConomy5;

import org.bukkit.plugin.java.JavaPlugin;

public class EconomyManager {

    private JavaPlugin plugin = null;
    private TreeMap<Integer, Economy> econs = new TreeMap<Integer, Economy>();
    private Economy activeEconomy = null;
    private static final Logger log = Logger.getLogger("Minecraft");

    /**
     * Constructs a new instance of EconomyManager provided an instance of a JavaPlugin
     * @param plugin Your plugin (should be "this")
     */
    public EconomyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        
        // Try to load 3co
        if(packageExists(new String[] { "me.ic3d.eco.ECO" })) {
            Economy econ = new Economy_3co(plugin);
            econs.put(11, econ);
            log.info(String.format("[%s][Economy] 3co found: %s", plugin.getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] 3co not found.", plugin.getDescription().getName()));
        }
        
        // Try to load BOSEconomy
        if (packageExists(new String[] { "cosine.boseconomy.BOSEconomy" })) {
            Economy bose = new Economy_BOSE(plugin);
            econs.put(10, bose);
            log.info(String.format("[%s][Economy] BOSEconomy found: %s", plugin.getDescription().getName(), bose.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] BOSEconomy not found.", plugin.getDescription().getName()));
        }

        // Try to load Essentials Economy
        if (packageExists(new String[] { "com.earth2me.essentials.api.Economy", "com.earth2me.essentials.api.NoLoanPermittedException", "com.earth2me.essentials.api.UserDoesNotExistException" })) {
            Economy essentials = new Economy_Essentials(plugin);
            econs.put(9, essentials);
            log.info(String.format("[%s][Economy] Essentials Economy found: %s", plugin.getDescription().getName(), essentials.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] Essentials Economy not found.", plugin.getDescription().getName()));
        }

        // Try to load iConomy 4
        if (packageExists(new String[] { "com.nijiko.coelho.iConomy.iConomy", "com.nijiko.coelho.iConomy.system.Account" })) {
            Economy icon4 = new Economy_iConomy4(plugin);
            econs.put(8, icon4);
            log.info(String.format("[%s][Economy] iConomy 4 found: ", plugin.getDescription().getName(), icon4.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] iConomy 4 not found.", plugin.getDescription().getName()));
        }

        // Try to load iConomy 5
        if (packageExists(new String[] { "com.iConomy.iConomy", "com.iConomy.system.Account", "com.iConomy.system.Holdings" })) {
            Economy icon5 = new Economy_iConomy5(plugin);
            econs.put(7, icon5);
            log.info(String.format("[%s][Economy] iConomy 5 found: %s", plugin.getDescription().getName(), icon5.isEnabled() ? "Loaded" : "Waiting"));
        } else {
            log.info(String.format("[%s][Economy] iConomy 5 not found.", plugin.getDescription().getName()));
        }
    }

    private boolean packageExists(String[] packages) {
        try {
            for (String pkg : packages) {
                Class.forName(pkg);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Economy getEconomy() {
        if (activeEconomy == null) {
            Iterator<Economy> it = econs.values().iterator();
            while (it.hasNext()) {
                Economy e = it.next();
                if (e.isEnabled()) {
                    return e;
                }
            }
            return null;
        } else {
            return activeEconomy;
        }
    }

    /**
     * Accessor for Name of Economy
     * @return Name of active Economy
     */
    public String getName() {
        return getEconomy().getName();
    }

    /**
     * Formats value to human readable forms
     * @param amount Value to format
     * @return Human readable form of amount
     */
    public String format(double amount) {
        return getEconomy().format(amount);
    }

    /**
     * Returns current player balance
     * @param playerName Player name
     * @return Response containing amount (balance) and other meta data
     */
    public EconomyResponse getBalance(String playerName) {
        return getEconomy().getBalance(playerName);
    }

    /**
     * Withdraw amount from a player account
     * @param playerName Player name
     * @param amount Amount to withdraw
     * @return Response containing amount removed, and new balance
     */
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return getEconomy().withdrawPlayer(playerName, amount);
    }

    /**
     * Deposit amount to a player account
     * @param playerName Player name
     * @param amount Amount to deposit
     * @return Response containing amount added, and new balance
     */
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return getEconomy().depositPlayer(playerName, amount);
    }
}