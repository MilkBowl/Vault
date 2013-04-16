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

import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.chat.plugins.Chat_DroxPerms;
import net.milkbowl.vault.chat.plugins.Chat_GroupManager;
import net.milkbowl.vault.chat.plugins.Chat_Permissions3;
import net.milkbowl.vault.chat.plugins.Chat_PermissionsEx;
import net.milkbowl.vault.chat.plugins.Chat_bPermissions;
import net.milkbowl.vault.chat.plugins.Chat_bPermissions2;
import net.milkbowl.vault.chat.plugins.Chat_iChat;
import net.milkbowl.vault.chat.plugins.Chat_mChat;
import net.milkbowl.vault.chat.plugins.Chat_mChatSuite;
import net.milkbowl.vault.chat.plugins.Chat_zPermissions;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.plugins.Economy_3co;
import net.milkbowl.vault.economy.plugins.Economy_AEco;
import net.milkbowl.vault.economy.plugins.Economy_BOSE6;
import net.milkbowl.vault.economy.plugins.Economy_BOSE7;
import net.milkbowl.vault.economy.plugins.Economy_CommandsEX;
import net.milkbowl.vault.economy.plugins.Economy_Craftconomy;
import net.milkbowl.vault.economy.plugins.Economy_Craftconomy3;
import net.milkbowl.vault.economy.plugins.Economy_CurrencyCore;
import net.milkbowl.vault.economy.plugins.Economy_Dosh;
import net.milkbowl.vault.economy.plugins.Economy_EconXP;
import net.milkbowl.vault.economy.plugins.Economy_Essentials;
import net.milkbowl.vault.economy.plugins.Economy_GoldIsMoney;
import net.milkbowl.vault.economy.plugins.Economy_GoldIsMoney2;
import net.milkbowl.vault.economy.plugins.Economy_Gringotts;
import net.milkbowl.vault.economy.plugins.Economy_McMoney;
import net.milkbowl.vault.economy.plugins.Economy_MineConomy;
import net.milkbowl.vault.economy.plugins.Economy_MultiCurrency;
import net.milkbowl.vault.economy.plugins.Economy_SDFEconomy;
import net.milkbowl.vault.economy.plugins.Economy_XPBank;
import net.milkbowl.vault.economy.plugins.Economy_eWallet;
import net.milkbowl.vault.economy.plugins.Economy_iConomy4;
import net.milkbowl.vault.economy.plugins.Economy_iConomy5;
import net.milkbowl.vault.economy.plugins.Economy_iConomy6;
import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.permission.plugins.Permission_DroxPerms;
import net.milkbowl.vault.permission.plugins.Permission_GroupManager;
import net.milkbowl.vault.permission.plugins.Permission_Permissions3;
import net.milkbowl.vault.permission.plugins.Permission_PermissionsBukkit;
import net.milkbowl.vault.permission.plugins.Permission_PermissionsEx;
import net.milkbowl.vault.permission.plugins.Permission_Privileges;
import net.milkbowl.vault.permission.plugins.Permission_SimplyPerms;
import net.milkbowl.vault.permission.plugins.Permission_Starburst;
import net.milkbowl.vault.permission.plugins.Permission_SuperPerms;
import net.milkbowl.vault.permission.plugins.Permission_Xperms;
import net.milkbowl.vault.permission.plugins.Permission_bPermissions;
import net.milkbowl.vault.permission.plugins.Permission_bPermissions2;
import net.milkbowl.vault.permission.plugins.Permission_zPermissions;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

public class Vault{

    private static final Logger log = Logger.getLogger("Minecraft");
    private Permission perms;

    private ServicesManager sm;
    private Plugin vault;

