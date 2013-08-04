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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.chat.plugins.Chat_DroxPerms;
import net.milkbowl.vault.chat.plugins.Chat_GroupManager;
import net.milkbowl.vault.chat.plugins.Chat_Permissions3;
import net.milkbowl.vault.chat.plugins.Chat_PermissionsEx;
import net.milkbowl.vault.chat.plugins.Chat_Privileges;
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
import net.milkbowl.vault.economy.plugins.Economy_XPBank;
import net.milkbowl.vault.economy.plugins.Economy_eWallet;
import net.milkbowl.vault.economy.plugins.Economy_iConomy4;
import net.milkbowl.vault.economy.plugins.Economy_iConomy5;
import net.milkbowl.vault.economy.plugins.Economy_iConomy6;
import net.milkbowl.vault.economy.plugins.Economy_SDFEconomy;
import net.milkbowl.vault.economy.plugins.Economy_Minefaconomy;  
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
import net.milkbowl.vault.permission.plugins.Permission_TotalPermissions;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nijikokun.register.payment.Methods;
import net.milkbowl.vault.economy.plugins.Economy_MiConomy;

public class Vault extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private Permission perms;
    private double newVersion;
    private double currentVersion;
    private ServicesManager sm;
    private Metrics metrics;

    @Override
    public void onDisable() {
        // Remove all Service Registrations
        getServer().getServicesManager().unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        currentVersion = Double.valueOf(getDescription().getVersion().split("-")[0].replaceFirst("\\.", ""));
        sm = getServer().getServicesManager();
        // Load Vault Addons
        loadEconomy();
        loadPermission();
        loadChat();

        getCommand("vault-info").setExecutor(this);
        getCommand("vault-convert").setExecutor(this);
        getServer().getPluginManager().registerEvents(new VaultListener(), this);

        // Schedule to check the version every 30 minutes for an update. This is to update the most recent 
        // version so if an admin reconnects they will be warned about newer versions.
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {

            @Override
            public void run() {
                try {
                    newVersion = updateCheck(currentVersion);
                    if (newVersion > currentVersion) {
                        log.warning("Vault " + newVersion + " is out! You are running: Vault " + currentVersion);
                        log.warning("Update Vault at: http://dev.bukkit.org/server-mods/vault");
                    }
                } catch (Exception e) {
                    // ignore exceptions
                }
            }

        }, 0, 432000);

        // Load up the Plugin metrics
        try {
            metrics = new Metrics(this);
            metrics.findCustomData();
            metrics.start();
        } catch (IOException e) {
            // ignore exception
        }
        log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
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

        // Try to load Privileges
        hookChat("Privileges", Chat_Privileges.class, ServicePriority.Normal, "net.krinsoft.privileges.Privileges");
    }

    /**
     * Attempts to load Economy Addons
     */
    private void loadEconomy() {
        // Try to load MiConomy
        hookEconomy("MiConomy", Economy_MiConomy.class, ServicePriority.Normal, "com.gmail.bleedobsidian.miconomy.Main");
             
        // Try to load MiFaConomy
        hookEconomy("MineFaConomy", Economy_Minefaconomy.class, ServicePriority.Normal, "me.coniin.plugins.minefaconomy.Minefaconomy"); 
       
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
        hookEconomy("CraftConomy3", Economy_Craftconomy3.class, ServicePriority.Normal, "com.greatmancode.craftconomy3.tools.interfaces.BukkitLoader");

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
        hookPermission("zPermissions", Permission_zPermissions.class, ServicePriority.High, "org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsPlugin");

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

        //Try to load TotalPermissions
        hookPermission("TotalPermissions", Permission_TotalPermissions.class, ServicePriority.Normal, "net.ae97.totalpermissions.TotalPermissions");

        Permission perms = new Permission_SuperPerms(this);
        sm.register(Permission.class, perms, this, ServicePriority.Lowest);
        log.info(String.format("[%s][Permission] SuperPermissions loaded as backup permission system.", getDescription().getName()));

        this.perms = sm.getRegistration(Permission.class).getProvider();
    }

    private void hookChat (String name, Class<? extends Chat> hookClass, ServicePriority priority, String...packages) {
        try {
            if (packagesExists(packages)) {
                Chat chat = hookClass.getConstructor(Plugin.class, Permission.class).newInstance(this, perms);
                sm.register(Chat.class, chat, this, priority);
                log.info(String.format("[%s][Chat] %s found: %s", getDescription().getName(), name, chat.isEnabled() ? "Loaded" : "Waiting"));
            }
        } catch (Exception e) {
            log.severe(String.format("[%s][Chat] There was an error hooking %s - check to make sure you're using a compatible version!", getDescription().getName(), name));
        }
    }

    private void hookEconomy (String name, Class<? extends Economy> hookClass, ServicePriority priority, String...packages) {
        try {
            if (packagesExists(packages)) {
                Economy econ = hookClass.getConstructor(Plugin.class).newInstance(this);
                sm.register(Economy.class, econ, this, priority);
                log.info(String.format("[%s][Economy] %s found: %s", getDescription().getName(), name, econ.isEnabled() ? "Loaded" : "Waiting"));
            }
        } catch (Exception e) {
            log.severe(String.format("[%s][Economy] There was an error hooking %s - check to make sure you're using a compatible version!", getDescription().getName(), name));
        }
    }

    private void hookPermission (String name, Class<? extends Permission> hookClass, ServicePriority priority, String...packages) {
        try {
            if (packagesExists(packages)) {
                Permission perms = hookClass.getConstructor(Plugin.class).newInstance(this);
                sm.register(Permission.class, perms, this, priority);
                log.info(String.format("[%s][Permission] %s found: %s", getDescription().getName(), name, perms.isEnabled() ? "Loaded" : "Waiting"));
            }
        } catch (Exception e) {
            log.severe(String.format("[%s][Permission] There was an error hooking %s - check to make sure you're using a compatible version!", getDescription().getName(), name));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            // Check if Player
            // If so, ignore command if player is not Op
            Player p = (Player) sender;
            if (!p.isOp()) {
                return true;
            }
        }

        if (command.getName().equalsIgnoreCase("vault-info")) {
            infoCommand(sender);
            return true;
        } else if (command.getName().equalsIgnoreCase("vault-convert")) {
            convertCommand(sender, args);
            return true;
        } else {
            // Show help
            sender.sendMessage("Vault Commands:");
            sender.sendMessage("  /vault-info - Displays information about Vault");
            sender.sendMessage("  /vault-convert [economy1] [economy2] - Converts from one Economy to another");
            return true;
        }
    }

    private void convertCommand(CommandSender sender, String[] args) {
        Collection<RegisteredServiceProvider<Economy>> econs = this.getServer().getServicesManager().getRegistrations(Economy.class);
        if (econs == null || econs.size() < 2) {
            sender.sendMessage("You must have at least 2 economies loaded to convert.");
            return;
        } else if (args.length != 2) {
            sender.sendMessage("You must specify only the economy to convert from and the economy to convert to. (names should not contain spaces)");
            return;
        }
        Economy econ1 = null;
        Economy econ2 = null;
        for (RegisteredServiceProvider<Economy> econ : econs) {
            String econName = econ.getProvider().getName().replace(" ", "");
            if (econName.equalsIgnoreCase(args[0])) {
                econ1 = econ.getProvider();
            } else if (econName.equalsIgnoreCase(args[1])) {
                econ2 = econ.getProvider();
            }
        }

        if (econ1 == null) {
            sender.sendMessage("Could not find " + args[0] + " loaded on the server, check your spelling");
            return;
        } else if (econ2 == null) {
            sender.sendMessage("Could not find " + args[1] + " loaded on the server, check your spelling");
            return;
        }

        sender.sendMessage("This may take some time to convert, expect server lag.");
        for (OfflinePlayer op : Bukkit.getServer().getOfflinePlayers()) {
            String pName = op.getName();
            if (econ1.hasAccount(pName)) {
                if (econ2.hasAccount(pName)) {
                    continue;
                }
                econ2.createPlayerAccount(pName);
                econ2.depositPlayer(pName, econ1.getBalance(pName));
            }
        }
        sender.sendMessage("Converson complete, please verify the data before using it.");
    }

    private void infoCommand(CommandSender sender) {
        // Get String of Registered Economy Services
        String registeredEcons = null;
        Collection<RegisteredServiceProvider<Economy>> econs = this.getServer().getServicesManager().getRegistrations(Economy.class);
        for (RegisteredServiceProvider<Economy> econ : econs) {
            Economy e = econ.getProvider();
            if (registeredEcons == null) {
                registeredEcons = e.getName();
            } else {
                registeredEcons += ", " + e.getName();
            }
        }

        // Get String of Registered Permission Services
        String registeredPerms = null;
        Collection<RegisteredServiceProvider<Permission>> perms = this.getServer().getServicesManager().getRegistrations(Permission.class);
        for (RegisteredServiceProvider<Permission> perm : perms) {
            Permission p = perm.getProvider();
            if (registeredPerms == null) {
                registeredPerms = p.getName();
            } else {
                registeredPerms += ", " + p.getName();
            }
        }

        String registeredChats = null;
        Collection<RegisteredServiceProvider<Chat>> chats = this.getServer().getServicesManager().getRegistrations(Chat.class);
        for (RegisteredServiceProvider<Chat> chat : chats) {
            Chat c = chat.getProvider();
            if (registeredChats == null) {
                registeredChats = c.getName();
            } else {
                registeredChats += ", " + c.getName();
            }
        }

        // Get Economy & Permission primary Services
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = null;
        if (rsp != null) {
            econ = rsp.getProvider();
        }
        Permission perm = null;
        RegisteredServiceProvider<Permission> rspp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rspp != null) {
            perm = rspp.getProvider();
        }
        Chat chat = null;
        RegisteredServiceProvider<Chat> rspc = getServer().getServicesManager().getRegistration(Chat.class);
        if (rspc != null) {
            chat = rspc.getProvider();
        }
        // Send user some info!
        sender.sendMessage(String.format("[%s] Vault v%s Information", getDescription().getName(), getDescription().getVersion()));
        sender.sendMessage(String.format("[%s] Economy: %s [%s]", getDescription().getName(), econ == null ? "None" : econ.getName(), registeredEcons));
        sender.sendMessage(String.format("[%s] Permission: %s [%s]", getDescription().getName(), perm == null ? "None" : perm.getName(), registeredPerms));
        sender.sendMessage(String.format("[%s] Chat: %s [%s]", getDescription().getName(), chat == null ? "None" : chat.getName(), registeredChats));
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

    public double updateCheck(double currentVersion) throws Exception {
        String pluginUrlString = "http://dev.bukkit.org/server-mods/vault/files.rss";
        try {
            URL url = new URL(pluginUrlString);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("item");
            Node firstNode = nodes.item(0);
            if (firstNode.getNodeType() == 1) {
                Element firstElement = (Element)firstNode;
                NodeList firstElementTagName = firstElement.getElementsByTagName("title");
                Element firstNameElement = (Element) firstElementTagName.item(0);
                NodeList firstNodes = firstNameElement.getChildNodes();
                return Double.valueOf(firstNodes.item(0).getNodeValue().replace("Vault", "").replaceFirst(".", "").trim());
            }
        }
        catch (Exception localException) {
        }
        return currentVersion;
    }

    public class VaultListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            if (perms.has(player, "vault.admin")) {
                try {
                    if (newVersion > currentVersion) {
                        player.sendMessage(newVersion + " is out! You are running " + currentVersion);
                        player.sendMessage("Update Vault at: http://dev.bukkit.org/server-mods/vault");
                    }
                } catch (Exception e) {
                    // Ignore exceptions
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (event.getPlugin().getDescription().getName().equals("Register") && packagesExists("com.nijikokun.register.payment.Methods")) {
                if (!Methods.hasMethod()) {
                    try {
                        Method m = Methods.class.getMethod("addMethod", Methods.class);
                        m.setAccessible(true);
                        m.invoke(null, "Vault", new net.milkbowl.vault.VaultEco());
                        if (!Methods.setPreferred("Vault")) {
                            log.info("Unable to hook register");
                        } else {
                            log.info("[Vault] - Successfully injected Vault methods into Register.");
                        }
                    } catch (SecurityException e) {
                        log.info("Unable to hook register");
                    } catch (NoSuchMethodException e) {
                        log.info("Unable to hook register");
                    } catch (IllegalArgumentException e) {
                        log.info("Unable to hook register");
                    } catch (IllegalAccessException e) {
                        log.info("Unable to hook register");
                    } catch (InvocationTargetException e) {
                        log.info("Unable to hook register");
                    }
                }
            }
        }
    }
}
