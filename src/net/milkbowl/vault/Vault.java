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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.chat.plugins.Chat_DroxPerms;
import net.milkbowl.vault.chat.plugins.Chat_GroupManager;
import net.milkbowl.vault.chat.plugins.Chat_OverPermissions;
import net.milkbowl.vault.chat.plugins.Chat_Permissions3;
import net.milkbowl.vault.chat.plugins.Chat_PermissionsEx;
import net.milkbowl.vault.chat.plugins.Chat_Privileges;
import net.milkbowl.vault.chat.plugins.Chat_bPermissions;
import net.milkbowl.vault.chat.plugins.Chat_bPermissions2;
import net.milkbowl.vault.chat.plugins.Chat_iChat;
import net.milkbowl.vault.chat.plugins.Chat_mChat;
import net.milkbowl.vault.chat.plugins.Chat_mChatSuite;
import net.milkbowl.vault.chat.plugins.Chat_rscPermissions;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.permission.plugins.Permission_DroxPerms;
import net.milkbowl.vault.permission.plugins.Permission_GroupManager;
import net.milkbowl.vault.permission.plugins.Permission_OverPermissions;
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
import net.milkbowl.vault.permission.plugins.Permission_TotalPermissions;
import net.milkbowl.vault.permission.plugins.Permission_rscPermissions;
import net.milkbowl.vault.permission.plugins.Permission_KPerms;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import net.milkbowl.vault.chat.plugins.Chat_TotalPermissions;

public class Vault extends JavaPlugin {

    private static final String VAULT_BUKKIT_URL = "https://dev.bukkit.org/projects/Vault";
    private static Logger log;
    private Permission perms;
    private String newVersionTitle = "";
    private double newVersion;
    private double currentVersion;
    private String currentVersionTitle = "";
    private ServicesManager sm;
    private Vault plugin;

