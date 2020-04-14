package fr.craftyourmind.manager.checker;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import fr.craftyourmind.manager.CYMChecker;
import fr.craftyourmind.manager.PeriodicCheck;
import fr.craftyourmind.manager.CYMChecker.ICheckerEntity;
import fr.craftyourmind.manager.CYMChecker.ICheckerPosition;
import fr.craftyourmind.manager.util.IChecker;

public class PositionMeta extends CheckerMeta implements IChecker{

	public final static int INSIDE = 1;
	public final static int OUTSIDE = 2;
	
	private ICheckerPosition cp;
	private Entity entity;
	private String world = "";
	private int x, y, z, radius, state, preState;
	private long time = 0;
	
	public PositionMeta(ICheckerEntity e, ICheckerPosition cp) {
		super(e, cp);
		this.cp = cp;
		entity = e.getEntity();
		PeriodicCheck.add(this);
		world = cp.getWorld();
		x = cp.getX();
		y = cp.getY();
		z = cp.getZ();
		radius = cp.getRadius();
		state = 0;
		preState = 0;
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void stop() {
		super.stop();
		PeriodicCheck.remove(this);
	}
	
	@Override
	public int getType() {
		return CYMChecker.POSITION;
	}

	@Override
	public boolean checker() {
		if(time < System.currentTimeMillis()){
			time = System.currentTimeMillis()+400;
			Location loc = entity.getLocation();
			if(loc.getBlockX() >= x - radius && loc.getBlockX() <= x + radius && loc.getBlockZ() >= z - radius && loc.getBlockZ() <= z + radius && (y == 0 || (loc.getBlockY() >= y - radius - 1 && loc.getBlockY() <= y + radius) && world.equals(loc.getWorld().getName())))
				state = INSIDE;
			else
				state = OUTSIDE;
			
			if(state != preState){
				preState = state;
				if(state == INSIDE) cp.inside();
				else if(state == OUTSIDE) cp.outside();
			}
		}
		return false;
	}

}