    public Vault(ServicesManager sm, VaultPlugin vault) {
        this.sm = sm;
        this.vault = vault;
        loadChat();
        loadEconomy();
        loadPermission();
    }
    /**
     * Attempts to load Chat Addons
     */
    private void loadChat() {
        // Try to load PermissionsEx
        hookChat("PermissionsEx", Chat_PermissionsEx.class, ServicePriority.Highest, "ru.tehkode.permissions.bukkit.PermissionsEx");

        // Try to load mChatSuite
        hookChat("mChatSuite", Chat_mChatSuite.class, ServicePriority.Highest, "in.mDev.MiracleM4n.mChatSuite.mChatSuite");

        // Try to load mChat
        hookChat("mChat", Chat_mChat.class, ServicePriority.Highest, "net.D3GN.MiracleM4n.mChat");

        // Try to load DroxPerms Chat
        hookChat("DroxPerms", Chat_DroxPerms.class, ServicePriority.Lowest, "de.hydrox.bukkit.DroxPerms.DroxPerms");

        // Try to load bPermssions 2
        hookChat("bPermssions2", Chat_bPermissions2.class, ServicePriority.Highest, "de.bananaco.bpermissions.api.ApiLayer");

        // Try to load bPermissions 1
        hookChat("bPermissions", Chat_bPermissions.class, ServicePriority.Normal, "de.bananaco.permissions.info.InfoReader");

        // Try to load GroupManager
        hookChat("GroupManager", Chat_GroupManager.class, ServicePriority.Normal, "org.anjocaido.groupmanager.GroupManager");

        // Try to load Permissions 3 (Yeti)
        hookChat("Permissions3", Chat_Permissions3.class, ServicePriority.Normal, "com.nijiko.permissions.ModularControl");

        // Try to load iChat
        hookChat("iChat", Chat_iChat.class, ServicePriority.Low, "net.TheDgtl.iChat.iChat");

        // Try to load zPermissions
        hookChat("zPermissions", Chat_zPermissions.class, ServicePriority.Normal, "org.tyrannyofheaven.bukkit.zPermissions.model.EntityMetadata");
    }

