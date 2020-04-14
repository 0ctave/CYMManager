package fr.craftyourmind.manager.util;

import java.io.IOException;

import fr.craftyourmind.manager.server.Handler;
import fr.craftyourmind.manager.server.network.CYMPlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import fr.craftyourmind.manager.server.network.ICYMPacket;
import fr.craftyourmind.manager.server.network.ByteBuffer;

public abstract class CYMData implements ICYMPacket{
	
	protected CYMPlayerConnection playercnx;
	protected Player player;
	private boolean cancel = false;
	
	public CYMData() {}
	public CYMData(Player player) {
		this.player = player;
	}
	public CYMData(CYMPlayerConnection playercnx, Player player) {
		this.player = player;
		this.playercnx = playercnx;
	}
	
	public void receive(CYMPlayerConnection playercnx){
		this.playercnx = playercnx;
		this.player = playercnx.getPlayer();
		callEvent();
	}
	
	public abstract void callEvent();
	protected void callEvent(CYMEvent event){
		Bukkit.getPluginManager().callEvent(event);
	}
	public void callEvent(Player player){
		this.player = player;
		callEvent();
	}

	public void send(){
		if(!cancel && player != null){
			if(playercnx == null)
				playercnx = Handler.getPlayerConnection(player);
			
			playercnx.sendPacket(this);
		}
	}
	
	public void send(Player player){
		if(!cancel && player != null)
			Handler.getPlayerConnection(player).sendPacket(this);
	}
	
	public void send(CYMPlayerConnection playercnx){
		if(!cancel && playercnx != null)
			playercnx.sendPacket(this);
	}
	
	public CYMPlayerConnection getHandler() {
		return playercnx;
	}

	public void setHandler(CYMPlayerConnection playercnx) {
		this.playercnx = playercnx;
	}

	public void setCancelled(boolean cancel){
		this.cancel = cancel;
	}
	
	public boolean isCancelled(){
		return cancel;
	}
	
	public Player getPlayer() {
        return player;
    }
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	protected Integer[] readListInt(ByteBuffer input) throws IOException{
		int nb = input.readInt();
		Integer[] list = new Integer[nb];
		for(int i = 0 ; i < nb ; i++) list[i] = input.readInt();
		return list;
	}
	protected void writeListInt(ByteBuffer output, Integer[] list) throws IOException{
		output.writeInt(list.length);
		for(int i = 0 ; i < list.length ; i++) output.writeInt(list[i]);
	}
	
	protected Float[] readListFloat(ByteBuffer input) throws IOException{
		int nb = input.readInt();
		Float[] list = new Float[nb];
		for(int i = 0 ; i < nb ; i++) list[i] = input.readFloat();
		return list;
	}
	protected void writeListFloat(ByteBuffer output, Float[] list) throws IOException{
		output.writeInt(list.length);
		for(int i = 0 ; i < list.length ; i++) output.writeFloat(list[i]);
	}
}
