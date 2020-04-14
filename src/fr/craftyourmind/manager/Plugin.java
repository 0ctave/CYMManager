package fr.craftyourmind.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import fr.craftyourmind.manager.command.AbsCYMCommand;
import fr.craftyourmind.manager.command.CmdParticle;
import fr.craftyourmind.manager.command.CmdTextScreen;
import fr.craftyourmind.manager.util.CYMHandlerUtil;

public class Plugin extends JavaPlugin{

	public static Plugin it;

	@Override
	public void onEnable() { 
		it = this;
		log("Load ...");
		CYMHandlerUtil.init();
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run(){
				CYMManager.init();}});
		
		new CYMEvent();
		AbsCYMCommand.add(new CmdParticle());
		AbsCYMCommand.add(new CmdTextScreen());
		
        log("Finish !");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try{
	    	if(label.equalsIgnoreCase("mini")){
		    	// ******** MINI ********
	    		if(args.length == 0){
		    		CYMManager.help(sender);
		        	return true;
		    	}
				// ******** MINI INIT ********
		    	/*if(sender.isOp() && args.length == 1 &&args[0].equalsIgnoreCase("init")) {
		    		MiniManager.init();
		    		sender.sendMessage(ChatColor.GRAY+"init...");
		    		return true;
		    	}*/
		    	if(args.length == 6 && args[0].equalsIgnoreCase("tp")){
		    		if(sender.hasPermission("cymmanager.tp")){
			    		Player p = Bukkit.getPlayer(args[1]);
			    		World w = Bukkit.getWorld(args[2]);
			    		int x = Integer.valueOf(args[3]);
			    		int y = Integer.valueOf(args[4]);
			    		int z = Integer.valueOf(args[5]);
			    		CYMManager.tpWorld(p, w, x, y, z);
		    		}else sender.sendMessage(ChatColor.GRAY+"You aren't permission.");
		    		return true;
		    	}
		    	if(args.length >= 5 && args[0].equalsIgnoreCase("repute")){
		    		if(sender.hasPermission("cymmanager.repute")){
		    			String nameCP = args[4];
			    		for(int i = 5 ; i < args.length ; i++) nameCP += " "+args[i];
		    			CYMManager.repute(sender, args[1], args[2], Integer.valueOf(args[3]), nameCP);
		    		}else sender.sendMessage(ChatColor.GRAY+"You aren't permission.");
		    		return true;
		    	}
		    	if(args.length == 1 && args[0].equalsIgnoreCase("synchronpc")){
		    		if(sender.hasPermission("cymmanager.synchronpc")){
		    			CYMManager.synchroCitizens(sender);
		    			sender.sendMessage(ChatColor.GRAY+"Synchronization npc ...");
		    		}else sender.sendMessage(ChatColor.GRAY+"You aren't permission.");
		    		return true;
		    	}
		    	if(args.length == 1 && args[0].equalsIgnoreCase("clan")){
		    		CYMManager.clanHelp(sender);
		    		return true;
		    	}
		    	if(args.length > 1 && args[0].equalsIgnoreCase("clan")){
		    		if(args[1].equalsIgnoreCase("create")){
		    			if(sender.hasPermission("cymmanager.clan.create")){
			    			if(args.length == 3) CYMManager.clanCreate(sender, args[2]);
			    			else if(args.length == 4) CYMManager.clanCreate(sender, args[2], args[3]);
		    			}else sender.sendMessage(ChatColor.GRAY+"You aren't permission.");
		    			return true;
		    		}
		    		if(args[1].equalsIgnoreCase("join")){
		    			if(sender.hasPermission("cymmanager.clan.join")){
			    			if(args.length == 3) CYMManager.clanJoin(sender, args[2]);
			    			else if(args.length == 4) CYMManager.clanJoin(sender, args[2], args[3]);
		    			}else sender.sendMessage(ChatColor.GRAY+"You aren't permission.");
		    			return true;
		    		}
		    		if(args[1].equalsIgnoreCase("leave")){
		    			if(sender.hasPermission("cymmanager.clan.leave")){
			    			if(args.length == 2) CYMManager.clanLeave(sender);
			    			else if(args.length == 3) CYMManager.clanLeave(sender, args[2]);
		    			}else sender.sendMessage(ChatColor.GRAY+"You aren't permission.");
		    			return true;
		    		}
		    		if(args[1].equalsIgnoreCase("remove")){
		    			if(sender.hasPermission("cymmanager.clan.remove")){
			    			if(args.length == 2) CYMManager.clanRemove(sender);
			    			else if(args.length == 3) CYMManager.clanRemove(sender, args[2]);
		    			}else sender.sendMessage(ChatColor.GRAY+"You aren't permission.");
		    			return true;
		    		}
		    		if(args[1].equalsIgnoreCase("chef")){
		    			if(sender.hasPermission("cymmanager.clan.chef")){
			    			if(args.length == 3) CYMManager.clanChef(sender, args[2]);
			    			else if(args.length == 4) CYMManager.clanChef(sender, args[2], args[3]);
		    			}else sender.sendMessage(ChatColor.GRAY+"You aren't permission.");
		    			return true;
		    		}
		    		if(args.length >= 6 && args[1].equalsIgnoreCase("repute")){
			    		if(sender.hasPermission("cymmanager.clan.repute")){
			    			String nameCP = args[5];
				    		for(int i = 6 ; i < args.length ; i++) nameCP += " "+args[i];
			    			CYMManager.clanRepute(sender, args[2], args[3], Integer.valueOf(args[4]), nameCP);
			    		}else sender.sendMessage(ChatColor.GRAY+"You aren't permission.");
			    		return true;
			    	}
		    		if(args[1].equalsIgnoreCase("info")){
		    			if(args.length == 2) CYMManager.clanInfo(sender);
		    			else if(args.length == 3) CYMManager.clanInfo(sender, args[2]);
		    			return true;
		    		}
		    		if(args[1].equalsIgnoreCase("list")){
		    			if(args.length == 2) CYMManager.clanList(sender);
		    			return true;
		    		}
		    	}
	    	}
		}catch (Exception e) {
			log("Error command : "+e.getMessage());
			e.printStackTrace();
		}
    	return false;
	}
	
	public static void log(String msg){
		it.getLogger().info(msg);
	}
}
