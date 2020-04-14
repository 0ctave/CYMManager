package fr.craftyourmind.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import fr.craftyourmind.manager.packet.DataNPC;
import fr.craftyourmind.manager.sql.SQLCYMManager;

public class CYMNPC {

	private static List<CYMNPC> list = new ArrayList<CYMNPC>();
	private static List<CYMNPC> listBlock = new ArrayList<CYMNPC>();
	
	public int id = -1;
	public String name = "";
	public String urlSkin = "";
	public boolean isBlock = false;
	public World world;
	public int x = 0;
	public int y = 0;
	public int z = 0;
	public String text = "";
	public int distanceDrawIcon = 16;
	
	private Entity entity;
	
	public CYMNPC(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public CYMNPC(Boolean isBlock, int id, String name, Location loc) {
		this.id = id;
		this.name = name;
		this.isBlock = isBlock;
		world = loc.getWorld();
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		if(!isBlock) add(this);
	}

	public static void add(CYMNPC npc){
		list.add(npc);
	}
	
	public static List<CYMNPC> get(){
		return list;
	}
	
	public static CYMNPC get(int id){
		for(CYMNPC npc : list)
			if(npc.id == id) return npc;
		return null;
	}
	
	public static CYMNPC getBlock(int id){
		for(CYMNPC npc : listBlock)
			if(npc.id == id) return npc;
		return null;
	}
	
	public static String getText(int id){
		CYMNPC npc = get(id);
		if(npc == null) return "";
		return npc.text;
	}
	
	public static void remove(int id) {
		CYMNPC npcRemove = null;
		for(CYMNPC npc : list){
			if(npc.id == id){
				npcRemove = npc;
				break;
			}
		}
		if(npcRemove != null) npcRemove.delete();
	}
	
	public static void synchroCitizens(Map<Integer, String> map){
		for(Entry<Integer, String> entry : map.entrySet()){
			CYMNPC npc = get(entry.getKey());
			if(npc == null){
				npc = getBlock(entry.getKey());
				if(npc == null){
					npc = new CYMNPC(entry.getKey(), entry.getValue());
					npc.create();
					add(npc);
				}
			}
		}
	}
	
	public int getIdEntity() {
		if(entity == null || (entity.isDead() || !entity.isValid())) entity = CYMManager.getPlayerNPC(id);
		else return entity.getEntityId();
		if(entity == null) return 0; else return entity.getEntityId();
	}
	
	public boolean clickon(Location loc){
		if(isBlock && world != null)
			return world.getUID() == loc.getWorld().getUID() && x == loc.getBlockX() && y == loc.getBlockY() && z == loc.getBlockZ();
		return false;
	}
	
	public void addBlockNpcMeta(){
		listBlock.add(this);
		if(world != null){
			Block b = world.getBlockAt(x, y, z);
			b.setMetadata("blocknpc", new MetaNpc());
		}
	}
	
	public void removeBlockNpcMeta(){
		listBlock.remove(this);
		if(world != null){
			Block b = world.getBlockAt(x, y, z);
			b.removeMetadata("blocknpc", Plugin.it);
		}
	}
	
	public void moveNpcBlock(Block b){
		removeBlockNpcMeta();
		world = b.getWorld();
		x = b.getX();
		y = b.getY();
		z = b.getZ();
		addBlockNpcMeta();
		save();
	}
	
	public void npcToBlock(Block b){
		//list.remove(this);
		isBlock = true;
		world = b.getWorld();
		x = b.getX();
		y = b.getY();
		z = b.getZ();
		addBlockNpcMeta();
		save();
	}
	
	public void blockToNpc(){
		removeBlockNpcMeta();
		isBlock = false;
		save();
		//list.add(this);
	}
	
	public static boolean isBlockNpc(Block b){
		return b.hasMetadata("blocknpc");
	}
	
	public static CYMNPC get(Block b){
		if(b.hasMetadata("blocknpc")){
			List<MetadataValue> metas = b.getMetadata("blocknpc");
			if(metas.isEmpty()) return null;
			return (CYMNPC) metas.get(0).value();
		}
		return null;
	}
	public void createBlockNpc() {
		addBlockNpcMeta();
		SQLCYMManager.create(this);
	}
	public void create() {
		SQLCYMManager.create(this);
	}
	
	public void save() {
		SQLCYMManager.save(this);
	}
	
	public void delete() {
		list.remove(this);
		if(isBlock) removeBlockNpcMeta();
		SQLCYMManager.delete(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this.getClass() != obj.getClass())
			return false;
		CYMNPC o = (CYMNPC)obj;
		return this.id == o.id;
	}
	
	class MetaNpc implements MetadataValue{
		@Override
		public boolean asBoolean() { return false; }
		@Override
		public byte asByte() { return 0; }
		@Override
		public double asDouble() { return 0; }
		@Override
		public float asFloat() { return 0; }
		@Override
		public int asInt() { return id; }
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
		public Object value() { return CYMNPC.this; }
	}
	
	public static void receive(DataNPC data){
		CYMNPC npc = get(data.npc);
		if(npc != null){
			if(data.action == DataNPC.TEXT){
				new DataNPC(DataNPC.TEXT, npc.id, npc.text, npc.urlSkin, npc.distanceDrawIcon).send(data.getPlayer());
			}else if(data.action == DataNPC.TEXTCOMMIT){
				npc.text = data.text;
				if(npc.distanceDrawIcon != data.distanceDrawIcon || !npc.urlSkin.equals(data.urlSkin)){
					npc.urlSkin = data.urlSkin;
					npc.distanceDrawIcon = data.distanceDrawIcon;
					for(Player p : Bukkit.getOnlinePlayers()) new DataNPC(p).sendRefreshNpc(npc.id, npc.urlSkin, npc.distanceDrawIcon);
				}
				npc.save();
			}
		}
	}
	
	public static void sendLogin(Player p){
		DataNPC datanpc = new DataNPC(DataNPC.LOGIN);
		for(CYMNPC npc : CYMNPC.get()){
			datanpc.identities.add(npc.getIdEntity());
			datanpc.names.add(npc.name);
			datanpc.idNpc.add(npc.id);
			datanpc.skins.add(npc.urlSkin);
			datanpc.distanceDrawIcons.add(npc.distanceDrawIcon);
		}
		datanpc.send(p);
	}
	
	public static void sendCreateNpc(CYMNPC npc){
		for(Player p : Bukkit.getOnlinePlayers()){
			DataNPC datanpc = new DataNPC(DataNPC.CREATENPC);
			datanpc.npc = npc.id;
			datanpc.name = npc.name;
			datanpc.urlSkin = npc.urlSkin;
			datanpc.distanceDrawIcon = npc.distanceDrawIcon;
			datanpc.send(p);
		}
	}
}