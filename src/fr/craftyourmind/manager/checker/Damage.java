package fr.craftyourmind.manager.checker;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import fr.craftyourmind.manager.CYMChecker;
import fr.craftyourmind.manager.CYMChecker.ICheckerDamage;
import fr.craftyourmind.manager.CYMChecker.ICheckerEntity;

public class Damage extends CheckerMeta{

	private ICheckerDamage cm;
	private EntityDamageEvent event;
	private boolean recieved, given;
	private Entity victim, damager;	
	
	public Damage(ICheckerEntity e, ICheckerDamage cm) {
		super(e, cm);
		this.cm = cm;
	}

	@Override
	public void tick() {
		cm.setEvent(event);
		cm.setRecieved(recieved);
		cm.setGiven(given);
		cm.setVictim(victim);
		cm.setDamager(damager);
		cm.checker();
	}

	private void set(EntityDamageEvent event) {
		this.event = event;
	}
	
	private void set(boolean recieved, boolean given) {
		this.recieved = recieved;
		this.given = given;
	}
	
	private void set(Entity victim, Entity damager) {
		this.victim = victim;
		this.damager = damager;
	}
	
	@Override
	public int getType() {
		return CYMChecker.DAMAGE;
	}

	public static void onEntityDamageEvent(EntityDamageEvent event) {		
		Entity damager = null;
		if(event instanceof EntityDamageByEntityEvent){
			damager = ((EntityDamageByEntityEvent)event).getDamager();
			if(damager != null) tickEvent(event, damager, damager, event.getEntity(), false, true);
			if(damager instanceof Projectile){
				ProjectileSource ps = ((Projectile) damager).getShooter();
				if(ps != null && ps instanceof Entity){
					damager = (Entity) ps;
					tickEvent(event, damager, damager, event.getEntity(), false, true);
				}
			}
		}
		tickEvent(event, event.getEntity(), damager, event.getEntity(), true, false);
	}
	
	private static void tickEvent(EntityDamageEvent event, Entity e, Entity damager, Entity victim, boolean recieved, boolean given){
		ICheckerEntity ce = CYMChecker.getCheckerEntityMeta(e);
		if(ce != null){
			for(Damage d : ce.getCheckerDamage().toArray(new Damage[0])){
				d.set(event);
				d.set(recieved, given);
				d.set(victim, damager);
				d.tick();
			}
		}
	}
}
