package net.milkbowl.vault.permission.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VaultPermissionCheckEvent extends Event {

	private State state = State.DEFAULT;
	private String player;
	private String world;
	private String permissionSystem;
	private String permissionNode;
	
	public VaultPermissionCheckEvent (String player, String world, String permissionSystem, String permissionNode) {
		this.player = player;
		this.world = world;
		this.permissionSystem = permissionSystem;
		this.permissionNode = permissionNode;
	}
	
	@Override
	public HandlerList getHandlers() {
		return null;
	}
	
	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * @return the player
	 */
	public String getPlayer() {
		return player;
	}

	/**
	 * @return the world
	 */
	public String getWorld() {
		return world;
	}

	/**
	 * @param world the world to set
	 */
	public void setWorld(String world) {
		this.world = world;
	}

	/**
	 * @return the permissionSystem
	 */
	public String getPermissionSystem() {
		return permissionSystem;
	}

	/**
	 * @return the permissionNode
	 */
	public String getPermissionNode() {
		return permissionNode;
	}

	/**
	 * @param permissionNode the permissionNode to set
	 */
	public void setPermissionNode(String permissionNode) {
		this.permissionNode = permissionNode;
	}

	public enum State {
		DEFAULT, TRUE, FALSE
	}

}
