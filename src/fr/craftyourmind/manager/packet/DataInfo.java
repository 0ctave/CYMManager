package fr.craftyourmind.manager.packet;

import java.io.IOException;

import fr.craftyourmind.manager.server.network.ByteBuffer;
import fr.craftyourmind.manager.util.CYMData;

public class DataInfo extends CYMData {
	public final static String DELIMITER = "";
	
	public final static int WORLD = 0;
	
	public int action;
	public String params;
	
	public DataInfo() {}
	public DataInfo(int action, String params) {
		this.action = action;
		this.params = params;
	}
	
	@Override
	public void readPacketData(ByteBuffer input) throws IOException {
		action = input.readInt();
		params = input.readStr();
	}

	@Override
	public void writePacketData(ByteBuffer output) throws IOException {
		output.writeInt(action);
		output.writeStr(params);
	}

	@Override
	public void callEvent() {
		
	}

	private static int typedata = 0;
	public int getTypedata() {
		return typedata;
	}
	public void setTypedata(int typedata) {
		this.typedata = typedata;
	}
}
