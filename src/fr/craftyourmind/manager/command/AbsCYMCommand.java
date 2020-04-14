package fr.craftyourmind.manager.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import fr.craftyourmind.manager.packet.DataCYMCommand;
import fr.craftyourmind.manager.server.network.ByteBuffer;
public abstract class AbsCYMCommand implements ICYMCommandData {
	
	public static final int CMDPARTICLE = 1;
	//public static final int CMDMEKABOX = 2;
	//public static final int CMDCHUNKPOSITION = 4;
	//public static final int CMDSKILLS = 5;
	//public static final int CMDGUISKILLS = 6;
	public static final int CMDTEXTSCREEN = 7;
	//public static final int CMDQUEST = 8;
	
	private static Map<Integer, AbsCYMCommand> types = new HashMap<Integer, AbsCYMCommand>();
	public static void add(AbsCYMCommand c){
		types.put(c.id, c);
		c.init();
	}
	public static AbsCYMCommand get(int id){ return types.get(id); }
	
	// ---------------------------------------------------------------------------------------------------
	private int id;
	private boolean isCmdAct = false;
	protected AbsCYMCommand parent;
	protected List<AbsCYMCommand> childs = new ArrayList<AbsCYMCommand>();
	protected List<AbsCYMCommandAction> cmdDatas = new ArrayList<AbsCYMCommandAction>();
	protected String permission = "";
	
	public AbsCYMCommand(int id) { this(null, id); }
	public AbsCYMCommand(AbsCYMCommand parent, int id) {
		this.parent = parent;
		this.id = id;
	}
	
	public int getId(){ return id; }
	@Override
	public void init(){
		initChilds();
		initActions();
		for(AbsCYMCommand child : childs) child.init();
	}
	public void addChild(AbsCYMCommand cmdgui){ childs.add(cmdgui); cmdgui.permission = permission; }
	public AbsCYMCommand getChild(int idChild){ for(AbsCYMCommand child : childs) if(child.getId() == idChild) return child; return null; };
	
	public abstract void initChilds();
	public abstract void initActions();
	
	public void addAction(AbsCYMCommandAction a){ cmdDatas.add(a); }
	public void addAction(AbsCYMCommandAction... actions){ cmdDatas.addAll(Arrays.asList(actions)); }
	public AbsCYMCommandAction getAction(int idAct){ for(AbsCYMCommandAction a : cmdDatas) if(a.getId() == idAct) return a; return null; };
	
	public void send(Player p, int idAct){ send(p, getAction(idAct).clone()); }
	public void send(Player p, ICYMCommandData cmdData){ cmdData.initSend(p); new DataCYMCommand(cmdData).send(p); }
	
	public ICYMCommandData readPacketData(ByteBuffer input) throws IOException{
		boolean isCmdActInput = input.readBoolean();
		if(isCmdActInput){
			int action = input.readInt();
			AbsCYMCommandAction cmddata = getAction(action);
			if(cmddata != null) {
				cmddata = cmddata.clone();
				return cmddata.readPacketData(input);
			}
		}else{
			int type = input.readInt();
			AbsCYMCommand child = getChild(type);
			if(child != null)
				return child.readPacketData(input);
		}
		return this;
	}
	public void writePacketData(ByteBuffer output) throws IOException{
		if(parent != null){
			parent.writePacketData(output);
			output.writeBoolean(isCmdAct);
		}
		output.writeInt(id);
	}
	@Override
	public void initSend(Player p) { }
	@Override
	public void receive(Player p) { }
	
	public abstract class AbsCYMCommandAction implements ICYMCommandData {
		private boolean isCmdAct = true;
		private ByteBuffer input;
		private ByteBuffer output;
		public abstract AbsCYMCommandAction clone();
		public abstract void sendWrite() throws IOException ;
		public abstract void receiveRead() throws IOException ;
		@Override
		public ICYMCommandData readPacketData(ByteBuffer input) throws IOException {
			this.input = input;
			receiveRead();
			return this;
		}
		@Override
		public void writePacketData(ByteBuffer output) throws IOException {
			this.output = output;
			AbsCYMCommand.this.writePacketData(output);
			write(isCmdAct);
			write(getId());
			sendWrite();
		}
		@Override
		public void init() { }
		public int readInt() throws IOException { return input.readInt(); }
		public String readStr() throws IOException { return input.readStr(); }
		public boolean readBool() throws IOException { return input.readBoolean(); }
		public double readDouble() throws IOException { return input.readDouble(); }
		public float readFloat() throws IOException { return input.readFloat(); }
		public void write(int i) throws IOException { output.writeInt(i); }
		public void write(String s) throws IOException { output.writeStr(s); }
		public void write(boolean b) throws IOException { output.writeBoolean(b); }
		public void write(double d) throws IOException { output.writeDouble(d); }
		public void write(float f) throws IOException { output.writeFloat(f); }
		public int[] readListInt() throws IOException { return readListInt(input); }
		public void readList(List<Integer> listInt, List<String> listStr) throws IOException { input.readList(listInt, listStr); }
		public void readListBool(List<Boolean> listBool) throws IOException { input.readListBool(listBool); }
		public void readListInt(List<Integer> listInt) throws IOException { input.readListInt(listInt); }
		public void readListStr(List<String> listStr) throws IOException { input.readListStr(listStr); }
		public void write(int[] listInt) throws IOException{ writeListInt(output, listInt); }
		public void writeList(List<Integer> listInt, List<String> listStr) throws IOException { output.writeList(listInt, listStr); }
		public void writeListStr(List<String> listStr) throws IOException { output.writeListStr(listStr); }
		public void writeListInt(List<Integer> listInt) throws IOException { output.writeListInt(listInt); }
		public void writeListBool(List<Boolean> listBool) throws IOException { output.writeListBool(listBool); }
		
		private int[] readListInt(ByteBuffer input) throws IOException{
			int nb = input.readInt();
			int[] listInt = new int[nb];
			for(int i = 0 ; i < nb ; i++) listInt[i] = input.readInt();
			return listInt;
		}
		
		private void writeListInt(ByteBuffer output, int[] listInt) throws IOException{
			output.writeInt(listInt.length);
			for(int i = 0 ; i < listInt.length ; i++) output.writeInt(listInt[i]);
		}
	}
	// --------------------------------------------------------------------------------------------------------------------------
}