package fr.craftyourmind.manager.command;

import java.io.IOException;

import org.bukkit.entity.Player;
import fr.craftyourmind.manager.server.network.ByteBuffer;
public interface ICYMCommandData {

	public int getId();
	public void init();
	public void initSend(Player p);
	public ICYMCommandData readPacketData(ByteBuffer input) throws IOException;
	public void writePacketData(ByteBuffer output) throws IOException;
	public void receive(Player p);
	
}
