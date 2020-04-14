package fr.craftyourmind.manager;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import fr.craftyourmind.manager.checker.Damage;
import fr.craftyourmind.manager.checker.Kill;
import fr.craftyourmind.manager.event.CYMLoginEvent;
import fr.craftyourmind.manager.packet.DataInfo;
import fr.craftyourmind.manager.packet.DataLogin;

public class CYMEvent implements Listener{

	public CYMEvent() {
		Bukkit.getPluginManager().registerEvents(this, Plugin.it);
	}
	
	// ******************* PLAYER *******************
	
	@EventHandler
	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
		if(CYMPlayer.get(event.getPlayer()).useMod)
			new DataInfo(DataInfo.WORLD, event.getPlayer().getWorld().getName()).send(event.getPlayer());
	}
	
	@EventHandler
	public void onMILoginEvent(CYMLoginEvent event) {
		DataLogin data = event.data;
		CYMPlayer mp = CYMPlayer.getOrCreate(data.getPlayer().getName()); // init
		//if(mp.useMod){
			mp.setPlayer(data.getPlayer());
			mp.useMod = true;
			CYMNPC.sendLogin(data.getPlayer());
			new DataInfo(DataInfo.WORLD, data.getPlayer().getWorld().getName()).send(data.getPlayer());
			mp.startCheckers();
			//ICheckerPlayer cp = MiniChecker.getPlayer(data.getPlayer());
			//if(cp != null) cp.startCheckers();
		//}
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		CYMPlayer mp = CYMPlayer.getOrCreate(event.getPlayer().getName());
		mp.useMod = false;
	}


	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Kill.onEntityDeath(event);
	}
	
	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event){
		Damage.onEntityDamageEvent(event);
	}
	
}