    /**
     * Attempts to load Economy Addons
     */
    private void loadEconomy() {
        // Try to load MultiCurrency
        hookEconomy("MultiCurrency", Economy_MultiCurrency.class, ServicePriority.Normal, "me.ashtheking.currency.Currency", "me.ashtheking.currency.CurrencyList");

        // Try to load MineConomy
        hookEconomy("MineConomy", Economy_MineConomy.class, ServicePriority.Normal, "me.mjolnir.mineconomy.MineConomy");

        // Try to load AEco
        hookEconomy("AEco", Economy_AEco.class, ServicePriority.Normal, "org.neocraft.AEco.AEco");

        // Try to load McMoney
        hookEconomy("McMoney", Economy_McMoney.class, ServicePriority.Normal, "boardinggamer.mcmoney.McMoneyAPI");

        // Try to load Craftconomy
        hookEconomy("CraftConomy", Economy_Craftconomy.class, ServicePriority.Normal, "me.greatman.Craftconomy.Craftconomy");

        // Try to load Craftconomy3
        hookEconomy("CraftConomy3", Economy_Craftconomy3.class, ServicePriority.Normal, "com.greatmancode.craftconomy3.BukkitLoader");

        // Try to load eWallet
        hookEconomy("eWallet", Economy_eWallet.class, ServicePriority.Normal, "me.ethan.eWallet.ECO");

        // Try to load 3co
        hookEconomy("3co", Economy_3co.class, ServicePriority.Normal, "me.ic3d.eco.ECO");

        // Try to load BOSEconomy 6
        hookEconomy("BOSEconomy6", Economy_BOSE6.class, ServicePriority.Normal, "cosine.boseconomy.BOSEconomy", "cosine.boseconomy.CommandManager");

        // Try to load BOSEconomy 7
        hookEconomy("BOSEconomy7", Economy_BOSE7.class, ServicePriority.Normal, "cosine.boseconomy.BOSEconomy", "cosine.boseconomy.CommandHandler");

        // Try to load CurrencyCore
        hookEconomy("CurrencyCore", Economy_CurrencyCore.class, ServicePriority.Normal, "is.currency.Currency");

        // Try to load Gringotts
        hookEconomy("Gringotts", Economy_Gringotts.class, ServicePriority.Normal, "org.gestern.gringotts.Gringotts");

        // Try to load Essentials Economy
        hookEconomy("Essentials Economy", Economy_Essentials.class, ServicePriority.Low, "com.earth2me.essentials.api.Economy", "com.earth2me.essentials.api.NoLoanPermittedException",  "com.earth2me.essentials.api.UserDoesNotExistException");

        // Try to load iConomy 4
        hookEconomy("iConomy 4", Economy_iConomy4.class, ServicePriority.High, "com.nijiko.coelho.iConomy.iConomy", "com.nijiko.coelho.iConomy.system.Account");

        // Try to load iConomy 5
        hookEconomy("iConomy 5", Economy_iConomy5.class, ServicePriority.High, "com.iConomy.iConomy", "com.iConomy.system.Account", "com.iConomy.system.Holdings");

        // Try to load iConomy 6
        hookEconomy("iConomy 6", Economy_iConomy6.class, ServicePriority.High, "com.iCo6.iConomy");

        // Try to load EconXP
        hookEconomy("EconXP", Economy_EconXP.class, ServicePriority.Normal, "ca.agnate.EconXP.EconXP");

        // Try to load GoldIsMoney
        hookEconomy("GoldIsMoney", Economy_GoldIsMoney.class, ServicePriority.Normal, "com.flobi.GoldIsMoney.GoldIsMoney");

        // Try to load GoldIsMoney2
        hookEconomy("GoldIsMoney2", Economy_GoldIsMoney2.class, ServicePriority.Normal, "com.flobi.GoldIsMoney2.GoldIsMoney");

        // Try to load Dosh
        hookEconomy("Dosh", Economy_Dosh.class, ServicePriority.Normal, "com.gravypod.Dosh.Dosh");

        // Try to load CommandsEX Economy
        hookEconomy("CommandsEX", Economy_CommandsEX.class, ServicePriority.Normal, "com.github.zathrus_writer.commandsex.api.EconomyAPI");
         
        // Try to load SDFEconomy Economy
        hookEconomy("SDFEconomy", Economy_SDFEconomy.class, ServicePriority.Normal, "com.github.omwah.SDFEconomy.SDFEconomy");
        
        // Try to load XPBank
        hookEconomy("XPBank", Economy_XPBank.class, ServicePriority.Normal, "com.gmail.mirelatrue.xpbank.XPBank");
    }

    /**
     * Attempts to load Permission Addons
     */
    private void loadPermission() {
        // Try to load Starburst
        hookPermission("Starburst", Permission_Starburst.class, ServicePriority.Highest, "com.dthielke.starburst.StarburstPlugin");

        // Try to load PermissionsEx
        hookPermission("PermissionsEx", Permission_PermissionsEx.class, ServicePriority.Highest, "ru.tehkode.permissions.bukkit.PermissionsEx");

        // Try to load PermissionsBukkit
        hookPermission("PermissionsBukkit", Permission_PermissionsBukkit.class, ServicePriority.Normal, "com.platymuus.bukkit.permissions.PermissionsPlugin");

        // Try to load DroxPerms
        hookPermission("DroxPerms", Permission_DroxPerms.class, ServicePriority.High, "de.hydrox.bukkit.DroxPerms.DroxPerms");

        // Try to load SimplyPerms
        hookPermission("SimplyPerms", Permission_SimplyPerms.class, ServicePriority.Highest, "net.crystalyx.bukkit.simplyperms.SimplyPlugin");

        // Try to load bPermissions2
        hookPermission("bPermissions 2", Permission_bPermissions2.class, ServicePriority.Highest, "de.bananaco.bpermissions.api.WorldManager");

        // Try to load zPermission
        hookPermission("zPermissions", Permission_zPermissions.class, ServicePriority.Highest, "org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsPlugin");

        // Try to load Privileges
        hookPermission("Privileges", Permission_Privileges.class, ServicePriority.Highest, "net.krinsoft.privileges.Privileges");

        // Try to load bPermissions
        hookPermission("bPermissions", Permission_bPermissions.class, ServicePriority.High, "de.bananaco.permissions.SuperPermissionHandler");

        // Try to load GroupManager
        hookPermission("GroupManager", Permission_GroupManager.class, ServicePriority.High, "org.anjocaido.groupmanager.GroupManager");

        // Try to load Permissions 3 (Yeti)
        hookPermission("Permissions 3 (Yeti)", Permission_Permissions3.class, ServicePriority.Normal, "com.nijiko.permissions.ModularControl");
        
        // Try to load Xperms
        hookPermission("Xperms", Permission_Xperms.class, ServicePriority.Low, "com.github.sebc722.Xperms");

        Permission perms = new Permission_SuperPerms(vault);
        sm.register(Permission.class, perms, vault, ServicePriority.Lowest);
        log.info(String.format("[%s][Permission] SuperPermissions loaded as backup permission system.", vault.getDescription().getName()));

        this.perms = sm.getRegistration(Permission.class).getProvider();
    }

