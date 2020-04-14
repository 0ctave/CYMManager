package fr.craftyourmind.manager;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.MetadataValue;
import fr.craftyourmind.manager.checker.BlockEvent;
import fr.craftyourmind.manager.checker.Damage;
import fr.craftyourmind.manager.checker.Inventory;
import fr.craftyourmind.manager.checker.Kill;
import fr.craftyourmind.manager.checker.Position;
import fr.craftyourmind.manager.checker.PositionMeta;
import fr.craftyourmind.manager.checker.TextScreen;
import fr.craftyourmind.manager.checker.UseItem;
import fr.craftyourmind.manager.packet.DataChecker;
import fr.craftyourmind.manager.util.IChecker;

public class CYMChecker {
	
	// Type client
	public final static int POSITION = 0;
	public final static int BLOCKEVENT = 1;
	public final static int KILL = 2;
	public final static int INVENTORY = 3;
	public final static int USEITEM = 4;
	public final static int DAMAGE = 5;
	public final static int TEXTSCREEN = 6;
	
	public CYMChecker() {}
	
	public static Position startPosition(ICheckerPlayer p, ICheckerPosition cp){
		Position pos = new Position(p, cp);
		pos.start();
		return pos;
	}
	
	public static PositionMeta startPositionMeta(ICheckerEntity e, ICheckerPosition cp){
		PositionMeta pos = new PositionMeta(e, cp);
		pos.start();
		return pos;
	}
	
	public static BlockEvent startBlockEvent(ICheckerPlayer p, ICheckerBlockEvent cb){
		BlockEvent be = new BlockEvent(p, cb);
		be.start();
		return be;
	}
	
	public static Kill startKill(ICheckerEntity e, ICheckerKill ck){
		Kill k = new Kill(e, ck);
		k.start();
		return k;
	}
	
	public static Inventory startInventory(ICheckerPlayer p, ICheckerInventory ci){
		Inventory i = new Inventory(p, ci);
		i.start();
		return i;
	}
	
	public static UseItem startUseItem(ICheckerPlayer p, ICheckerUseItem cui){
		UseItem ui = new UseItem(p, cui);
		ui.start();
		return ui;
	}
	
	public static Damage startDamage(ICheckerPlayer p, ICheckerDamage cd){
		Damage d = new Damage(p, cd);
		d.start();
		return d;
	}
	
	public static TextScreen startTextScreen(ICheckerPlayer p, ICheckerTextScreen cts){
		TextScreen ts = new TextScreen(p, cts);
		ts.start();
		return ts;
	}
	
	public static void initChecker(CYMPlayer mp, Player p){
		p.removeMetadata("miniEntity", Plugin.it);
		p.setMetadata("miniEntity", mp);
	}
	
	public static ICheckerEntity getOrNewCheckerEntityMeta(Entity e) {
		ICheckerEntity ce = getCheckerEntityMeta(e);
		if(ce == null){
			if(e.getType() == EntityType.PLAYER) return null;
			ce = new CYMEntity(e);
			e.setMetadata("miniEntity", ce);
			return ce;
		}
		return ce;
	}
	
	public static ICheckerEntity getCheckerEntityMeta(Entity e) {
		List<MetadataValue> metas = e.getMetadata("miniEntity");
		if(metas.isEmpty()) return null;
		return (ICheckerEntity) metas.get(0);
	}
	
	public static void receive(DataChecker data){
		ICheckerPlayer cp = (ICheckerPlayer) getCheckerEntityMeta(data.getPlayer());
		if(cp != null){
			ICheckerClient cc = cp.getRecieveChecker(data.id, data.type);
			if(cc != null){
				cc.loadParams(data.params);
				cc.tick();
			}
		}
	}
	
	// ------------------------------------------
	public interface ICheckerController extends IChecker{
		public int getId();
	}
	
	public interface ICheckerClient {
		public int getId();
		public ICheckerController getChecker();
		public void start();
		public void stop();
		public void tick();
		public int getType();
		public String getParams();
		public void loadParams(String params);
		public void loadParams(String[] params);
	}
	public interface ICheckerEntity extends MetadataValue{
		public int getIdIncrementChecker();
		public Entity getEntity();
		public void addChecker(ICheckerClient cc);
		public void removeChecker(ICheckerClient cc);
		public void startCheckers();
		public List<ICheckerClient> getCheckerKill();
		public List<ICheckerClient> getCheckerDamage();
	}
	public interface ICheckerPlayer extends ICheckerEntity{
		public Player getPlayer();
		public ICheckerClient getRecieveChecker(int id, int type);
	}

	public interface ICheckerPosition extends ICheckerController{
		public void inside();
		public void outside();
		public String getWorld();
		public int getX();
		public int getY();
		public int getZ();
		public int getRadius();
	}
	public interface ICheckerBlockEvent extends ICheckerController{
		public void event();
		public int getTypeEvent();
		public String getIdMat();
		public int getData();
		public boolean getCancelled();
		public String getWorld();
		public int getX();
		public int getY();
		public int getZ();
	}
	public interface ICheckerKill extends ICheckerController{
		public void setKiller(LivingEntity killer);
		public void setVictim(LivingEntity victim);
	}
	public interface ICheckerInventory extends ICheckerController{
		
	}
	public interface ICheckerUseItem extends ICheckerController{
		public String getIdItem();
		public int getIdData();
		public String getNameItem();
		public int getActionClick();
		public boolean isSlotSkill();
		public boolean isCancelMcEvent();
		public void setSlotSelected(int slotSelected);
	}
	public interface ICheckerDamage extends ICheckerController{
		public void setEvent(EntityDamageEvent event);
		public void setRecieved(boolean recieved);
		public void setGiven(boolean given);
		public void setVictim(Entity victim);
		public void setDamager(Entity damager);
	}
	public interface ICheckerTextScreen extends ICheckerController{
		public CYMTextScreen getTextScreen();
		
	}	
}