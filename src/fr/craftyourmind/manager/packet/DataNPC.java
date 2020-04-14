package fr.craftyourmind.manager.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import fr.craftyourmind.manager.CYMNPC;
import fr.craftyourmind.manager.server.network.ByteBuffer;
import fr.craftyourmind.manager.util.CYMData;

public class DataNPC extends CYMData {

	public static final int LOGIN = 0;
	public static final int TEXT = 1;
	public static final int TEXTCOMMIT = 2;
	public static final int REFRESHSKIN = 3;
	public static final int CREATENPC = 4;
	
	public List<Integer> identities = new ArrayList<Integer>();
	public List<String> names = new ArrayList<String>();
	public List<Integer> idNpc = new ArrayList<Integer>();
	public List<String> skins = new ArrayList<String>();
	public List<Integer> distanceDrawIcons = new ArrayList<Integer>();
	
	public int action;
	public int npc;
	public String name = "";
	public String text = "";
	public String urlSkin = "";
	public int distanceDrawIcon;
	
	public DataNPC() {}
	public DataNPC(Player p) { this.player = p;}
	public DataNPC(int action) {
		this.action = action;
	}
	public DataNPC(int action, int npc, String text, String urlSkin, int distanceDrawIcon) {
		this.action = action;
		this.npc = npc;
		this.text = text;
		this.urlSkin = urlSkin;
		this.distanceDrawIcon = distanceDrawIcon;
	}
	
	public void sendRefreshNpc(int idnpc, String urlSkin, Integer distanceDrawIcon){
		action = REFRESHSKIN; this.npc = idnpc; this.urlSkin = urlSkin; this.distanceDrawIcon = distanceDrawIcon; send();
	}
	
	public void readPacketData(ByteBuffer input) throws IOException {
		action = input.readInt();
		npc = input.readInt();
		if(action == TEXTCOMMIT){
			text = input.readStr();
			urlSkin = input.readStr();
			distanceDrawIcon = input.readInt();
		}
	}

	public void writePacketData(ByteBuffer output) throws IOException {
		output.writeInt(action);
		if(action == LOGIN){
			output.writeListInt(identities);
			output.writeListStr(names);
			output.writeListInt(idNpc);
			output.writeListStr(skins);
			output.writeListInt(distanceDrawIcons);
		}else if(action == TEXT){
			output.writeInt(npc);
			output.writeStr(text);
			output.writeStr(urlSkin);
			output.writeInt(distanceDrawIcon);
		}else if(action == REFRESHSKIN){
			output.writeInt(npc);
			output.writeStr(urlSkin);
			output.writeInt(distanceDrawIcon);
		}else if(action == CREATENPC){
			output.writeInt(npc);
			output.writeStr(name);
			output.writeStr(urlSkin);
			output.writeInt(distanceDrawIcon);
		}
	}

	@Override
	public void callEvent() {
		CYMNPC.receive(this);
	}

	private static int typedata = 0;
	public int getTypedata() {
		return typedata;
	}
	public void setTypedata(int typedata) {
		this.typedata = typedata;
	}
}
