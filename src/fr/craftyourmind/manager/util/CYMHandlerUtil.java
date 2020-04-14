package fr.craftyourmind.manager.util;

import java.util.HashMap;
import java.util.Map;

import fr.craftyourmind.manager.packet.DataAlert;
import fr.craftyourmind.manager.packet.DataChecker;
import fr.craftyourmind.manager.packet.DataCYMCommand;
import fr.craftyourmind.manager.packet.DataInfo;
import fr.craftyourmind.manager.packet.DataLogin;
import fr.craftyourmind.manager.packet.DataNPC;

import fr.craftyourmind.manager.server.Handler;
import fr.craftyourmind.manager.server.network.ICYMPacket;

public class CYMHandlerUtil extends Handler {
	
	private static Map<Integer, Class<? extends fr.craftyourmind.manager.server.network.ICYMPacket>> datas = new HashMap<Integer, Class<? extends fr.craftyourmind.manager.server.network.ICYMPacket>>();
	private static Map<Integer, Class<? extends fr.craftyourmind.manager.client.network.ICYMPacket>> datasClient = new HashMap<Integer, Class<? extends fr.craftyourmind.manager.client.network.ICYMPacket>>();
	
	public static void init() {
		instance = new CYMHandlerUtil();

		addDataLogin(DataLogin.class, fr.craftyourmind.manager.client.network.packet.DataLogin.class);
		addData(DataAlert.class, fr.craftyourmind.manager.client.network.packet.DataAlert.class);
		addData(DataNPC.class, fr.craftyourmind.manager.client.network.packet.DataNPC.class);
		addData(DataChecker.class, fr.craftyourmind.manager.client.network.packet.DataChecker.class);
		addData(DataInfo.class, fr.craftyourmind.manager.client.network.packet.DataInfo.class);
		addData(DataCYMCommand.class, fr.craftyourmind.manager.client.network.packet.DataCYMCommand.class);
	}
	
	public static void addDataLogin(Class<? extends fr.craftyourmind.manager.server.network.ICYMPacket> pck, Class<? extends fr.craftyourmind.manager.client.network.ICYMPacket> pckClient){
		datas.put(1, pck);
		datasClient.put(1, pckClient);
	}
	
	public static void addData(Class<? extends fr.craftyourmind.manager.server.network.ICYMPacket> pck, Class<? extends fr.craftyourmind.manager.client.network.ICYMPacket> pckClient){
		int i = 2;
		while(datas.containsKey(i))
			i++;
		datas.put(i, pck);
		datasClient.put(i, pckClient);
		try {
			((ICYMPacket)pck.newInstance()).setTypedata(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Map<Integer, Class<? extends fr.craftyourmind.manager.client.network.ICYMPacket>> getDatasClient(){
		return datasClient;
	}
	
	@Override
	public ICYMPacket newdata(int type) {
		
		if(datas.containsKey(type)){
			try {
				return (ICYMPacket) datas.get(type).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