    @Override
    public void onDisable() {
        // Remove all Service Registrations
        getServer().getServicesManager().unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getLogger();
        currentVersionTitle = getDescription().getVersion().split("-")[0];
        currentVersion = Double.parseDouble(currentVersionTitle.replaceFirst("\\.", ""));
        sm = getServer().getServicesManager();
        // set defaults
        getConfig().addDefault("update-check", true);
        getConfig().options().copyDefaults(true);
        saveConfig();
        // Load Vault Addons
        loadPermission();
        loadChat();

        getCommand("vault-info").setExecutor(this);
        getCommand("vault-convert").setExecutor(this);
        getServer().getPluginManager().registerEvents(new VaultListener(), this);
        // Schedule to check the version every 30 minutes for an update. This is to update the most recent 
        // version so if an admin reconnects they will be warned about newer versions.
        this.getServer().getScheduler().runTask(this, () -> {
            // Programmatically set the default permission value cause Bukkit doesn't handle plugin.yml properly for Load order STARTUP plugins
            org.bukkit.permissions.Permission perm = getServer().getPluginManager().getPermission("vault.update");
            if (perm == null) {
                perm = new org.bukkit.permissions.Permission("vault.update");
                perm.setDefault(PermissionDefault.OP);
                plugin.getServer().getPluginManager().addPermission(perm);
            }
            perm.setDescription("Allows a user or the console to check for vault updates");

            getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                if (getServer().getConsoleSender().hasPermission("vault.update") && getConfig().getBoolean("update-check", true)) {
                    log.info("Checking for Updates ... ");
                    newVersion = updateCheck(currentVersion);
                    if (newVersion > currentVersion) {
                        log.warning("Stable Version: " + newVersionTitle + " is out!" + " You are still running version: " + currentVersionTitle);
                        log.warning("Update at: https://dev.bukkit.org/projects/vault");
                    } else if (currentVersion > newVersion) {
                        log.info("Stable Version: " + newVersionTitle + " | Current Version: " + currentVersionTitle);
                    }
                }
            }, 0, 432000);

        });

        // Load up the Plugin metrics
        Metrics metrics = new Metrics(this, 887);
        findCustomData(metrics);

        log.info(String.format("Enabled Version %s", getDescription().getVersion()));
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

        // Try to load OverPermissions
        hookChat("OverPermissions", Chat_OverPermissions.class, ServicePriority.Highest, "com.overmc.overpermissions.internal.OverPermissions");

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

        // Try to load Privileges
        hookChat("Privileges", Chat_Privileges.class, ServicePriority.Normal, "net.krinsoft.privileges.Privileges");

        // Try to load rscPermissions
        hookChat("rscPermissions", Chat_rscPermissions.class, ServicePriority.Normal, "ru.simsonic.rscPermissions.MainPluginClass");

        //Try to load TotalPermissions
        hookChat("TotalPermissions", Chat_TotalPermissions.class, ServicePriority.Normal, "net.ar97.totalpermissions.TotalPermissions");
    }

    /**
     * Attempts to load Permission Addons
     */
    private void loadPermission() {
        // Try to load Starburst
        hookPermission("Starburst", Permission_Starburst.class, ServicePriority.Highest, "com.dthielke.starburst.StarburstPlugin");

        // Try to load PermissionsEx
        hookPermission("PermissionsEx", Permission_PermissionsEx.class, ServicePriority.Highest, "ru.tehkode.permissions.bukkit.PermissionsEx");

        // Try to load OverPermissions
        hookPermission("OverPermissions", Permission_OverPermissions.class, ServicePriority.Highest, "com.overmc.overpermissions.internal.OverPermissions");

        // Try to load PermissionsBukkit
        hookPermission("PermissionsBukkit", Permission_PermissionsBukkit.class, ServicePriority.Normal, "com.platymuus.bukkit.permissions.PermissionsPlugin");

        // Try to load DroxPerms
        hookPermission("DroxPerms", Permission_DroxPerms.class, ServicePriority.High, "de.hydrox.bukkit.DroxPerms.DroxPerms");

        // Try to load SimplyPerms
        hookPermission("SimplyPerms", Permission_SimplyPerms.class, ServicePriority.Highest, "net.crystalyx.bukkit.simplyperms.SimplyPlugin");

        // Try to load bPermissions2
        hookPermission("bPermissions 2", Permission_bPermissions2.class, ServicePriority.Highest, "de.bananaco.bpermissions.api.WorldManager");

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

        // Try to load rscPermissions
        hookPermission("rscPermissions", Permission_rscPermissions.class, ServicePriority.Normal, "ru.simsonic.rscPermissions.MainPluginClass");

        // Try to load KPerms
        hookPermission("KPerms", Permission_KPerms.class, ServicePriority.Normal, "com.lightniinja.kperms.KPermsPlugin");

        Permission perms = new Permission_SuperPerms(this);
        sm.register(Permission.class, perms, this, ServicePriority.Lowest);
        log.info("[Permission] SuperPermissions loaded as backup permission system.");

        this.perms = sm.getRegistration(Permission.class).getProvider();
    }

    private void hookChat (String name, Class<? extends Chat> hookClass, ServicePriority priority, String...packages) {
        try {
            if (packagesExists(packages)) {
                Chat chat = hookClass.getConstructor(Plugin.class, Permission.class).newInstance(this, perms);
                sm.register(Chat.class, chat, this, priority);
                log.info(String.format("[Chat] %s found: %s", name, chat.isEnabled() ? "Loaded" : "Waiting"));
            }
        } catch (Exception e) {
            log.severe(String.format("[Chat] There was an error hooking %s - check to make sure you're using a compatible version!", name));
        }
    }

    private void hookPermission (String name, Class<? extends Permission> hookClass, ServicePriority priority, String...packages) {
        try {
            if (packagesExists(packages)) {
                Permission perms = hookClass.getConstructor(Plugin.class).newInstance(this);
                sm.register(Permission.class, perms, this, priority);
                log.info(String.format("[Permission] %s found: %s", name, perms.isEnabled() ? "Loaded" : "Waiting"));
            }
        } catch (Exception e) {
            log.severe(String.format("[Permission] There was an error hooking %s - check to make sure you're using a compatible version!", name));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!sender.hasPermission("vault.admin")) {
            sender.sendMessage("You do not have permission to use that command!");
            return true;
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
        StringBuilder economies = new StringBuilder();
        for (RegisteredServiceProvider<Economy> econ : econs) {
            String econName = econ.getProvider().getName().replace(" ", "");
            if (econName.equalsIgnoreCase(args[0])) {
                econ1 = econ.getProvider();
            } else if (econName.equalsIgnoreCase(args[1])) {
                econ2 = econ.getProvider();
            }
            if (economies.length() > 0) {
            	economies.append(", ");
            }
            economies.append(econName);
        }

        if (econ1 == null) {
            sender.sendMessage("Could not find " + args[0] + " loaded on the server, check your spelling.");
            sender.sendMessage("Valid economies are: " + economies);
            return;
        } else if (econ2 == null) {
            sender.sendMessage("Could not find " + args[1] + " loaded on the server, check your spelling.");
            sender.sendMessage("Valid economies are: " + economies);
            return;
        }

        sender.sendMessage("This may take some time to convert, expect server lag.");
        for (OfflinePlayer op : Bukkit.getServer().getOfflinePlayers()) {
            if (econ1.hasAccount(op)) {
                if (econ2.hasAccount(op)) {
                    continue;
                }
                econ2.createPlayerAccount(op);
                double diff = econ1.getBalance(op) - econ2.getBalance(op);
                if (diff > 0) {
                	econ2.depositPlayer(op, diff);
                } else if (diff < 0) {
                	econ2.withdrawPlayer(op, -diff);
                }
                
            }
        }
        sender.sendMessage("Converson complete, please verify the data before using it.");
    }

    private void infoCommand(CommandSender sender) {
        // Get String of Registered Economy Services
        StringBuilder registeredEcons = null;
        Collection<RegisteredServiceProvider<Economy>> econs = this.getServer().getServicesManager().getRegistrations(Economy.class);
        for (RegisteredServiceProvider<Economy> econ : econs) {
            Economy e = econ.getProvider();
            if (registeredEcons == null) {
                registeredEcons = new StringBuilder(e.getName());
            } else {
                registeredEcons.append(", ").append(e.getName());
            }
        }

        // Get String of Registered Permission Services
        StringBuilder registeredPerms = null;
        Collection<RegisteredServiceProvider<Permission>> perms = this.getServer().getServicesManager().getRegistrations(Permission.class);
        for (RegisteredServiceProvider<Permission> perm : perms) {
            Permission p = perm.getProvider();
            if (registeredPerms == null) {
                registeredPerms = new StringBuilder(p.getName());
            } else {
                registeredPerms.append(", ").append(p.getName());
            }
        }

        StringBuilder registeredChats = null;
        Collection<RegisteredServiceProvider<Chat>> chats = this.getServer().getServicesManager().getRegistrations(Chat.class);
        for (RegisteredServiceProvider<Chat> chat : chats) {
            Chat c = chat.getProvider();
            if (registeredChats == null) {
                registeredChats = new StringBuilder(c.getName());
            } else {
                registeredChats.append(", ").append(c.getName());
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
        sender.sendMessage(String.format("[%s] Economy: %s %s", getDescription().getName(), econ == null ? "None" : econ.getName(), registeredEcons == null ? "" : "[" + registeredEcons + "]"));
        sender.sendMessage(String.format("[%s] Permission: %s %s", getDescription().getName(), perm == null ? "None" : perm.getName(), registeredPerms == null ? "" : "[" + registeredPerms + "]"));
        sender.sendMessage(String.format("[%s] Chat: %s %s", getDescription().getName(), chat == null ? "None" : chat.getName(), registeredChats == null ? "" : "[" + registeredChats + "]"));
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

    public double updateCheck(double currentVersion) {
        try {
            URL url = new URL("https://api.curseforge.com/servermods/files?projectids=33184");
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(5000);
            conn.addRequestProperty("User-Agent", "Vault Update Checker");
            conn.setDoOutput(true);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();
            final JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.size() == 0) {
                this.getLogger().warning("No files found, or Feed URL is bad.");
                return currentVersion;
            }
            // Pull the last version from the JSON
            newVersionTitle = ((String) ((JSONObject) array.get(array.size() - 1)).get("name")).replace("Vault", "").trim();
            return Double.parseDouble(newVersionTitle.replaceFirst("\\.", "").trim());
        } catch (Exception e) {
            log.info("There was an issue attempting to check for the latest version.");
        }
        return currentVersion;
    }

    private void findCustomData(Metrics metrics) {
        // Create our Economy Graph and Add our Economy plotters
        RegisteredServiceProvider<Economy> rspEcon = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = null;
        if (rspEcon != null) {
            econ = rspEcon.getProvider();
        }
        final String econName = econ != null ? econ.getName() : "No Economy";
        metrics.addCustomChart(new SimplePie("economy", () -> econName));

        // Create our Permission Graph and Add our permission Plotters
        final String permName = Bukkit.getServer().getServicesManager().getRegistration(Permission.class).getProvider().getName();
        metrics.addCustomChart(new SimplePie("permission", () -> permName));

        // Create our Chat Graph and Add our chat Plotters
        RegisteredServiceProvider<Chat> rspChat = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        Chat chat = null;
        if (rspChat != null) {
            chat = rspChat.getProvider();
        }
        final String chatName = chat != null ? chat.getName() : "No Chat";
        metrics.addCustomChart(new SimplePie("chat", () -> chatName));
    }

    public class VaultListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            if (perms.has(player, "vault.update")) {
                if (newVersion > currentVersion) {
                    player.sendMessage("Vault " +  newVersionTitle + " is out! You are running " + currentVersionTitle);
                    player.sendMessage("Update Vault at: " + VAULT_BUKKIT_URL);
                }
            }
        }

    }
}
