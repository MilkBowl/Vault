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
import net.milkbowl.vault.chat.plugins.Chat_GroupManager;
import net.milkbowl.vault.chat.plugins.Chat_Permissions3;
import net.milkbowl.vault.chat.plugins.Chat_PermissionsEx;
import net.milkbowl.vault.chat.plugins.Chat_Towny;
import net.milkbowl.vault.chat.plugins.Chat_bPermissions;
import net.milkbowl.vault.chat.plugins.Chat_bPermissions2;
import net.milkbowl.vault.chat.plugins.Chat_iChat;
import net.milkbowl.vault.chat.plugins.Chat_mChat;
import net.milkbowl.vault.chat.plugins.Chat_mChatSuite;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.plugins.Economy_3co;
import net.milkbowl.vault.economy.plugins.Economy_AEco;
import net.milkbowl.vault.economy.plugins.Economy_BOSE6;
import net.milkbowl.vault.economy.plugins.Economy_BOSE7;
import net.milkbowl.vault.economy.plugins.Economy_Craftconomy;
import net.milkbowl.vault.economy.plugins.Economy_CurrencyCore;
import net.milkbowl.vault.economy.plugins.Economy_EconXP;
import net.milkbowl.vault.economy.plugins.Economy_Essentials;
import net.milkbowl.vault.economy.plugins.Economy_McMoney;
import net.milkbowl.vault.economy.plugins.Economy_MineConomy;
import net.milkbowl.vault.economy.plugins.Economy_MultiCurrency;
import net.milkbowl.vault.economy.plugins.Economy_eWallet;
import net.milkbowl.vault.economy.plugins.Economy_iConomy4;
import net.milkbowl.vault.economy.plugins.Economy_iConomy5;
import net.milkbowl.vault.economy.plugins.Economy_iConomy6;
import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.permission.plugins.Permission_GroupManager;
import net.milkbowl.vault.permission.plugins.Permission_Permissions3;
import net.milkbowl.vault.permission.plugins.Permission_PermissionsBukkit;
import net.milkbowl.vault.permission.plugins.Permission_PermissionsEx;
import net.milkbowl.vault.permission.plugins.Permission_Starburst;
import net.milkbowl.vault.permission.plugins.Permission_SuperPerms;
import net.milkbowl.vault.permission.plugins.Permission_bPermissions;
import net.milkbowl.vault.permission.plugins.Permission_bPermissions2;
import net.milkbowl.vault.permission.plugins.Permission_zPermissions;

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
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nijikokun.register.payment.Methods;

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

        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
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
        this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {

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
            String authors = "";
            for (String author : this.getDescription().getAuthors()) {
                authors += author + ", ";
            }
            if (!authors.isEmpty()) {
                authors = authors.substring(0, authors.length() - 2);
            }
            metrics = new Metrics(getDescription().getVersion(), authors);
            metrics.findCustomData(this);
            metrics.beginMeasuringPlugin(this);
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
        if (packageExists("ru.tehkode.permissions.bukkit.PermissionsEx")) {
            Chat eChat = new Chat_PermissionsEx(this, perms);
            sm.register(Chat.class, eChat, this, ServicePriority.Highest);
            log.info(String.format("[%s][Chat] PermissionsEx found: %s", getDescription().getName(), eChat.isEnabled() ? "Loaded" : "Waiting"));
        }

        //Try loading mChatSuite
        if (packageExists("in.mDev.MiracleM4n.mChatSuite.mChatSuite")) {
            Chat mChat = new Chat_mChatSuite(this, perms);
            sm.register(Chat.class, mChat, this, ServicePriority.Highest);
            log.info(String.format("[%s][Chat] mChatSuite found: %s", getDescription().getName(), mChat.isEnabled() ? "Loaded" : "Waiting"));
        }

        //Try loading mChat
        if (packageExists("net.D3GN.MiracleM4n.mChat")) {
            Chat mChat = new Chat_mChat(this, perms);
            sm.register(Chat.class, mChat, this, ServicePriority.Highest);
            log.info(String.format("[%s][Chat] mChat found: %s", getDescription().getName(), mChat.isEnabled() ? "Loaded" : "Waiting"));
        }

        //try loading bPermssions 2
        if (packageExists("de.bananaco.bpermissions.api.ApiLayer")) {
            Chat bPerms = new Chat_bPermissions2(this, perms);
            sm.register(Chat.class, bPerms, this, ServicePriority.High);
            log.info(String.format("[%s][Chat] bPermissions2 found: %s", getDescription().getName(), bPerms.isEnabled() ? "Loaded" : "Waiting"));
        } 

        // try loading bPermissions 1
        if (packageExists("de.bananaco.permissions.info.InfoReader")) {
            Chat bPerms = new Chat_bPermissions(this, perms);
            sm.register(Chat.class, bPerms, this, ServicePriority.Normal);
            log.info(String.format("[%s][Chat] bPermissions found: %s", getDescription().getName(), bPerms.isEnabled() ? "Loaded" : "Waiting"));
        }

        // Try to load GroupManager
        if (packageExists("org.anjocaido.groupmanager.GroupManager")) {
            Chat gPerms = new Chat_GroupManager(this, perms);
            sm.register(Chat.class, gPerms, this, ServicePriority.Normal);
            log.info(String.format("[%s][Chat] GroupManager found: %s", getDescription().getName(), gPerms.isEnabled() ? "Loaded" : "Waiting"));
        }

        // Try to load Permissions 3 (Yeti)
        if (packageExists("com.nijiko.permissions.ModularControl")) {
            Chat nPerms = new Chat_Permissions3(this, perms);
            sm.register(Chat.class, nPerms, this, ServicePriority.Normal);
            log.info(String.format("[%s][Chat] Permissions 3 (Yeti) found: %s", getDescription().getName(), nPerms.isEnabled() ? "Loaded" : "Waiting"));
        }

        // Try to load iChat
        if (packageExists("net.TheDgtl.iChat.iChat")) {
            Chat iChat = new Chat_iChat(this, perms);
            sm.register(Chat.class, iChat, this, ServicePriority.Low);
            log.info(String.format("[%s][Chat] iChat found: %s", getDescription().getName(), iChat.isEnabled() ? "Loaded" : "Waiting"));
        }

        //Try to load Towny Chat
        if (packageExists("com.palmergames.bukkit.towny.Towny")) {
            Chat townChat = new Chat_Towny(this, perms);
            sm.register(Chat.class, townChat, this, ServicePriority.Lowest);
            log.info(String.format("[%s][Chat] Towny found: %s", getDescription().getName(), townChat.isEnabled() ? "Loaded" : "Waiting"));
        }
    }

    /**
     * Attempts to load Economy Addons
     */
    private void loadEconomy() {
        // Try to load MultiCurrency
        if (packageExists("me.ashtheking.currency.Currency", "me.ashtheking.currency.CurrencyList")) {
            Economy econ = new Economy_MultiCurrency(this);
            sm.register(Economy.class, econ, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] MultiCurrency found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
        }

        //Try Loading MineConomy
        if (packageExists("me.mjolnir.mineconomy.MineConomy")) {
           // Economy econ = new Economy_MineConomy(this);
            //sm.register(Economy.class, econ, this, ServicePriority.Normal);
            //log.info(String.format("[%s][Economy] MineConomy found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
            log.info(String.format("[%s][Economy] MineConomy found but is no longer supported!", getDescription().getName()));
        }
        
        //Try Loading AEco
        if (packageExists("org.neocraft.AEco.AEco")) {
            Economy econ = new Economy_AEco(this);
            sm.register(Economy.class, econ, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] AEco found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
        }
        
        //Try Loading McMoney
        if (packageExists("boardinggamer.mcmoney.McMoneyAPI")) {
            Economy econ = new Economy_McMoney(this);
            sm.register(Economy.class, econ, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] McMoney found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
        }

        //Try Loading Craftconomy
        if (packageExists("me.greatman.Craftconomy.Craftconomy")) {
            Economy econ = new Economy_Craftconomy(this);
            sm.register(Economy.class, econ, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] CraftConomy found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
        }

        //Try loading eWallet
        if (packageExists("me.ethan.eWallet.ECO")) {
            Economy econ = new Economy_eWallet(this);
            sm.register(Economy.class, econ, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] eWallet found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
        }

        // Try to load 3co
        if (packageExists("me.ic3d.eco.ECO")) {
            Economy econ = new Economy_3co(this);
            sm.register(Economy.class, econ, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] 3co found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
        }

        // Try to load BOSEconomy
        if (packageExists("cosine.boseconomy.BOSEconomy", "cosine.boseconomy.CommandManager")) {
            Economy bose6 = new Economy_BOSE6(this);
            sm.register(Economy.class, bose6, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] BOSEconomy6 found: %s", getDescription().getName(), bose6.isEnabled() ? "Loaded" : "Waiting"));
        }

        // Try to load BOSEconomy
        if (packageExists("cosine.boseconomy.BOSEconomy", "cosine.boseconomy.CommandHandler")) {
            Economy bose7 = new Economy_BOSE7(this);
            sm.register(net.milkbowl.vault.economy.Economy.class, bose7, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] BOSEconomy7 found: %s", getDescription().getName(), bose7.isEnabled() ? "Loaded" : "Waiting"));
        }

        //Try to load CurrencyCore
        if (packageExists("is.currency.Currency")) {
            Economy cCore = new Economy_CurrencyCore(this);
            sm.register(net.milkbowl.vault.economy.Economy.class, cCore, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] CurrencyCore found: %s", getDescription().getName(), cCore.isEnabled() ? "Loaded" : "Waiting"));
        }

        // Try to load Essentials Economy
        if (packageExists("com.earth2me.essentials.api.Economy", "com.earth2me.essentials.api.NoLoanPermittedException", "com.earth2me.essentials.api.UserDoesNotExistException")) {
            Economy essentials = new Economy_Essentials(this);
            sm.register(net.milkbowl.vault.economy.Economy.class, essentials, this, ServicePriority.Low);
            log.info(String.format("[%s][Economy] Essentials Economy found: %s", getDescription().getName(), essentials.isEnabled() ? "Loaded" : "Waiting"));
        }

        // Try to load iConomy 4
        if (packageExists("com.nijiko.coelho.iConomy.iConomy", "com.nijiko.coelho.iConomy.system.Account")) {
            Economy icon4 = new Economy_iConomy4(this);
            sm.register(net.milkbowl.vault.economy.Economy.class, icon4, this, ServicePriority.High);
            log.info(String.format("[%s][Economy] iConomy 4 found: ", getDescription().getName(), icon4.isEnabled() ? "Loaded" : "Waiting"));
        }

        // Try to load iConomy 5
        if (packageExists("com.iConomy.iConomy", "com.iConomy.system.Account", "com.iConomy.system.Holdings")) {
            Economy icon5 = new Economy_iConomy5(this);
            sm.register(net.milkbowl.vault.economy.Economy.class, icon5, this, ServicePriority.High);
            log.info(String.format("[%s][Economy] iConomy 5 found: %s", getDescription().getName(), icon5.isEnabled() ? "Loaded" : "Waiting"));
        }

        // Try to load iConomy 6
        if (packageExists("com.iCo6.iConomy")) {
            Economy icon6 = new Economy_iConomy6(this);
            sm.register(Economy.class, icon6, this, ServicePriority.High);
            log.info(String.format("[%s][Economy] iConomy 6 found: %s", getDescription().getName(), icon6.isEnabled() ? "Loaded" : "Waiting"));
        }

        //Try loading EconXP
        if (packageExists("ca.agnate.EconXP.EconXP")) {
            Economy econ = new Economy_EconXP(this);
            sm.register(Economy.class, econ, this, ServicePriority.Normal);
            log.info(String.format("[%s][Economy] EconXP found: %s", getDescription().getName(), econ.isEnabled() ? "Loaded" : "Waiting"));
        }
    }

    /**
     * Attempts to load Permission Addons
     */
    private void loadPermission() {
        //Try to load Starburst
        if (packageExists("com.dthielke.starburst.StarburstPlugin")) {
            Permission sPerms = new Permission_Starburst(this);
            sm.register(Permission.class, sPerms, this, ServicePriority.Highest);
            log.info(String.format("[%s][Permission] Starburst found: %s", getDescription().getName(), sPerms.isEnabled() ? "Loaded" : "Waiting"));
        }

        // Try to load PermissionsEx
        if (packageExists("ru.tehkode.permissions.bukkit.PermissionsEx")) {
            Permission ePerms = new Permission_PermissionsEx(this);
            sm.register(Permission.class, ePerms, this, ServicePriority.Highest);
            log.info(String.format("[%s][Permission] PermissionsEx found: %s", getDescription().getName(), ePerms.isEnabled() ? "Loaded" : "Waiting"));
        }

        //Try loading PermissionsBukkit
        if (packageExists("com.platymuus.bukkit.permissions.PermissionsPlugin")) {
            Permission pPerms = new Permission_PermissionsBukkit(this);
            sm.register(Permission.class, pPerms, this, ServicePriority.Highest);
            log.info(String.format("[%s][Permission] PermissionsBukkit found: %s", getDescription().getName(), pPerms.isEnabled() ? "Loaded" : "Waiting"));
        }


        //try loading bPermissions2
        if (packageExists("de.bananaco.bpermissions.api.WorldManager")) {
            Permission bPerms = new Permission_bPermissions2(this);
            sm.register(Permission.class, bPerms, this, ServicePriority.Highest);
            log.info(String.format("[%s][Chat] bPermissions found: %s", getDescription().getName(), bPerms.isEnabled() ? "Loaded" : "Waiting"));
        }

        //Try to load zPermission
        if (packageExists("org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsPlugin")) {
            Permission zPerms = new Permission_zPermissions(this);
            sm.register(Permission.class, zPerms, this, ServicePriority.Highest);
            log.info(String.format("[%s][Permission] zPermissions found: %s", getDescription().getName(), zPerms.isEnabled() ? "Loaded" : "Waiting"));
        }

        //Try to load bPermissions
        if (packageExists("de.bananaco.permissions.SuperPermissionHandler")) {
            Permission bPerms = new Permission_bPermissions(this);
            sm.register(Permission.class, bPerms, this, ServicePriority.High);
            log.info(String.format("[%s][Permission] bPermissions found: %s", getDescription().getName(), bPerms.isEnabled() ? "Loaded" : "Waiting"));
        }

        // Try to load GroupManager
        if (packageExists("org.anjocaido.groupmanager.GroupManager")) {
            Permission gPerms = new Permission_GroupManager(this);
            sm.register(Permission.class, gPerms, this, ServicePriority.High);
            log.info(String.format("[%s][Permission] GroupManager found: %s", getDescription().getName(), gPerms.isEnabled() ? "Loaded" : "Waiting"));
        }
        // Try to load Permissions 3 (Yeti)
        if (packageExists("com.nijiko.permissions.ModularControl")) {
            Permission nPerms = new Permission_Permissions3(this);
            sm.register(Permission.class, nPerms, this, ServicePriority.High);
            log.info(String.format("[%s][Permission] Permissions 3 (Yeti) found: %s", getDescription().getName(), nPerms.isEnabled() ? "Loaded" : "Waiting"));
        }

        Permission perms = new Permission_SuperPerms(this);
        sm.register(Permission.class, perms, this, ServicePriority.Lowest);
        log.info(String.format("[%s][Permission] SuperPermissions loaded as backup permission system.", getDescription().getName()));

        this.perms = sm.getRegistration(Permission.class).getProvider();
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
            sender.sendMessage("You must specify only the economy to convert from and the economy to convert to. (without spaces)");
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

        // Get Economy & Permission primary Services
        Economy econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        Permission perm = getServer().getServicesManager().getRegistration(Permission.class).getProvider();

        // Send user some info!
        sender.sendMessage(String.format("[%s] Vault v%s Information", getDescription().getName(), getDescription().getVersion()));
        sender.sendMessage(String.format("[%s] Economy: %s [%s]", getDescription().getName(), econ.getName(), registeredEcons));
        sender.sendMessage(String.format("[%s] Permission: %s [%s]", getDescription().getName(), perm.getName(), registeredPerms));
    }

    /**
     * Determines if all packages in a String array are within the Classpath
     * This is the best way to determine if a specific plugin exists and will be
     * loaded. If the plugin package isn't loaded, we shouldn't bother waiting
     * for it!
     * @param packages String Array of package names to check
     * @return Success or Failure
     */
    private static boolean packageExists(String...packages) {
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
            if (event.getPlugin().getDescription().getName().equals("Register") && packageExists("com.nijikokun.register.payment.Methods")) {
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