    public void hookChat (String name, Class<? extends Chat> hookClass, ServicePriority priority, String...packages) {
        try {
            if (packagesExists(packages)) {
                Chat chat = hookClass.getConstructor(Plugin.class, Permission.class).newInstance(vault, perms);
                sm.register(Chat.class, chat, vault, priority);
                log.info(String.format("[%s][Chat] %s found: %s", vault.getDescription().getName(), name, chat.isEnabled() ? "Loaded" : "Waiting"));
            }
        } catch (Exception e) {
            log.severe(String.format("[%s][Chat] There was an error hooking %s - check to make sure you're using a compatible version!", vault.getDescription().getName(), name));
        }
    }

    public void hookEconomy (String name, Class<? extends Economy> hookClass, ServicePriority priority, String...packages) {
        try {
            if (packagesExists(packages)) {
                Economy econ = hookClass.getConstructor(Plugin.class).newInstance(vault);
                sm.register(Economy.class, econ, vault, priority);
                log.info(String.format("[%s][Economy] %s found: %s", vault.getDescription().getName(), name, econ.isEnabled() ? "Loaded" : "Waiting"));
            }
            else{
                for(String pkg : packages){
                    log.warning("'" + pkg + "' do not exists!");
                }
            }
        } catch (Exception e) {
            log.severe(String.format("[%s][Economy] There was an error hooking %s - check to make sure you're using a compatible version!", vault.getDescription().getName(), name));
            e.printStackTrace();
        }
    }

    public void hookPermission (String name, Class<? extends Permission> hookClass, ServicePriority priority, String...packages) {
        try {
            if (packagesExists(packages)) {
                Permission perms = hookClass.getConstructor(Plugin.class).newInstance(vault);
                sm.register(Permission.class, perms, vault, priority);
                log.info(String.format("[%s][Permission] %s found: %s", vault.getDescription().getName(), name, perms.isEnabled() ? "Loaded" : "Waiting"));
            }
        } catch (Exception e) {
            log.severe(String.format("[%s][Permission] There was an error hooking %s - check to make sure you're using a compatible version!", vault.getDescription().getName(), name));
            e.printStackTrace();
        }
    }

    /**
     * Determines if all packages in a String array are within the Classpath
     * This is the best way to determine if a specific plugin exists and will be
     * loaded. If the plugin package isn't loaded, we shouldn't bother waiting
     * for it!
     * @param packages String Array of package names to check
     * @return Success or Failure
     */
    private static boolean packagesExists(String...packages) {
        try {
            for (String pkg : packages) {
                Class.forName(pkg);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Permission getPerms() {
        return perms;
    }
}
