package fr.craftyourmind.manager.packet;

import java.io.IOException;
import java.util.Map.Entry;

import fr.craftyourmind.manager.client.network.ICYMPacket;
import fr.craftyourmind.manager.event.CYMLoginEvent;
import fr.craftyourmind.manager.server.network.ByteBuffer;
import fr.craftyourmind.manager.util.CYMData;
import fr.craftyourmind.manager.util.CYMHandlerUtil;

public class DataLogin extends CYMData {

	public DataLogin() {}
	
	public void readPacketData(ByteBuffer input) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void writePacketData(ByteBuffer output) throws IOException {
		output.writeInt(CYMHandlerUtil.getDatasClient().size());
		for(Entry<Integer, Class<? extends ICYMPacket>> data : CYMHandlerUtil.getDatasClient().entrySet()){
			output.writeStr(data.getValue().getName());
			output.writeInt(data.getKey());
		}
	}

	@Override
	public void callEvent() {
		send();
		callEvent(new CYMLoginEvent(this));
	}

	private static int typedata = 1;
	public int getTypedata() {
		return typedata;
	}
	public void setTypedata(int typedata) {
		this.typedata = typedata;
	}
}
