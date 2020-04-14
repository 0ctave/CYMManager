package fr.craftyourmind.manager.checker;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.projectiles.ProjectileSource;
import fr.craftyourmind.manager.CYMChecker;
import fr.craftyourmind.manager.CYMChecker.ICheckerEntity;
import fr.craftyourmind.manager.CYMChecker.ICheckerKill;

public class Kill extends CheckerMeta{
	
	private LivingEntity killer;
	private LivingEntity victim;
	private ICheckerKill ck;
	
	public Kill(ICheckerEntity e, ICheckerKill ck) {
		super(e, ck);
		this.ck = ck;
	}

	@Override
	public void tick() {
		ck.setKiller(killer);
		ck.setVictim(victim);
		ck.checker();
	}

	public void set(LivingEntity killer, LivingEntity victim){
		this.killer = killer;
		this.victim = victim;
	}
	
	@Override
	public int getType() {
		return CYMChecker.KILL;
	}

	public static void onEntityDeath(EntityDeathEvent event) {
		checkOnDeath(giveKiller(event.getEntity()), event.getEntity());
	}
	
	public static LivingEntity giveKiller(LivingEntity victim){
		LivingEntity killer = null;
		EntityDamageEvent cause = victim.getLastDamageCause();
		if(cause != null && cause.getClass() == EntityDamageByEntityEvent.class) {
			EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) cause;
			Entity killerDamage = entityEvent.getDamager();
			if(killerDamage instanceof LivingEntity) killer = (LivingEntity) killerDamage;
			else if(killerDamage instanceof Projectile){
				ProjectileSource ps = ((Projectile) killerDamage).getShooter();
				if(ps != null && ps instanceof LivingEntity) killer = (LivingEntity) ps;
			}
		}
		return killer;
	}
	
	public static void checkOnDeath(LivingEntity killer, LivingEntity victim){
		if(isDoubleDeath(victim)) return; // bug double kill
		if(killer == null){
			if(victim != null) checkVictim(victim);
		}else{
			if(victim != null){
				checkKiller(killer, victim);
				checkVictim(killer, victim);
			}
		}
	}
	
	private static List<DoubleDeath> victims = new ArrayList<DoubleDeath>();
	public static boolean isDoubleDeath(LivingEntity victim){
		for(DoubleDeath dd : victims.toArray(new DoubleDeath[0])){
			if(dd.time < System.currentTimeMillis()) victims.remove(dd);
			else{ if(dd.victim.getEntityId() == victim.getEntityId()) return true; }
		}
		victims.add(new DoubleDeath(System.currentTimeMillis(), victim));
		return false;
	}
	
	public static void checkKiller(LivingEntity killer, LivingEntity victim) {
		check(CYMChecker.getCheckerEntityMeta(killer), killer, victim);
	}
	
	public static void checkVictim(LivingEntity victim) {
		checkVictim(null, victim);
	}
	
	public static void checkVictim(LivingEntity killer, LivingEntity victim) {
		check(CYMChecker.getCheckerEntityMeta(victim), killer, victim);
	}
	
	public static void check(ICheckerEntity ce, LivingEntity killer, LivingEntity victim){
		if(ce != null){
			for(Kill k : ce.getCheckerKill().toArray(new Kill[0])){
				k.set(killer, victim);
				k.tick();
			}
		}
	}
}

class DoubleDeath {
	public long time = 0;
	public LivingEntity victim;
	public DoubleDeath(long time, LivingEntity victim) {
		this.time = time+500;
		this.victim = victim;
	}
}
