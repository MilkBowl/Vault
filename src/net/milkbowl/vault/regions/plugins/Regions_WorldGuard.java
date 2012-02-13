package net.milkbowl.vault.regions.plugins;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.milkbowl.vault.regions.Regions;

public class Regions_WorldGuard extends Regions {
	private static final Logger log = Logger.getLogger("Minecraft");
	private final String name = "WorldGuard";
	private Plugin plugin = null;
	protected WorldGuardPlugin wG = null;
	
	public Regions_WorldGuard(Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new RegionsServerListener(this), plugin);
		
		if(wG == null)  {
			Plugin twG = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
			if(twG != null && twG.isEnabled() && twG.getClass().getName().equals("com.sk89q.worldguard.bukkit.WorldGuardPlugin")) {
				wG = (WorldGuardPlugin) twG;
			}
			log.info(String.format("[%s][Regions] %s hooked.", plugin.getDescription().getName(), name));
		}
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isEnabled() {
		return wG != null && wG.isEnabled();
	}

	@Override
	public boolean playerInRegion(Player player, String name) {
		RegionManager manager = wG.getGlobalRegionManager().get(player.getWorld());
		if(manager.hasRegion(name)) {
			Location playerloc = player.getLocation();
			return manager.getRegion(name).contains(playerloc.getBlockX(), playerloc.getBlockY(), playerloc.getBlockZ());
		}else{
			return false;
		}
	}

	@Override
	public Set<String> getRegions(Player player) {
		Set<String> ret = new HashSet<String>();
		RegionManager manager;
		for(World w : Bukkit.getWorlds()) {
			manager = wG.getGlobalRegionManager().get(w);
			for(Entry<String, ProtectedRegion> r : manager.getRegions().entrySet()) {
				if(r.getValue().getMembers().contains(wG.wrapPlayer(player)) || r.getValue().getOwners().contains(wG.wrapPlayer(player))) {
					ret.add(r.getKey());
				}
			}
		}
		return ret;
	}
	
	@Override
	public List<String> getMembers(String region) {
		List<String> ret = new ArrayList<String>();
		RegionManager manager;
		for(World w : Bukkit.getWorlds()) {
			manager = wG.getGlobalRegionManager().get(w);
			if(manager.hasRegion(region)) {
				ProtectedRegion tmp = manager.getRegion(region);
				for(String player : tmp.getMembers().getPlayers()) {
					ret.add(player);
				}
				break;
			}
		}
		return ret;
	}
	@Override
	public List<String> getOwners(String region) {
		List<String> ret = new ArrayList<String>();
		RegionManager manager;
		for(World w : Bukkit.getWorlds()) {
			manager = wG.getGlobalRegionManager().get(w);
			if(manager.hasRegion(region)) {
				ProtectedRegion tmp = manager.getRegion(region);
				for(String player : tmp.getOwners().getPlayers()) {
					if(!ret.contains(player)) ret.add(player);
				}
				break;
			}
		}
		return ret;
	}
	@Override
	public boolean canBuildAt(Player player, Location loc) {
		return wG.canBuild(player, loc);
	}
	
	public boolean canBuildAt(Player player, Block block) {
		return wG.canBuild(player, block);
	}
	
	@Override
	public String getWelcomeMessage(String region) {
		RegionManager manager;
		for(World w : Bukkit.getWorlds()) {
			manager = wG.getGlobalRegionManager().get(w);
			if(manager.hasRegion(region)) {
				Map<Flag<?>, Object> helper = manager.getRegion(region).getFlags();
				return String.valueOf(helper.get(DefaultFlag.GREET_MESSAGE));
			}
		}
		return null;
	}
	@Override
	public String getFarewellMessage(String region) {
		RegionManager manager;
		for(World w : Bukkit.getWorlds()) {
			manager = wG.getGlobalRegionManager().get(w);
			if(manager.hasRegion(region)) {
				Map<Flag<?>, Object> helper = manager.getRegion(region).getFlags();
				return String.valueOf(helper.get(DefaultFlag.FAREWELL_MESSAGE));
			}
		}
		return null;
	}
	
	@Override
	public Location getFirstCorner(String region) {
		RegionManager manager;
		for(World w : Bukkit.getWorlds()) {
			manager = wG.getGlobalRegionManager().get(w);
			if(manager.hasRegion(region)) {
				BlockVector a = manager.getRegion(region).getMinimumPoint();
				return new Location(w, a.getX(), a.getY(), a.getZ());
			}
		}
		return null;
	}
	@Override
	public Location getSecondCorner(String region) {
		RegionManager manager;
		for(World w : Bukkit.getWorlds()) {
			manager = wG.getGlobalRegionManager().get(w);
			if(manager.hasRegion(region)) {
				BlockVector a = manager.getRegion(region).getMaximumPoint();
				return new Location(w, a.getX(), a.getY(), a.getZ());
			}
		}
		return null;
	}
	
	public class RegionsServerListener implements Listener {

        private Regions_WorldGuard regions = null;

        public RegionsServerListener(Regions_WorldGuard regions) {
            this.regions = regions;     
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if(regions.wG == null) {
            	Plugin twG = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
    			if(twG != null && twG.isEnabled() && twG.getClass().getName().equals("com.sk89q.worldguard.bukkit.WorldGuardPlugin")) {
    				wG = (WorldGuardPlugin) twG;
    			}
    			log.info(String.format("[%s][Regions] %s hooked.", plugin.getDescription().getName(), name));
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
        	 if(regions.wG != null) {
        		 if(event.getPlugin().getDescription().getName().equals("WorldGuard")) {
        			 regions.wG = null;
        			 log.info(String.format("[%s][Regions] %s unhooked.", plugin.getDescription().getName(), regions.name));
        		 }
        	 }
        }
    }

	@Override
	public boolean canUse(Player player) {
		return canUse(player, player.getLocation());
	}
	@Override
	public boolean canUse(Player player, Location location) {
		LocalPlayer localPlayer = wG.wrapPlayer(player);
		Vector vec = toVector(location);
		ApplicableRegionSet set = wG.getGlobalRegionManager().get(location.getWorld()).getApplicableRegions(vec);
		return set.allows(DefaultFlag.USE) || set.canBuild(localPlayer);
	}


}
