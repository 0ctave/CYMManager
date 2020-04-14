package fr.craftyourmind.manager.command;

import java.io.IOException;

import org.bukkit.entity.Player;
import fr.craftyourmind.manager.CYMTextScreen;

public class CmdTextScreen extends AbsCYMCommand {

	private static final int START = 0;
	private static final int STOP = 1;
	
	private static CmdTextScreen instance;
	
	public static void sendStart(Player p, CYMTextScreen ts){
		instance.send(p, instance.new TEXTSCREENDATA(START, ts));
	}
	
	public static void sendStop(Player p, CYMTextScreen ts){
		instance.send(p, instance.new TEXTSCREENDATA(STOP, ts));
	}
	
	public CmdTextScreen() { super(null, CMDTEXTSCREEN); instance = this; }

	@Override
	public void initChilds() { }
	@Override
	public void initActions() { }

	class TEXTSCREENDATA extends AbsCYMCommandAction{
		private int action;
		private CYMTextScreen ts;
		public TEXTSCREENDATA(int action, CYMTextScreen ts) {
			this.action = action;
			this.ts = ts;
		}
		@Override
		public int getId() { return 0; }
		@Override
		public AbsCYMCommandAction clone() { return new TEXTSCREENDATA(action, ts); }
		@Override
		public void initSend(Player p) { }
		@Override
		public void sendWrite() throws IOException {
			write(action);
			write(ts.id);
			if(action == START) write(ts.getDatas());
		}
		@Override
		public void receiveRead() throws IOException { }
		@Override
		public void receive(Player p) { }
	}
}