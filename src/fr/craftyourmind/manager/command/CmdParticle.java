package fr.craftyourmind.manager.command;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftFirework;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;

public class CmdParticle extends AbsCYMCommand {
	
	private static CmdParticle instance;
	
	public CmdParticle() { super(CMDPARTICLE); instance = this; }

	public static void send(Player p, Location loc, String typeParticle, double velX, double velY, double velZ, boolean cloud, boolean useVelocity, int nbParticule, float width, float height, float length, int idMat, int dataMat){
		instance.send(p, instance.new PARTICLEDATA(loc, typeParticle, velX, velY, velZ, cloud, useVelocity, nbParticule, width, height, length, idMat, dataMat));
	}
	
	public static void fireworkExplosion(Location loc, Firework fireWork){
		//fireWork.remove();
		//fireWork.detonate();
		((CraftFirework)fireWork).getHandle().expectedLifespan = 1;
		//WorldServer world = ((CraftWorld)loc.getWorld()).getHandle();
        //world.broadcastEntityEffect(((CraftFirework)fireWork).getHandle(), (byte) 17);
	}
	@Override
	public void initChilds() { }
	@Override
	public void initActions() { }
	
	class PARTICLEDATA extends AbsCYMCommandAction{
		private Location loc;
		public String typeParticle = "";
		public double velX, velY, velZ;
		public boolean cloud, useVelocity;
		public int nbParticule, idMat, dataMat;
		public float width, height, length;
		public PARTICLEDATA(Location loc, String typeParticle, double velX, double velY, double velZ, boolean cloud, boolean useVelocity, int nbParticule, float width, float height, float length, int idMat, int dataMat) {
			this.loc = loc;
			this.typeParticle = typeParticle;
			this.velX = velX;
			this.velY = velY;
			this.velZ = velZ;
			this.cloud = cloud;
			this.useVelocity = useVelocity;
			this.nbParticule = nbParticule;
			this.width = width;
			this.height = height;
			this.length = length;
			this.idMat = idMat;
			this.dataMat = dataMat;
		}
		@Override
		public int getId() { return 0; }
		@Override
		public AbsCYMCommandAction clone() { return new PARTICLEDATA(loc, typeParticle, velX, velY, velZ, cloud, useVelocity, nbParticule, width, height, length, idMat, dataMat); }
		@Override
		public void initSend(Player p) { }
		@Override
		public void sendWrite() throws IOException {
			write(typeParticle);
			write(loc.getX());
			write(loc.getY());
			write(loc.getZ());
			write(velX);
			write(velY);
			write(velZ);
			write(cloud);
			write(useVelocity);
			write(nbParticule);
			write(width);
			write(height);
			write(length);
			write(idMat);
			write(dataMat);
		}
		@Override
		public void receiveRead() throws IOException { }
		@Override
		public void receive(Player p) { }
	}
}