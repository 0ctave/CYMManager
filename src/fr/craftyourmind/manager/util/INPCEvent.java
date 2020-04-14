package fr.craftyourmind.manager.util;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public interface INPCEvent {

	public void onNPCRightClick(Player p, int npc);

	public void onNPCDeathEvent(int npc, LivingEntity entity);

}
