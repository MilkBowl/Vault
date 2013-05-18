package net.milkbowl.vault.permission.Events;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
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

	public enum State {
		DEFAULT, TRUE, FALSE
	}

}
