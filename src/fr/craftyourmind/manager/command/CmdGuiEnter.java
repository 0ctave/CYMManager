package fr.craftyourmind.manager.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public abstract class CmdGuiEnter extends AbsCYMCommand {

	private static final int OPEN = 0;
	private static final int SELECT = 1;
	private static final int SAVE = 2;
	private static final int DELETE = 3;
	private static final int SORT = 5;
	
	private OPEN actOPEN = new OPEN();
	private AbsCYMCommandAction actSELECT = new SELECT();
	private AbsCYMCommandAction actSAVE = new SAVE();
	private AbsCYMCommandAction actDELETE = new DELETE();
	private AbsCYMCommandAction actSORT = new SORT();
	
	public CmdGuiEnter(AbsCYMCommand parent, int id) { super(parent, id); }

	public void sendOpen(Player p, int... ids){ send(p, actOPEN.clone().init(ids)); }
	public void sendSelect(Player p){ send(p, actSELECT.clone()); }
	public void sendSave(Player p){ send(p, actSAVE.clone()); }
	public void sendDelete(Player p){ send(p, actDELETE.clone()); }
	public void sendSort(Player p){ send(p, actSORT.clone()); }
	
	public OPEN getOpen(){ return actOPEN; }
	public AbsCYMCommandAction getSelect(){ return actSELECT; }
	public AbsCYMCommandAction getSave(){ return actSAVE; }
	public AbsCYMCommandAction getDelete(){ return actDELETE; }
	public AbsCYMCommandAction getSort(){ return actSORT; }
	
	public void addActionOPEN(){ actOPEN = getOpen(); addAction(actOPEN); }
	public void addActionSELECT(){ actSELECT = getSelect(); addAction(actSELECT); }
	public void addActionOSAVE(){ actSAVE = getSave(); addAction(actSAVE); }
	public void addActionDELETE(){ actDELETE = getDelete(); addAction(actDELETE); }
	public void addActionSort(){ actSORT = getSort(); addAction(actSORT); }
	
	@Override
	public void initActions() { addActionOPEN(); addActionSELECT(); addActionOSAVE(); addActionDELETE(); addActionSort(); }
	
	public abstract void initSendOpen(List<Integer> idlists, List<String> namelists, List<Integer> orderlists, int... ids);
	public abstract void receiveDelete(int... ids);
	public abstract void receiveSort(boolean order, int... ids);
	public void actionSendOpen(Player p, int... ids){ sendOpen(p, ids); }
	
	public abstract ICmdData getCmdData();
	public interface ICmdData{
		public void initSend(int... ids);
		public void sendWrite(AbsCYMCommandAction cmd) throws IOException;
		public void receiveRead(AbsCYMCommandAction cmd) throws IOException;
		public int[] receive();
	}
	
	// ---- OPEN ----
	public class OPEN extends AbsCYMCommandAction{
		protected int[] ids;
		protected List<Integer> idlists = new ArrayList<Integer>();
		protected List<String> namelists = new ArrayList<String>();
		protected List<Integer> orderlists = new ArrayList<Integer>();
		public OPEN init(int... ids){ this.ids = ids; return this; }
		@Override
		public int getId() { return OPEN; }
		@Override
		public OPEN clone() { return new OPEN(); }
		@Override
		public void initSend(Player p) { initSendOpen(idlists, namelists, orderlists, ids); }
		@Override
		public void sendWrite() throws IOException { writeList(idlists, namelists); writeListInt(orderlists); write(ids); }
		@Override
		public void receiveRead() throws IOException { ids = readListInt(); }
		@Override
		public void receive(Player p) { if(p.hasPermission(permission)) send(p, this); }
	}
	// ---- SELECT ----
	public class SELECT extends AbsCYMCommandAction{
		protected int[] ids;
		protected ICmdData cd;
		public SELECT init(int... ids){ this.ids = ids; return this; }
		@Override
		public int getId() { return SELECT; }
		@Override
		public AbsCYMCommandAction clone() { return new SELECT(); }
		@Override
		public void initSend(Player p) { cd = getCmdData(); cd.initSend(ids); }
		@Override
		public void sendWrite() throws IOException { cd.sendWrite(this); }
		@Override
		public void receiveRead() throws IOException { ids = readListInt(); }
		@Override
		public void receive(Player p) { send(p, this); }
	}
	// ---- SAVE ----
	public class SAVE extends AbsCYMCommandAction{
		protected ICmdData cd;
		@Override
		public int getId() { return SAVE; }
		@Override
		public AbsCYMCommandAction clone() { return new SAVE(); }
		@Override
		public void initSend(Player p) {  }
		@Override
		public void sendWrite() throws IOException { }
		@Override
		public void receiveRead() throws IOException { cd = getCmdData(); cd.receiveRead(this); }
		@Override
		public void receive(Player p) {
			int[] ids = new int[0];
			if(p.hasPermission(permission)) ids = cd.receive();
			actionSendOpen(p, ids);
		}
	}
	// ---- DELETE ----
	public class DELETE extends AbsCYMCommandAction{
		protected int[] ids;
		@Override
		public int getId() { return DELETE; }
		@Override
		public AbsCYMCommandAction clone() { return new DELETE(); }
		@Override
		public void initSend(Player p) { }
		@Override
		public void sendWrite() throws IOException { }
		@Override
		public void receiveRead() throws IOException { ids = readListInt(); }
		@Override
		public void receive(Player p) {
			if(p.hasPermission(permission)) receiveDelete(ids);
			actionSendOpen(p, ids);
		}
	}
	// ---- SORT ----
	public class SORT extends AbsCYMCommandAction{
		protected int[] ids;
		protected boolean order;
		@Override
		public int getId() { return SORT; }
		@Override
		public AbsCYMCommandAction clone() { return new SORT(); }
		@Override
		public void initSend(Player p) { }
		@Override
		public void sendWrite() throws IOException { }
		@Override
		public void receiveRead() throws IOException { ids = readListInt(); order = readBool(); }
		@Override
		public void receive(Player p) {
			if(p.hasPermission(permission)) receiveSort(order, ids);
			actionSendOpen(p, ids);
		}
	}
}