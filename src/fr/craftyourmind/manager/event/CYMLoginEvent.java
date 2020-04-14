package fr.craftyourmind.manager.event;

import fr.craftyourmind.manager.packet.DataLogin;
import fr.craftyourmind.manager.util.CYMEvent;


public class CYMLoginEvent extends CYMEvent {

	public DataLogin data;
	
	public CYMLoginEvent(DataLogin data) {
		this.data = data;
	}

}
