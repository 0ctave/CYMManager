package fr.craftyourmind.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Entity;

import fr.craftyourmind.manager.CYMChecker.ICheckerClient;
import fr.craftyourmind.manager.CYMChecker.ICheckerEntity;

public class CYMEntity implements ICheckerEntity{

	private int idIncrementChecker;
	private List<ICheckerClient> checkerPositions = new ArrayList<ICheckerClient>();
	private List<ICheckerClient> checkerKills = new ArrayList<ICheckerClient>();
	private List<ICheckerClient> checkerDamage = new ArrayList<ICheckerClient>();
	private Map<Integer, List<ICheckerClient>> checkers = new HashMap<Integer, List<ICheckerClient>>();
	
	private Entity entity;
	
	public CYMEntity(Entity e) {
		this.entity = e;
		checkers.put(CYMChecker.POSITION, checkerPositions);
		checkers.put(CYMChecker.KILL, checkerKills);
		checkers.put(CYMChecker.DAMAGE, checkerDamage);
	}
	
	@Override
	public int getIdIncrementChecker() {
		return ++idIncrementChecker;
	}
	
	@Override
	public Entity getEntity() {
		return entity;
	}

	@Override
	public void addChecker(ICheckerClient cc) {
		checkers.get(cc.getType()).add(cc);
	}

	@Override
	public void removeChecker(ICheckerClient cc) {
		checkers.get(cc.getType()).remove(cc);
	}

	@Override
	public void startCheckers() {
		
	}
	
	@Override
	public List<ICheckerClient> getCheckerKill() {
		return checkerKills;
	}
	
	@Override
	public List<ICheckerClient> getCheckerDamage() {
		return checkerDamage;
	}
	
	@Override
	public boolean asBoolean() { return false; }
	@Override
	public byte asByte() { return 0; }
	@Override
	public double asDouble() { return 0; }
	@Override
	public float asFloat() { return 0; }
	@Override
	public int asInt() { return 0; }
	@Override
	public long asLong() { return 0; }
	@Override
	public short asShort() { return 0; }
	@Override
	public String asString() { return ""; }
	@Override
	public org.bukkit.plugin.Plugin getOwningPlugin() { return Plugin.it; }
	@Override
	public void invalidate() { }
	@Override
	public Object value() { return this; }
}
