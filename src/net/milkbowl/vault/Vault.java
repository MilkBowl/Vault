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

import com.nijikokun.register.payment.Methods;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.chat.plugins.*;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.plugins.*;
import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.permission.plugins.*;
import org.bstats.bukkit.Metrics;
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
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.logging.Logger;

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
        this.getServer().getServicesManager().unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        this.plugin = this;
        Vault.log = getLogger();
        this.currentVersionTitle = this.getDescription().getVersion().split("-")[0];
        this.currentVersion = Double.parseDouble(this.currentVersionTitle.replaceFirst("\\.", ""));
        this.sm = this.getServer().getServicesManager();
        // set defaults
        this.getConfig().addDefault("update-check", true);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        // Load Vault Addons
        this.loadEconomy();
        this.loadPermission();
        this.loadChat();

        this.getCommand("vault-info").setExecutor(this);
        this.getCommand("vault-convert").setExecutor(this);
        this.getServer().getPluginManager().registerEvents(new VaultListener(), this);
        // Schedule to check the version every 30 minutes for an update. This is to update the most recent
        // version so if an admin reconnects they will be warned about newer versions.
        getServer().getScheduler().runTask(this, () -> {
            // Programmatically set the default permission value cause Bukkit doesn't handle plugin.yml properly for Load order STARTUP plugins
            org.bukkit.permissions.Permission perm = this.getServer().getPluginManager().getPermission("vault.update");
            if (perm == null) {
                perm = new org.bukkit.permissions.Permission("vault.update");
                perm.setDefault(PermissionDefault.OP);
                this.plugin.getServer().getPluginManager().addPermission(perm);
            }
            perm.setDescription("Allows a user or the console to check for vault updates");

            this.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
                if (this.getServer().getConsoleSender().hasPermission("vault.update") && this.getConfig().getBoolean("update-check", true)) {
                    try {
                        Vault.log.info("Checking for Updates ... ");
                        this.newVersion = this.updateCheck(this.currentVersion);
                        if (this.newVersion > this.currentVersion) {
                            Vault.log.warning("Stable Version: " + this.newVersionTitle + " is out!" + " You are still running version: " + this.currentVersionTitle);
                            Vault.log.warning("Update at: https://dev.bukkit.org/projects/vault");
                        } else if (this.currentVersion > this.newVersion) {
                            Vault.log.info("Stable Version: " + this.newVersionTitle + " | Current Version: " + this.currentVersionTitle);
                        } else {
                            Vault.log.info("No new version available");
                        }
                    } catch (final Exception e) {
                        // ignore exceptions
                    }
                }
            }, 0, 432000);

        });

        // Load up the Plugin metrics
        final Metrics metrics = new Metrics(this, 0); // TODO: Get the correct serviceId
        this.findCustomData(metrics);

        Vault.log.info(String.format("Enabled Version %s", this.getDescription().getVersion()));
    }

    /**
     * Attempts to load Chat Addons
     */
    private void loadChat() {
        // Try to load PermissionsEx
        this.hookChat("PermissionsEx", Chat_PermissionsEx.class, ServicePriority.Highest, "ru.tehkode.permissions.bukkit.PermissionsEx");

        // Try to load mChatSuite
        this.hookChat("mChatSuite", Chat_mChatSuite.class, ServicePriority.Highest, "in.mDev.MiracleM4n.mChatSuite.mChatSuite");

        // Try to load mChat
        this.hookChat("mChat", Chat_mChat.class, ServicePriority.Highest, "net.D3GN.MiracleM4n.mChat");

        // Try to load OverPermissions
        this.hookChat("OverPermissions", Chat_OverPermissions.class, ServicePriority.Highest, "com.overmc.overpermissions.internal.OverPermissions");

        // Try to load DroxPerms Chat
        this.hookChat("DroxPerms", Chat_DroxPerms.class, ServicePriority.Lowest, "de.hydrox.bukkit.DroxPerms.DroxPerms");

        // Try to load bPermssions 2
        this.hookChat("bPermssions2", Chat_bPermissions2.class, ServicePriority.Highest, "de.bananaco.bpermissions.api.ApiLayer");

        // Try to load bPermissions 1
        this.hookChat("bPermissions", Chat_bPermissions.class, ServicePriority.Normal, "de.bananaco.permissions.info.InfoReader");

        // Try to load GroupManager
        this.hookChat("GroupManager", Chat_GroupManager.class, ServicePriority.Normal, "org.anjocaido.groupmanager.GroupManager");

        // Try to load Permissions 3 (Yeti)
        this.hookChat("Permissions3", Chat_Permissions3.class, ServicePriority.Normal, "com.nijiko.permissions.ModularControl");

        // Try to load iChat
        this.hookChat("iChat", Chat_iChat.class, ServicePriority.Low, "net.TheDgtl.iChat.iChat");

        // Try to load Privileges
        this.hookChat("Privileges", Chat_Privileges.class, ServicePriority.Normal, "net.krinsoft.privileges.Privileges");

        // Try to load rscPermissions
        this.hookChat("rscPermissions", Chat_rscPermissions.class, ServicePriority.Normal, "ru.simsonic.rscPermissions.MainPluginClass");

        //Try to load TotalPermissions
        this.hookChat("TotalPermissions", Chat_TotalPermissions.class, ServicePriority.Normal, "net.ar97.totalpermissions.TotalPermissions");
    }

    /**
     * Attempts to load Economy Addons
     */
    private void loadEconomy() {
        // Try to load MiConomy
        this.hookEconomy("MiConomy", Economy_MiConomy.class, ServicePriority.Normal, "com.gmail.bleedobsidian.miconomy.Main");

        // Try to load MiFaConomy
        this.hookEconomy("MineFaConomy", Economy_Minefaconomy.class, ServicePriority.Normal, "me.coniin.plugins.minefaconomy.Minefaconomy");

        // Try to load MultiCurrency
        this.hookEconomy("MultiCurrency", Economy_MultiCurrency.class, ServicePriority.Normal, "me.ashtheking.currency.Currency", "me.ashtheking.currency.CurrencyList");

        // Try to load MineConomy
        this.hookEconomy("MineConomy", Economy_MineConomy.class, ServicePriority.Normal, "me.mjolnir.mineconomy.MineConomy");

        // Try to load McMoney
        this.hookEconomy("McMoney", Economy_McMoney.class, ServicePriority.Normal, "boardinggamer.mcmoney.McMoneyAPI");

        // Try to load Craftconomy3
        this.hookEconomy("CraftConomy3", Economy_Craftconomy3.class, ServicePriority.Normal, "com.greatmancode.craftconomy3.tools.interfaces.BukkitLoader");

        // Try to load eWallet
        this.hookEconomy("eWallet", Economy_eWallet.class, ServicePriority.Normal, "me.ethan.eWallet.ECO");

        // Try to load BOSEconomy 7
        this.hookEconomy("BOSEconomy7", Economy_BOSE7.class, ServicePriority.Normal, "cosine.boseconomy.BOSEconomy", "cosine.boseconomy.CommandHandler");

        // Try to load CurrencyCore
        this.hookEconomy("CurrencyCore", Economy_CurrencyCore.class, ServicePriority.Normal, "is.currency.Currency");

        // Try to load Gringotts
        this.hookEconomy("Gringotts", Economy_Gringotts.class, ServicePriority.Normal, "org.gestern.gringotts.Gringotts");

        // Try to load Essentials Economy
        this.hookEconomy("Essentials Economy", Economy_Essentials.class, ServicePriority.Low, "com.earth2me.essentials.api.Economy", "com.earth2me.essentials.api.NoLoanPermittedException", "com.earth2me.essentials.api.UserDoesNotExistException");

        // Try to load iConomy 6
        this.hookEconomy("iConomy 6", Economy_iConomy6.class, ServicePriority.High, "com.iCo6.iConomy");

        // Try to load EconXP
        this.hookEconomy("EconXP", Economy_EconXP.class, ServicePriority.Normal, "ca.agnate.EconXP.EconXP");

        // Try to load GoldIsMoney2
        this.hookEconomy("GoldIsMoney2", Economy_GoldIsMoney2.class, ServicePriority.Normal, "com.flobi.GoldIsMoney2.GoldIsMoney");

        // Try to load GoldenChestEconomy
        this.hookEconomy("GoldenChestEconomy", Economy_GoldenChestEconomy.class, ServicePriority.Normal, "me.igwb.GoldenChest.GoldenChestEconomy");

        // Try to load Dosh
        this.hookEconomy("Dosh", Economy_Dosh.class, ServicePriority.Normal, "com.gravypod.Dosh.Dosh");

        // Try to load CommandsEX Economy
        this.hookEconomy("CommandsEX", Economy_CommandsEX.class, ServicePriority.Normal, "com.github.zathrus_writer.commandsex.api.EconomyAPI");

        // Try to load SDFEconomy Economy
        this.hookEconomy("SDFEconomy", Economy_SDFEconomy.class, ServicePriority.Normal, "com.github.omwah.SDFEconomy.SDFEconomy");

        // Try to load XPBank
        this.hookEconomy("XPBank", Economy_XPBank.class, ServicePriority.Normal, "com.gmail.mirelatrue.xpbank.XPBank");

        // Try to load TAEcon
        this.hookEconomy("TAEcon", Economy_TAEcon.class, ServicePriority.Normal, "net.teamalpha.taecon.TAEcon");

        // Try to load DigiCoin
        this.hookEconomy("DigiCoin", Economy_DigiCoin.class, ServicePriority.Normal, "co.uk.silvania.cities.digicoin.DigiCoin");
    }

    /**
     * Attempts to load Permission Addons
     */
    private void loadPermission() {
        // Try to load Starburst
        this.hookPermission("Starburst", Permission_Starburst.class, ServicePriority.Highest, "com.dthielke.starburst.StarburstPlugin");

        // Try to load PermissionsEx
        this.hookPermission("PermissionsEx", Permission_PermissionsEx.class, ServicePriority.Highest, "ru.tehkode.permissions.bukkit.PermissionsEx");

        // Try to load OverPermissions
        this.hookPermission("OverPermissions", Permission_OverPermissions.class, ServicePriority.Highest, "com.overmc.overpermissions.internal.OverPermissions");

        // Try to load PermissionsBukkit
        this.hookPermission("PermissionsBukkit", Permission_PermissionsBukkit.class, ServicePriority.Normal, "com.platymuus.bukkit.permissions.PermissionsPlugin");

        // Try to load DroxPerms
        this.hookPermission("DroxPerms", Permission_DroxPerms.class, ServicePriority.High, "de.hydrox.bukkit.DroxPerms.DroxPerms");

        // Try to load SimplyPerms
        this.hookPermission("SimplyPerms", Permission_SimplyPerms.class, ServicePriority.Highest, "net.crystalyx.bukkit.simplyperms.SimplyPlugin");

        // Try to load bPermissions2
        this.hookPermission("bPermissions 2", Permission_bPermissions2.class, ServicePriority.Highest, "de.bananaco.bpermissions.api.WorldManager");

        // Try to load Privileges
        this.hookPermission("Privileges", Permission_Privileges.class, ServicePriority.Highest, "net.krinsoft.privileges.Privileges");

        // Try to load bPermissions
        this.hookPermission("bPermissions", Permission_bPermissions.class, ServicePriority.High, "de.bananaco.permissions.SuperPermissionHandler");

        // Try to load GroupManager
        this.hookPermission("GroupManager", Permission_GroupManager.class, ServicePriority.High, "org.anjocaido.groupmanager.GroupManager");

        // Try to load Permissions 3 (Yeti)
        this.hookPermission("Permissions 3 (Yeti)", Permission_Permissions3.class, ServicePriority.Normal, "com.nijiko.permissions.ModularControl");

        // Try to load Xperms
        this.hookPermission("Xperms", Permission_Xperms.class, ServicePriority.Low, "com.github.sebc722.Xperms");

        //Try to load TotalPermissions
        this.hookPermission("TotalPermissions", Permission_TotalPermissions.class, ServicePriority.Normal, "net.ae97.totalpermissions.TotalPermissions");

        // Try to load rscPermissions
        this.hookPermission("rscPermissions", Permission_rscPermissions.class, ServicePriority.Normal, "ru.simsonic.rscPermissions.MainPluginClass");

        // Try to load KPerms
        this.hookPermission("KPerms", Permission_KPerms.class, ServicePriority.Normal, "com.lightniinja.kperms.KPermsPlugin");

        final Permission perms = new Permission_SuperPerms(this);
        this.sm.register(Permission.class, perms, this, ServicePriority.Lowest);
        Vault.log.info("[Permission] SuperPermissions loaded as backup permission system.");

        this.perms = this.sm.getRegistration(Permission.class).getProvider();
    }

    private void hookChat(final String name, final Class<? extends Chat> hookClass, final ServicePriority priority, final String... packages) {
        try {
            if (Vault.packagesExists(packages)) {
                final Chat chat = hookClass.getConstructor(Plugin.class, Permission.class).newInstance(this, this.perms);
                this.sm.register(Chat.class, chat, this, priority);
                Vault.log.info(String.format("[Chat] %s found: %s", name, chat.isEnabled() ? "Loaded" : "Waiting"));
            }
        } catch (final Exception e) {
            Vault.log.severe(String.format("[Chat] There was an error hooking %s - check to make sure you're using a compatible version!", name));
        }
    }

    private void hookEconomy(final String name, final Class<? extends Economy> hookClass, final ServicePriority priority, final String... packages) {
        try {
            if (Vault.packagesExists(packages)) {
                final Economy econ = hookClass.getConstructor(Plugin.class).newInstance(this);
                this.sm.register(Economy.class, econ, this, priority);
                Vault.log.info(String.format("[Economy] %s found: %s", name, econ.isEnabled() ? "Loaded" : "Waiting"));
            }
        } catch (final Exception e) {
            Vault.log.severe(String.format("[Economy] There was an error hooking %s - check to make sure you're using a compatible version!", name));
        }
    }

    private void hookPermission(final String name, final Class<? extends Permission> hookClass, final ServicePriority priority, final String... packages) {
        try {
            if (Vault.packagesExists(packages)) {
                final Permission perms = hookClass.getConstructor(Plugin.class).newInstance(this);
                this.sm.register(Permission.class, perms, this, priority);
                Vault.log.info(String.format("[Permission] %s found: %s", name, perms.isEnabled() ? "Loaded" : "Waiting"));
            }
        } catch (final Exception e) {
            Vault.log.severe(String.format("[Permission] There was an error hooking %s - check to make sure you're using a compatible version!", name));
        }
    }

    @Override
    public boolean onCommand(final CommandSender sender, @NotNull final Command command, @NotNull final String commandLabel, final String[] args) {
        if (!sender.hasPermission("vault.admin")) {
            sender.sendMessage("You do not have permission to use that command!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("vault-info")) {
            this.infoCommand(sender);
            return true;
        } else if (command.getName().equalsIgnoreCase("vault-convert")) {
            this.convertCommand(sender, args);
            return true;
        } else {
            // Show help
            sender.sendMessage("Vault Commands:");
            sender.sendMessage("  /vault-info - Displays information about Vault");
            sender.sendMessage("  /vault-convert [economy1] [economy2] - Converts from one Economy to another");
            return true;
        }
    }

    private void convertCommand(final CommandSender sender, final String[] args) {
        final Collection<RegisteredServiceProvider<Economy>> econs = getServer().getServicesManager().getRegistrations(Economy.class);
        if (econs == null || econs.size() < 2) {
            sender.sendMessage("You must have at least 2 economies loaded to convert.");
            return;
        } else if (args.length != 2) {
            sender.sendMessage("You must specify only the economy to convert from and the economy to convert to. (names should not contain spaces)");
            return;
        }
        Economy econ1 = null;
        Economy econ2 = null;
        final StringBuilder economies = new StringBuilder();
        for (final RegisteredServiceProvider<Economy> econ : econs) {
            final String econName = econ.getProvider().getName().replace(" ", "");
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
        for (final OfflinePlayer op : Bukkit.getServer().getOfflinePlayers()) {
            if (econ1.hasAccount(op)) {
                if (econ2.hasAccount(op)) {
                    continue;
                }
                econ2.createPlayerAccount(op);
                final double diff = econ1.getBalance(op) - econ2.getBalance(op);
                if (diff > 0) {
                    econ2.depositPlayer(op, diff);
                } else if (diff < 0) {
                    econ2.withdrawPlayer(op, -diff);
                }

            }
        }
        sender.sendMessage("Converson complete, please verify the data before using it.");
    }

    private void infoCommand(final CommandSender sender) {
        // Get String of Registered Economy Services
        StringBuilder registeredEcons = null;
        final Collection<RegisteredServiceProvider<Economy>> econs = getServer().getServicesManager().getRegistrations(Economy.class);
        for (final RegisteredServiceProvider<Economy> econ : econs) {
            final Economy e = econ.getProvider();
            if (registeredEcons == null) {
                registeredEcons = new StringBuilder(e.getName());
            } else {
                registeredEcons.append(", ").append(e.getName());
            }
        }

        // Get String of Registered Permission Services
        StringBuilder registeredPerms = null;
        final Collection<RegisteredServiceProvider<Permission>> perms = getServer().getServicesManager().getRegistrations(Permission.class);
        for (final RegisteredServiceProvider<Permission> perm : perms) {
            final Permission p = perm.getProvider();
            if (registeredPerms == null) {
                registeredPerms = new StringBuilder(p.getName());
            } else {
                registeredPerms.append(", ").append(p.getName());
            }
        }

        StringBuilder registeredChats = null;
        final Collection<RegisteredServiceProvider<Chat>> chats = getServer().getServicesManager().getRegistrations(Chat.class);
        for (final RegisteredServiceProvider<Chat> chat : chats) {
            final Chat c = chat.getProvider();
            if (registeredChats == null) {
                registeredChats = new StringBuilder(c.getName());
            } else {
                registeredChats.append(", ").append(c.getName());
            }
        }

        // Get Economy & Permission primary Services
        final RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = null;
        if (rsp != null) {
            econ = rsp.getProvider();
        }
        Permission perm = null;
        final RegisteredServiceProvider<Permission> rspp = this.getServer().getServicesManager().getRegistration(Permission.class);
        if (rspp != null) {
            perm = rspp.getProvider();
        }
        Chat chat = null;
        final RegisteredServiceProvider<Chat> rspc = this.getServer().getServicesManager().getRegistration(Chat.class);
        if (rspc != null) {
            chat = rspc.getProvider();
        }
        // Send user some info!
        sender.sendMessage(String.format("[%s] Vault v%s Information", this.getDescription().getName(), this.getDescription().getVersion()));
        sender.sendMessage(String.format("[%s] Economy: %s [%s]", this.getDescription().getName(), econ == null ? "None" : econ.getName(), registeredEcons.toString()));
        sender.sendMessage(String.format("[%s] Permission: %s [%s]", this.getDescription().getName(), perm == null ? "None" : perm.getName(), registeredPerms.toString()));
        sender.sendMessage(String.format("[%s] Chat: %s [%s]", this.getDescription().getName(), chat == null ? "None" : chat.getName(), registeredChats.toString()));
    }

    /**
     * Determines if all packages in a String array are within the Classpath
     * This is the best way to determine if a specific plugin exists and will be
     * loaded. If the plugin package isn't loaded, we shouldn't bother waiting
     * for it!
     *
     * @param packages String Array of package names to check
     * @return Success or Failure
     */
    private static boolean packagesExists(final String... packages) {
        try {
            for (final String pkg : packages) {
                Class.forName(pkg);
            }
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    public double updateCheck(final double currentVersion) {
        try {
            final URL url = new URL("https://api.curseforge.com/servermods/files?projectids=33184");
            final URLConnection conn = url.openConnection();
            conn.setReadTimeout(5000);
            conn.addRequestProperty("User-Agent", "Vault Update Checker");
            conn.setDoOutput(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.size() == 0) {
                getLogger().warning("No files found, or Feed URL is bad.");
                return currentVersion;
            }
            // Pull the last version from the JSON
            this.newVersionTitle = ((String) ((JSONObject) array.get(array.size() - 1)).get("name")).replace("Vault", "").trim();
            return Double.parseDouble(this.newVersionTitle.replaceFirst("\\.", "").trim());
        } catch (final Exception e) {
            Vault.log.info("There was an issue attempting to check for the latest version.");
        }
        return currentVersion;
    }

    private void findCustomData(final Metrics metrics) {
        // Create our Economy Graph and Add our Economy plotters
        final RegisteredServiceProvider<Economy> rspEcon = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = null;
        if (rspEcon != null) {
            econ = rspEcon.getProvider();
        }
        String econName = econ != null ? econ.getName() : "No Economy";
        // TODO: Find out if you can do this right (no org.bstats.charts)
        metrics.addCustomChart(new org.bstats.charts.SimplePie("economy", () -> econName));

        // Create our Permission Graph and Add our permission Plotters
        String permName = Bukkit.getServer().getServicesManager().getRegistration(Permission.class).getProvider().getName();
        metrics.addCustomChart(new org.bstats.charts.SimplePie("permission", () -> permName));

        // Create our Chat Graph and Add our chat Plotters
        final RegisteredServiceProvider<Chat> rspChat = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        Chat chat = null;
        if (rspChat != null) {
            chat = rspChat.getProvider();
        }
        String chatName = chat != null ? chat.getName() : "No Chat";
        metrics.addCustomChart(new org.bstats.charts.SimplePie("chat", () -> chatName));
    }

    public class VaultListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerJoin(final PlayerJoinEvent event) {
            final Player player = event.getPlayer();
            if (Vault.this.perms.has(player, "vault.update")) {
                try {
                    if (Vault.this.newVersion > Vault.this.currentVersion) {
                        player.sendMessage("Vault " + Vault.this.newVersionTitle + " is out! You are running " + Vault.this.currentVersionTitle);
                        player.sendMessage("Update Vault at: " + Vault.VAULT_BUKKIT_URL);
                    }
                } catch (final Exception e) {
                    // Ignore exceptions
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(final PluginEnableEvent event) {
            if (event.getPlugin().getDescription().getName().equals("Register") && Vault.packagesExists("com.nijikokun.register.payment.Methods")) {
                if (!Methods.hasMethod()) {
                    try {
                        final Method m = Methods.class.getMethod("addMethod", Methods.class);
                        m.setAccessible(true);
                        m.invoke(null, "Vault", new net.milkbowl.vault.VaultEco());
                        if (!Methods.setPreferred("Vault")) {
                            Vault.log.info("Unable to hook register");
                        } else {
                            Vault.log.info("[Vault] - Successfully injected Vault methods into Register.");
                        }
                    } catch (final SecurityException | InvocationTargetException | IllegalAccessException |
                                   IllegalArgumentException | NoSuchMethodException e) {
                        Vault.log.info("Unable to hook register");
                    }
                }
            }
        }
    }
}
