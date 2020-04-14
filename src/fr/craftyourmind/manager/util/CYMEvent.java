package fr.craftyourmind.manager.util;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class CYMEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	
	
	public CYMEvent() {

	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
