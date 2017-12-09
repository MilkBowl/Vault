/*
 * This file is part of Vault.
 *
 * Copyright (c) 2011 Morgan Humes <morgan@lanaddict.com>
 * Copyright (c) 2017 Neolumia
 *
 * Vault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Vault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.milkbowl.vault;

import static org.bukkit.ChatColor.YELLOW;

import com.nijikokun.register.payment.Methods;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Scanner;
import java.util.logging.Level;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.chat.plugins.Chat_DroxPerms;
import net.milkbowl.vault.chat.plugins.Chat_GroupManager;
import net.milkbowl.vault.chat.plugins.Chat_OverPermissions;
import net.milkbowl.vault.chat.plugins.Chat_Permissions3;
import net.milkbowl.vault.chat.plugins.Chat_PermissionsEx;
import net.milkbowl.vault.chat.plugins.Chat_Privileges;
import net.milkbowl.vault.chat.plugins.Chat_TotalPermissions;
import net.milkbowl.vault.chat.plugins.Chat_bPermissions;
import net.milkbowl.vault.chat.plugins.Chat_bPermissions2;
import net.milkbowl.vault.chat.plugins.Chat_iChat;
import net.milkbowl.vault.chat.plugins.Chat_mChat;
import net.milkbowl.vault.chat.plugins.Chat_mChatSuite;
import net.milkbowl.vault.chat.plugins.Chat_rscPermissions;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.plugins.Economy_BOSE7;
import net.milkbowl.vault.economy.plugins.Economy_CommandsEX;
import net.milkbowl.vault.economy.plugins.Economy_Craftconomy3;
import net.milkbowl.vault.economy.plugins.Economy_CurrencyCore;
import net.milkbowl.vault.economy.plugins.Economy_DigiCoin;
import net.milkbowl.vault.economy.plugins.Economy_Dosh;
import net.milkbowl.vault.economy.plugins.Economy_EconXP;
import net.milkbowl.vault.economy.plugins.Economy_Essentials;
import net.milkbowl.vault.economy.plugins.Economy_GoldIsMoney2;
import net.milkbowl.vault.economy.plugins.Economy_GoldenChestEconomy;
import net.milkbowl.vault.economy.plugins.Economy_Gringotts;
import net.milkbowl.vault.economy.plugins.Economy_McMoney;
import net.milkbowl.vault.economy.plugins.Economy_MiConomy;
import net.milkbowl.vault.economy.plugins.Economy_MineConomy;
import net.milkbowl.vault.economy.plugins.Economy_MultiCurrency;
import net.milkbowl.vault.economy.plugins.Economy_SDFEconomy;
import net.milkbowl.vault.economy.plugins.Economy_TAEcon;
import net.milkbowl.vault.economy.plugins.Economy_XPBank;
import net.milkbowl.vault.economy.plugins.Economy_eWallet;
import net.milkbowl.vault.economy.plugins.Economy_iConomy6;
import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.permission.plugins.Permission_DroxPerms;
import net.milkbowl.vault.permission.plugins.Permission_GroupManager;
import net.milkbowl.vault.permission.plugins.Permission_KPerms;
import net.milkbowl.vault.permission.plugins.Permission_OverPermissions;
import net.milkbowl.vault.permission.plugins.Permission_Permissions3;
import net.milkbowl.vault.permission.plugins.Permission_PermissionsBukkit;
import net.milkbowl.vault.permission.plugins.Permission_PermissionsEx;
import net.milkbowl.vault.permission.plugins.Permission_Privileges;
import net.milkbowl.vault.permission.plugins.Permission_SimplyPerms;
import net.milkbowl.vault.permission.plugins.Permission_Starburst;
import net.milkbowl.vault.permission.plugins.Permission_SuperPerms;
import net.milkbowl.vault.permission.plugins.Permission_TotalPermissions;
import net.milkbowl.vault.permission.plugins.Permission_Xperms;
import net.milkbowl.vault.permission.plugins.Permission_bPermissions;
import net.milkbowl.vault.permission.plugins.Permission_bPermissions2;
import net.milkbowl.vault.permission.plugins.Permission_rscPermissions;
import net.milkbowl.vault.util.VersionComparator;
import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Vault extends JavaPlugin {

  private static final String HOOK = "[%s] %s found: %s";
  private static final String HOOK_FAILED = "[%s] There was an error hooking %s - check to make sure you're using a compatible version!";
  private static final String NO_PERMISSION = "&cYou do not have permission to use that command!";
  private Permission perms;

  /**
   * Determines if all packages in a String array are within the Classpath.
   * This is the best way to determine if a specific plugin exists and will be
   * loaded. If the plugin package isn't loaded, we shouldn't bother waiting
   * for it!
   *
   * @param packages String Array of package names to check
   * @return Success or Failure
   */
  private static boolean packagesExists(String... packages) {
    try {
      for (String pkg : packages) {
        Class.forName(pkg);
      }
      return true;
    } catch (Throwable throwable) {
      return false;
    }
  }

  private static String colorize(String input) {
    return ChatColor.translateAlternateColorCodes('&', input);
  }

  private static String read(String url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    connection.setRequestProperty("User-Agent", "Natrolite/1.0");
    try (InputStream in = connection.getInputStream()) {
      Scanner s = new Scanner(in).useDelimiter("\\A");
      return s.hasNext() ? s.next() : "";
    }
  }

  @Override
  public void onEnable() {
    final long start = System.currentTimeMillis();

    getConfig().addDefault("debug", false);
    getConfig().addDefault("updater.check", true);
    getConfig().addDefault("updater.download", true);
    getConfig().addDefault("messages.access-denied", NO_PERMISSION);
    getConfig().options().copyDefaults(true);
    saveConfig();

    loadEconomy();
    loadPermission();
    loadChat();

    getServer().getPluginManager().registerEvents(new VaultListener(), this);

    try {
      Metrics metrics = new Metrics(this);
      metrics.addCustomChart(new Metrics.SimplePie("permission_service") {
        @Override
        public String getValue() {
          final RegisteredServiceProvider<Permission> service =
            getServer().getServicesManager().getRegistration(Permission.class);
          if (service != null && service.getPlugin() != null) {
            return service.getProvider().getName();
          }
          return "None";
        }
      });
      metrics.addCustomChart(new Metrics.SimplePie("economy_service") {
        @Override
        public String getValue() {
          final RegisteredServiceProvider<Economy> service =
            getServer().getServicesManager().getRegistration(Economy.class);
          if (service != null && service.getPlugin() != null) {
            return service.getProvider().getName();
          }
          return "None";
        }
      });
      metrics.addCustomChart(new Metrics.SimplePie("chat_service") {
        @Override
        public String getValue() {
          final RegisteredServiceProvider<Chat> service =
            getServer().getServicesManager().getRegistration(Chat.class);
          if (service != null && service.getPlugin() != null) {
            return service.getProvider().getName();
          }
          return "None";
        }
      });
      metrics.addCustomChart(new Metrics.SimplePie("natrolite_installed") {
        @Override
        public String getValue() {
          if (getServer().getPluginManager().getPlugin("Natrolite") != null) {
            return "Yes";
          }
          return "No";
        }
      });
    } catch (Throwable throwable) {
      getLogger().info("Could not start metrics service");
    }

    if (getConfig().getBoolean("updater.check")) {
      getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
        @Override
        public void run() {
          try {
            final String current = getDescription().getVersion();
            final String output = read("http://api.spiget.org/v2/resources/41918/versions/latest");
            final JSONParser parser = new JSONParser();
            final JSONObject object = (JSONObject) parser.parse(output);
            final String latest = (String) object.get("name");
            final Path file = getServer().getUpdateFolderFile().toPath().resolve(getFile().getName());

            if (latest == null) {
              throw new NullPointerException("Latest version is null");
            }

            if (VersionComparator.isOlderThan(current, latest)) {
              getLogger().info(String.format("You are using an outdated version (Current: %s | Latest: %s)", current, latest));

              if (getConfig().getBoolean("updater.download")) {
                Files.createDirectories(file.getParent());
                getLogger().info(String.format("Downloading Vault v%s..", latest));
                try (
                  InputStream in = new URL("http://api.spiget.org/v2/resources/41918/download").openStream()) {
                  Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
                }
                getLogger().info(String.format("Done! After next start Vault will be on version %s", latest));
              }
            } else {
              getLogger().info("You are running the latest version");
            }
          } catch (Throwable throwable) {
            getLogger().info("> Unknown error while checking for updates");
            if (getConfig().getBoolean("debug")) {
              throwable.printStackTrace();
            }
          }
        }
      });
    }

    getLogger().log(Level.INFO, "Plugin enabled ({0}ms)", System.currentTimeMillis() - start);
  }

  @Override
  public void onDisable() {
    // These methods are probably redundant
    getServer().getServicesManager().unregisterAll(this);
    Bukkit.getScheduler().cancelTasks(this);
  }

  private void loadChat() {
    hookChat("PermissionsEx", Chat_PermissionsEx.class, ServicePriority.Highest, "ru.tehkode.permissions.bukkit.PermissionsEx");
    hookChat("mChatSuite", Chat_mChatSuite.class, ServicePriority.Highest, "in.mDev.MiracleM4n.mChatSuite.mChatSuite");
    hookChat("mChat", Chat_mChat.class, ServicePriority.Highest, "net.D3GN.MiracleM4n.mChat");
    hookChat("OverPermissions", Chat_OverPermissions.class, ServicePriority.Highest, "com.overmc.overpermissions.internal.OverPermissions");
    hookChat("DroxPerms", Chat_DroxPerms.class, ServicePriority.Lowest, "de.hydrox.bukkit.DroxPerms.DroxPerms");
    hookChat("bPermssions2", Chat_bPermissions2.class, ServicePriority.Highest, "de.bananaco.bpermissions.api.ApiLayer");
    hookChat("bPermissions", Chat_bPermissions.class, ServicePriority.Normal, "de.bananaco.permissions.info.InfoReader");
    hookChat("GroupManager", Chat_GroupManager.class, ServicePriority.Normal, "org.anjocaido.groupmanager.GroupManager");
    hookChat("Permissions3", Chat_Permissions3.class, ServicePriority.Normal, "com.nijiko.permissions.ModularControl");
    hookChat("iChat", Chat_iChat.class, ServicePriority.Low, "net.TheDgtl.iChat.iChat");
    hookChat("Privileges", Chat_Privileges.class, ServicePriority.Normal, "net.krinsoft.privileges.Privileges");
    hookChat("rscPermissions", Chat_rscPermissions.class, ServicePriority.Normal, "ru.simsonic.rscPermissions.MainPluginClass");
    hookChat("TotalPermissions", Chat_TotalPermissions.class, ServicePriority.Normal, "net.ar97.totalpermissions.TotalPermissions");
  }

  private void loadEconomy() {
    hookEconomy("MiConomy", Economy_MiConomy.class, ServicePriority.Normal, "com.gmail.bleedobsidian.miconomy.Main");
    hookEconomy("MultiCurrency", Economy_MultiCurrency.class, ServicePriority.Normal, "me.ashtheking.currency.Currency", "me.ashtheking.currency.CurrencyList");
    hookEconomy("MineConomy", Economy_MineConomy.class, ServicePriority.Normal, "me.mjolnir.mineconomy.MineConomy");
    hookEconomy("McMoney", Economy_McMoney.class, ServicePriority.Normal, "boardinggamer.mcmoney.McMoneyAPI");
    hookEconomy("CraftConomy3", Economy_Craftconomy3.class, ServicePriority.Normal, "com.greatmancode.craftconomy3.tools.interfaces.BukkitLoader");
    hookEconomy("eWallet", Economy_eWallet.class, ServicePriority.Normal, "me.ethan.eWallet.ECO");
    hookEconomy("BOSEconomy7", Economy_BOSE7.class, ServicePriority.Normal, "cosine.boseconomy.BOSEconomy", "cosine.boseconomy.CommandHandler");
    hookEconomy("CurrencyCore", Economy_CurrencyCore.class, ServicePriority.Normal, "is.currency.Currency");
    hookEconomy("Gringotts", Economy_Gringotts.class, ServicePriority.Normal, "org.gestern.gringotts.Gringotts");
    hookEconomy("Essentials Economy", Economy_Essentials.class, ServicePriority.Low, "com.earth2me.essentials.api.Economy", "com.earth2me.essentials.api.NoLoanPermittedException", "com.earth2me.essentials.api.UserDoesNotExistException");
    hookEconomy("iConomy 6", Economy_iConomy6.class, ServicePriority.High, "com.iCo6.iConomy");
    hookEconomy("EconXP", Economy_EconXP.class, ServicePriority.Normal, "ca.agnate.EconXP.EconXP");
    hookEconomy("GoldIsMoney2", Economy_GoldIsMoney2.class, ServicePriority.Normal, "com.flobi.GoldIsMoney2.GoldIsMoney");
    hookEconomy("GoldenChestEconomy", Economy_GoldenChestEconomy.class, ServicePriority.Normal, "me.igwb.GoldenChest.GoldenChestEconomy");
    hookEconomy("Dosh", Economy_Dosh.class, ServicePriority.Normal, "com.gravypod.Dosh.Dosh");
    hookEconomy("CommandsEX", Economy_CommandsEX.class, ServicePriority.Normal, "com.github.zathrus_writer.commandsex.api.EconomyAPI");
    hookEconomy("SDFEconomy", Economy_SDFEconomy.class, ServicePriority.Normal, "com.github.omwah.SDFEconomy.SDFEconomy");
    hookEconomy("XPBank", Economy_XPBank.class, ServicePriority.Normal, "com.gmail.mirelatrue.xpbank.XPBank");
    hookEconomy("TAEcon", Economy_TAEcon.class, ServicePriority.Normal, "net.teamalpha.taecon.TAEcon");
    hookEconomy("DigiCoin", Economy_DigiCoin.class, ServicePriority.Normal, "co.uk.silvania.cities.digicoin.DigiCoin");
  }

  private void loadPermission() {
    hookPermission("Starburst", Permission_Starburst.class, ServicePriority.Highest, "com.dthielke.starburst.StarburstPlugin");
    hookPermission("PermissionsEx", Permission_PermissionsEx.class, ServicePriority.Highest, "ru.tehkode.permissions.bukkit.PermissionsEx");
    hookPermission("OverPermissions", Permission_OverPermissions.class, ServicePriority.Highest, "com.overmc.overpermissions.internal.OverPermissions");
    hookPermission("PermissionsBukkit", Permission_PermissionsBukkit.class, ServicePriority.Normal, "com.platymuus.bukkit.permissions.PermissionsPlugin");
    hookPermission("DroxPerms", Permission_DroxPerms.class, ServicePriority.High, "de.hydrox.bukkit.DroxPerms.DroxPerms");
    hookPermission("SimplyPerms", Permission_SimplyPerms.class, ServicePriority.Highest, "net.crystalyx.bukkit.simplyperms.SimplyPlugin");
    hookPermission("bPermissions 2", Permission_bPermissions2.class, ServicePriority.Highest, "de.bananaco.bpermissions.api.WorldManager");
    hookPermission("Privileges", Permission_Privileges.class, ServicePriority.Highest, "net.krinsoft.privileges.Privileges");
    hookPermission("bPermissions", Permission_bPermissions.class, ServicePriority.High, "de.bananaco.permissions.SuperPermissionHandler");
    hookPermission("GroupManager", Permission_GroupManager.class, ServicePriority.High, "org.anjocaido.groupmanager.GroupManager");
    hookPermission("Permissions 3 (Yeti)", Permission_Permissions3.class, ServicePriority.Normal, "com.nijiko.permissions.ModularControl");
    hookPermission("Xperms", Permission_Xperms.class, ServicePriority.Low, "com.github.sebc722.Xperms");
    hookPermission("TotalPermissions", Permission_TotalPermissions.class, ServicePriority.Normal, "net.ae97.totalpermissions.TotalPermissions");
    hookPermission("rscPermissions", Permission_rscPermissions.class, ServicePriority.Normal, "ru.simsonic.rscPermissions.MainPluginClass");
    hookPermission("KPerms", Permission_KPerms.class, ServicePriority.Normal, "com.lightniinja.kperms.KPermsPlugin");

    Permission perms = new Permission_SuperPerms(this);
    getServer().getServicesManager().register(Permission.class, perms, this, ServicePriority.Lowest);
    getLogger().info("[Permission] SuperPermissions loaded as backup permission system.");
    this.perms = getServer().getServicesManager().getRegistration(Permission.class).getProvider();
  }

  private void hookChat(String name, Class<? extends Chat> clazz, ServicePriority priority, String... packages) {
    try {
      if (packagesExists(packages)) {
        final Chat chat = clazz.getConstructor(Plugin.class, Permission.class).newInstance(this, perms);
        getServer().getServicesManager().register(Chat.class, chat, this, priority);
        getLogger().info(String.format(HOOK, "Chat", name, chat.isEnabled() ? "Loaded" : "Waiting"));
      }
    } catch (Throwable throwable) {
      getLogger().severe(String.format(HOOK_FAILED, "Chat", name));
    }
  }

  private void hookEconomy(String name, Class<? extends Economy> clazz, ServicePriority priority, String... packages) {
    try {
      if (packagesExists(packages)) {
        final Economy econ = clazz.getConstructor(Plugin.class).newInstance(this);
        getServer().getServicesManager().register(Economy.class, econ, this, priority);
        getLogger().info(String.format(HOOK, "Economy", name, econ.isEnabled() ? "Loaded" : "Waiting"));
      }
    } catch (Throwable throwable) {
      getLogger().severe(String.format(HOOK_FAILED, "Economy", name));
    }
  }

  private void hookPermission(String name, Class<? extends Permission> clazz, ServicePriority priority, String... packages) {
    try {
      if (packagesExists(packages)) {
        final Permission perms = clazz.getConstructor(Plugin.class).newInstance(this);
        getServer().getServicesManager().register(Permission.class, perms, this, priority);
        getLogger().info(String.format(HOOK, "Permission", name, perms.isEnabled() ? "Loaded" : "Waiting"));
      }
    } catch (Throwable throwable) {
      getLogger().severe(String.format(HOOK_FAILED, "Permission", name));
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!sender.hasPermission("vault.admin")) {
      sender.sendMessage(colorize(getConfig().getString("messages.access-denied", NO_PERMISSION)));
      return true;
    }

    if (command.getName().equalsIgnoreCase("vault-info")) {
      infoCommand(sender);
      return true;
    }

    if (command.getName().equalsIgnoreCase("vault-convert")) {
      convertCommand(sender, args);
      return true;
    }

    sender.sendMessage(YELLOW + "Vault Commands:");
    sender.sendMessage(YELLOW + "  /vault-info - Displays information about Vault");
    sender.sendMessage(YELLOW + "  /vault-convert [economy1] [economy2] - Converts from one Economy to another");
    return true;
  }

  // Not very clean
  private void infoCommand(CommandSender sender) {
    StringBuilder registeredEcons = null;
    StringBuilder registeredPerms = null;
    StringBuilder registeredChats = null;
    Economy econ = null;
    Permission perm = null;
    Chat chat = null;

    for (RegisteredServiceProvider<Economy> pr : getServer().getServicesManager().getRegistrations(Economy.class)) {
      if (registeredEcons == null) {
        registeredEcons = new StringBuilder(pr.getProvider().getName());
        continue;
      }
      registeredEcons.append(", ").append(pr.getProvider().getName());
    }

    for (RegisteredServiceProvider<Permission> pr : getServer().getServicesManager().getRegistrations(Permission.class)) {
      if (registeredPerms == null) {
        registeredPerms = new StringBuilder(pr.getProvider().getName());
        continue;
      }
      registeredPerms.append(", ").append(pr.getProvider().getName());
    }

    for (RegisteredServiceProvider<Chat> pr : getServer().getServicesManager().getRegistrations(Chat.class)) {
      if (registeredChats == null) {
        registeredChats = new StringBuilder(pr.getProvider().getName());
        continue;
      }
      registeredChats.append(", ").append(pr.getProvider().getName());
    }

    final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp != null) {
      econ = rsp.getProvider();
    }

    final RegisteredServiceProvider<Permission> rspp = getServer().getServicesManager().getRegistration(Permission.class);
    if (rspp != null) {
      perm = rspp.getProvider();
    }

    RegisteredServiceProvider<Chat> rspc = getServer().getServicesManager().getRegistration(Chat.class);
    if (rspc != null) {
      chat = rspc.getProvider();
    }

    sender.sendMessage(String.format("[%s] Vault v%s Information", getDescription().getName(), getDescription().getVersion()));
    sender.sendMessage(String.format("[%s] Economy: %s [%s]", getDescription().getName(), econ == null ? "None" : econ.getName(), registeredEcons == null ? "" : registeredChats));
    sender.sendMessage(String.format("[%s] Permission: %s [%s]", getDescription().getName(), perm == null ? "None" : perm.getName(), registeredPerms == null ? "" : registeredPerms));
    sender.sendMessage(String.format("[%s] Chat: %s [%s]", getDescription().getName(), chat == null ? "None" : chat.getName(), registeredChats == null ? "" : registeredChats));
  }

  // I don't even try
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
    String economies = "";
    for (RegisteredServiceProvider<Economy> econ : econs) {
      String econName = econ.getProvider().getName().replace(" ", "");
      if (econName.equalsIgnoreCase(args[0])) {
        econ1 = econ.getProvider();
      } else if (econName.equalsIgnoreCase(args[1])) {
        econ2 = econ.getProvider();
      }
      if (economies.length() > 0) {
        economies += ", ";
      }
      economies += econName;
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

  public class VaultListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
      if (event.getPlugin().getDescription().getName().equals("Register") && packagesExists("com.nijikokun.register.payment.Methods")) {
        if (!Methods.hasMethod()) {
          try {
            Method m = Methods.class.getMethod("addMethod", Methods.class);
            m.setAccessible(true);
            m.invoke(null, "Vault", new net.milkbowl.vault.VaultEco());
            if (!Methods.setPreferred("Vault")) {
              getLogger().info("Unable to hook register");
            } else {
              getLogger().info("[Vault] - Successfully injected Vault methods into Register.");
            }
          } catch (Exception ex) {
            getLogger().info("Unable to hook register");
          }
        }
      }
    }
  }
}
