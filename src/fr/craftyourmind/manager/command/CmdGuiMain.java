package fr.craftyourmind.manager.command;

import java.io.IOException;

import org.bukkit.entity.Player;

public abstract class CmdGuiMain extends AbsCYMCommand {

	private static final int OPEN = 0;
	
	private OPEN actOPEN = new OPEN();
	
	public CmdGuiMain(int id) { super(null, id); }

	public OPEN getOpen(){ return actOPEN; }
	public void addActionOPEN(){
		actOPEN = getOpen();
		addAction(actOPEN);
	}
	
	public void sendOpenMain(Player p, int idSelectedMain, int... ids){
		if(p.hasPermission(permission))
			send(p, actOPEN.clone().init(idSelectedMain, ids));
	}
	@Override
	public void initActions() { addActionOPEN(); }
	// ---- OPEN ----
	class OPEN extends AbsCYMCommandAction{
		protected int idSelectedMain = 0;
		protected int[] ids;
		public OPEN init(int idSelectedMain, int... ids){ this.idSelectedMain = idSelectedMain; this.ids = ids; return this; }
		@Override
		public int getId() { return OPEN; }
		@Override
		public OPEN clone() { return new OPEN(); }
		@Override
		public void initSend(Player p) { }
		@Override
		public void sendWrite() throws IOException { write(idSelectedMain); write(ids == null ? new int[0] : ids); }
		@Override
		public void receiveRead() throws IOException { }
		@Override
		public void receive(Player p) { }
	}
}