package fr.craftyourmind.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import fr.craftyourmind.manager.CYMChecker.ICheckerClient;
import fr.craftyourmind.manager.CYMChecker.ICheckerPlayer;
import fr.craftyourmind.manager.sql.SQLCYMManager;
import fr.craftyourmind.manager.util.ReputeData;

public class CYMPlayer implements ICheckerPlayer{

	public static CYMPlayer nobody;
	private static List<CYMPlayer> list = new ArrayList<CYMPlayer>();
	
	private Map<CYMReputation, ReputeData> listReputePts = new HashMap<CYMReputation, ReputeData>();
	private List<ICheckerClient> checkerPositions = new ArrayList<ICheckerClient>();
	private List<ICheckerClient> checkerBlockEvents = new ArrayList<ICheckerClient>();
	private List<ICheckerClient> checkerKills = new ArrayList<ICheckerClient>();
	private List<ICheckerClient> checkerInventory = new ArrayList<ICheckerClient>();
	private List<ICheckerClient> checkerUseItem = new ArrayList<ICheckerClient>();
	private List<ICheckerClient> checkerDamage = new ArrayList<ICheckerClient>();
	private List<ICheckerClient> checkerTextScreen = new ArrayList<ICheckerClient>();
	private Map<Integer, List<ICheckerClient>> checkers = new HashMap<Integer, List<ICheckerClient>>();
	
	private int idIncrementChecker;
	public int id = 0;
	public String name = "";
	public int level = 0;
	public boolean useMod = false;
	
	private Player player;
	private CYMClan clan;
	
	public CYMPlayer(String name) {
		this.name = name;
		list.add(this);
		checkers.put(CYMChecker.POSITION, checkerPositions);
		checkers.put(CYMChecker.BLOCKEVENT, checkerBlockEvents);
		checkers.put(CYMChecker.KILL, checkerKills);
		checkers.put(CYMChecker.INVENTORY, checkerInventory);
		checkers.put(CYMChecker.USEITEM, checkerUseItem);
		checkers.put(CYMChecker.DAMAGE, checkerDamage);
		checkers.put(CYMChecker.TEXTSCREEN, checkerTextScreen);
	}
	
	@Override
	public int getIdIncrementChecker() {
		return ++idIncrementChecker;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public Entity getEntity() {
		return getPlayer();
	}
	
	@Override
	public Player getPlayer() {
		if(player == null) player = Bukkit.getPlayer(name);
		return player;
	}
	
	public void setPlayer(Player p){
		player = p;
		if(p == null) return;
		CYMChecker.initChecker(this, p);
	}
	
	public boolean hasClan() {
		return clan != null;
	}
	
	public void join(CYMClan mc) {
		leave();
		clan = mc;
		mc.join(this);
		save();
	}
	
	public void leave(){
		if(clan != null){
			clan.leave(this);
			clan = null;
			save();
		}
		clan = null;
	}
	
	public boolean isChef(){
		if(clan == null) return false;
		return clan.chef == this;
	}
	
	public CYMClan getClan(){
		return clan;
	}
	
	public void setClan(CYMClan mc){
		this.clan = mc;
		if(mc != null) mc.getMembres().add(this);
	}
	
	public static CYMPlayer get(Player p){
		return getOrCreate(p.getName());
	}
	public static CYMPlayer getOrCreate(String name){
		for(CYMPlayer mp : list) if(mp.name.equals(name)) return mp;
		CYMPlayer mp = new CYMPlayer(name);
		checkDB(mp);
		return mp;
	}
	public static CYMPlayer get(String name){
		for(CYMPlayer mp : list) if(mp.name.equalsIgnoreCase(name)) return mp;
		return null;
	}
	
	private static void checkDB(CYMPlayer mp) {
		int id = SQLCYMManager.getIdPlayer(mp.name);
		if(id <= 0) mp.create(); else mp.id = id;
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
	
	@Override
	public void addChecker(ICheckerClient cc){
		checkers.get(cc.getType()).add(cc);
	}
	
	@Override
	public void removeChecker(ICheckerClient cc){
		checkers.get(cc.getType()).remove(cc);
	}
	
	@Override
	public ICheckerClient getRecieveChecker(int id, int type){
		for(ICheckerClient cc : checkers.get(type))
			if(cc.getId() == id) return cc;
		return null;
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
	public void startCheckers() {
		for(List<ICheckerClient> list : checkers.values()){
			for(ICheckerClient cc : list.toArray(new ICheckerClient[0]))
				cc.start();
		}
	}
	
	private void create(CYMReputation repute) {
		SQLCYMManager.create(this, repute);
	}
	private void save(CYMReputation repute) {
		SQLCYMManager.save(this, repute);
	}
	
	public static List<CYMPlayer> getList(){
		return list;
	}
	
	public void create(){
		SQLCYMManager.create(this);
	}
	
	public void save() {
		SQLCYMManager.save(this);
	}
	
	public void del(){
		SQLCYMManager.delete(this);
	}
	
	static public CYMPlayer get(int idPlayer){
		for(CYMPlayer mp : list)
			if(mp.id == idPlayer) return mp;
		return null;
	}
	
	public void sendMessage(String msg) {
		if(player == null) player = Bukkit.getPlayer(name);
		if(player != null) player.sendMessage(msg);
	}
	
	public static void newNobody(){
		nobody = new CYMPlayer("nobody");
		nobody.id = -1;
	}
	
	public boolean isNobody(){ return id == -1; }
	
	@Override
	public boolean equals(Object obj) {
		if(this.getClass() != obj.getClass())
			return false;
		CYMPlayer o = (CYMPlayer)obj;
		return this.id == o.id;
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
