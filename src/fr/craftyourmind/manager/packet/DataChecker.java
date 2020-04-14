package fr.craftyourmind.manager.packet;

import java.io.IOException;

import fr.craftyourmind.manager.CYMChecker;
import fr.craftyourmind.manager.CYMChecker.ICheckerClient;
import fr.craftyourmind.manager.server.network.ByteBuffer;
import fr.craftyourmind.manager.util.CYMData;

public class DataChecker extends CYMData {

	public final static int START = 0;
	public final static int STOP = 1;
	public final static int TICK = 2;
	
	public int action;
	public ICheckerClient cc;
	public int id;
	public int type;
	public String params;
	
	public DataChecker() {}
	public DataChecker(int action, ICheckerClient cc) {
		this.action = action;
		this.cc = cc;
	}
	
	@Override
	public void readPacketData(ByteBuffer input) throws IOException {
		action = input.readInt();
		id = input.readInt();
		type = input.readInt();
		params = input.readStr();
	}
	@Override
	public void writePacketData(ByteBuffer output) throws IOException {
		output.writeInt(action);
		if(action == START){
			output.writeInt(cc.getId());
			output.writeInt(cc.getType());
			output.writeStr(cc.getParams());
		}else if(action == STOP){
			output.writeInt(cc.getId());
		}
	}

	@Override
	public void callEvent() {
		CYMChecker.receive(this);
	}
	
	private static int typedata = 0;
	public int getTypedata() {
		return typedata;
	}
	public void setTypedata(int typedata) {
		this.typedata = typedata;
	}
}
