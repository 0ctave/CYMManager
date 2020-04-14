package fr.craftyourmind.manager.packet;

import java.io.IOException;

import fr.craftyourmind.manager.server.network.ByteBuffer;
import fr.craftyourmind.manager.util.CYMData;

public class DataAlert extends CYMData {
	
	private String title;
	private String message;
	private String idItem;
	
	
	public DataAlert() {}
	public DataAlert(String title, String message, String idItem) {
		this.title = title;
		this.message = message;
		this.idItem = idItem;
	}
	
	public void readPacketData(ByteBuffer input) throws IOException {
		
	}

	public void writePacketData(ByteBuffer output) throws IOException {
		output.writeStr(title);
		output.writeStr(message);
		output.writeStr(idItem);
	}

	@Override
	public void callEvent() {
		// TODO Auto-generated method stub
		
	}
	
	private static int typedata = 0;
	public int getTypedata() {
		return typedata;
	}
	public void setTypedata(int typedata) {
		this.typedata = typedata;
	}
}
