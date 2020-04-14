package fr.craftyourmind.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;
import fr.craftyourmind.manager.checker.Kill;
import fr.craftyourmind.manager.util.INPCEvent;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.PlayerCreateNPCEvent;
import net.citizensnpcs.api.npc.NPC;

public class CitizensManager implements Listener{

	private boolean exist = false;
	private List<INPCEvent> npcEvents = new ArrayList<INPCEvent>();

	public void init(){
		Bukkit.getPluginManager().registerEvents(this, Plugin.it);
	}
	
	// ******************* EVENT *******************	
	
	@EventHandler
	public void onNPCCreateEvent(PlayerCreateNPCEvent event){
		CYMNPC npc = new CYMNPC(event.getNPC().getId(), event.getNPC().getName());
		npc.create();
		CYMNPC.add(npc);
		CYMNPC.sendCreateNpc(npc);
	}
	
	@EventHandler
	public void onNPCRemoveEvent(NPCRemoveEvent event){
		CYMNPC.remove(event.getNPC().getId());
	}
	
	@EventHandler
	public void onNPCRightClick(NPCRightClickEvent event) {
		for(INPCEvent npcevent : npcEvents)
			npcevent.onNPCRightClick(event.getClicker(), event.getNPC().getId());
	}
	
	@EventHandler
	public void onNPCDeathEvent(NPCDeathEvent event) {
		Kill.checkOnDeath(Kill.giveKiller((LivingEntity)event.getNPC().getEntity()), (LivingEntity)event.getNPC().getEntity());
		for(INPCEvent npcevent : npcEvents)
			npcevent.onNPCDeathEvent(event.getNPC().getId(), (LivingEntity)event.getNPC().getEntity());
	}
	
	// ******************* CMD *******************
	
	public boolean isNPC(Entity entity) {
		return CitizensAPI.getNPCRegistry().isNPC(entity);
	}
	
	public boolean hasSelectedNPC(Player p) {
		List<MetadataValue> selected = p.getMetadata("selected");
        if (selected == null || selected.size() == 0) 
        	return false;
        return true;
	}

	public int getSelectedNPC(Player p) {
		List<MetadataValue> selected = p.getMetadata("selected");
        if (selected == null || selected.size() == 0 )
        	return selected.get(0).asInt();
        return -1;
	}
	
	public Map<Integer, String> getIdName(){
		Map<Integer, String> list = new HashMap<Integer, String>();
		for(NPC npc : CitizensAPI.getNPCRegistry()){
			list.put(npc.getId(), npc.getName());
		}
		return list;
	}
	
	public Entity getPlayerNPC(int idnpc){
		NPC npc = CitizensAPI.getNPCRegistry().getById(idnpc);
		if(npc == null)  return null;
		else return npc.getEntity();
	}

	public void addNpcEvent(INPCEvent npcevent) {
		npcEvents.add(npcevent);
	}
	
}
