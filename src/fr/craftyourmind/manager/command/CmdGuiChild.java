package fr.craftyourmind.manager.command;

import java.util.List;

import org.bukkit.entity.Player;

public abstract class CmdGuiChild  extends CmdGuiEnter{
	
	private static final int OPENCHILD = 4;
	
	public CmdGuiChild(AbsCYMCommand parent, int id) { super(parent, id); }
	@Override
	public void initActions() { super.initActions(); addAction(new OPENCHILD()); }
	
	@Override
	public void actionSendOpen(Player p, int... ids) { send(p, new OPENCHILD().init(ids)); }
	
	// ----- OPENCHILD -----
	public abstract void initSendOpenChild(List<Integer> idchilds, List<String> namechilds, List<Integer> orderlists, int... ids);
	public class OPENCHILD extends OPEN{
		@Override
		public int getId() { return OPENCHILD; }
		@Override
		public OPENCHILD clone() { return new OPENCHILD(); }
		@Override
		public void initSend(Player p) { initSendOpenChild(idlists, namelists, orderlists, ids); }
	}
}