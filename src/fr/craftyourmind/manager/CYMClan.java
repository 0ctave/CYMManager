package fr.craftyourmind.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.craftyourmind.manager.sql.SQLCYMManager;
import fr.craftyourmind.manager.util.ReputeData;

public class CYMClan {

	private static List<CYMClan> list = new ArrayList<CYMClan>();
	
	private Map<CYMReputation, ReputeData> listReputePts = new HashMap<CYMReputation, ReputeData>();
	private List<CYMPlayer> membres = new ArrayList<CYMPlayer>();
	
	public int id = 0;
	public String name = "";
	public int archi = 5;
	public long dateCreation = 0;
	public int penalty = 0;
	public CYMPlayer chef;
	
	public CYMClan(String name) {
		this.name = name;
		dateCreation = new Date().getTime();
	}
	
	public int getAncien(){
		// En nombre de jours
		long diff = new Date().getTime() - dateCreation;
		return (int)(diff / (1000 * 60 * 60 * 24));
	}
	
	public void setChef(CYMPlayer chef) {
		this.chef = chef;
		if(chef != null)
			sendMessage(chef.name+" is chef of "+name+" !");
		save();
	}
	
	protected void join(CYMPlayer membre) {
		if(!membres.contains(membre)){
			membres.add(membre);
			sendMessage(membre.name+" join "+name+" !");
		}
	}
	
	protected void leave(CYMPlayer membre) {
		if(chef == membre) setChef(null);
		sendMessage(membre.name+" leave "+name+" !");
		membres.remove(membre);
	}
	
	public boolean containMembre(CYMPlayer membre) {
		return membres.contains(membre);
	}
	
	public void sendMessage(String msg){
		if(!msg.isEmpty()){ for(CYMPlayer mp : membres) mp.sendMessage(msg); }
	}
	
	public List<CYMPlayer> getMembres(){
		return membres;
	}
	
	public static void add(CYMClan mc){
		list.add(mc);
		mc.create();
	}
	
	public static void addLoad(CYMClan oc){
		list.add(oc);
	}
	
	public static void remove(String clan){
		CYMClan mc = get(clan);
		if(mc != null) mc.del();
	}
	
	public static List<CYMClan> get(){
		return list;
	}
	
	public static CYMClan get(int id){
		for (CYMClan mc : list)
			if(mc.id == id) return mc;
		return null;
	}
	
	public static CYMClan get(String clan){
		for (CYMClan mc : list)
			if(mc.name.equalsIgnoreCase(clan)) return mc;
		return null;
	}
	
	public static CYMClan getOrCreate(String name){
		for(CYMClan mc : list) if(mc.name.equals(name)) return mc;
		CYMClan mc = new CYMClan(name);
		checkDB(mc);
		return mc;
	}
	
	private static void checkDB(CYMClan mc) {
		int id = SQLCYMManager.getIdClan(mc.name);
		if(id <= 0) add(mc); else mc.id = id;
	}
	
	public void addRepute(CYMReputation repute, int points, int param){
		if(repute != null){
			if(listReputePts.containsKey(repute)){
				ReputeData d = listReputePts.get(repute);
				d.points += points;
				if(param > 0) d.param = param;
				else d.param = 0;
				save(repute);
			}else{
				if(CYMReputation.getById(repute.id) != null){ // if not delete
					listReputePts.put(repute, new ReputeData(points, param));
					create(repute);
				}
			}
		}
	}
	
	public void initRepute(CYMReputation repute, int points, int param){
		if(repute != null){
			if(listReputePts.containsKey(repute)){
				ReputeData d = listReputePts.get(repute);
				d.points = points;
				d.param = param;
				save(repute);
			}else{
				if(CYMReputation.getById(repute.id) != null){ // if not delete
					listReputePts.put(repute, new ReputeData(points, param));
					create(repute);
				}
			}
		}
	}
	
	public void addReputeLoad(int idRepute, int points, int param) {
		CYMReputation rep = CYMReputation.getById(idRepute);
		if(rep != null) listReputePts.put(rep, new ReputeData(points, param));
	}
	
	public int getReputePts(CYMReputation rep){
		ReputeData rdata = listReputePts.get(rep);
		if(rdata == null) return 0;
		return rdata.points;
	}
	
	public int getReputeParam(CYMReputation rep){
		ReputeData rdata = listReputePts.get(rep);
		if(rdata == null) return 0;
		return rdata.param;
	}
		
	public Map<CYMReputation, ReputeData> getReputations(){
		return listReputePts;
	}
	
	private void create(CYMReputation repute) {
		SQLCYMManager.create(this, repute);
	}
	private void save(CYMReputation repute) {
		SQLCYMManager.save(this, repute);
	}
	
	public void create(){
		SQLCYMManager.create(this);
	}
	
	public void save() {
		SQLCYMManager.save(this);
	}
	
	public void del(){
		for(CYMReputation repute : getReputations().keySet())
			SQLCYMManager.delete(this, repute);
		sendMessage(name+" is remove.");
		for(CYMPlayer mp : membres.toArray(new CYMPlayer[0])) mp.leave();
		list.remove(this);
		SQLCYMManager.delete(this);
	}
}