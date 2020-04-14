package fr.craftyourmind.manager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import fr.craftyourmind.manager.sql.SQLCYMManager;
import fr.craftyourmind.manager.util.INPCEvent;

public class CYMManager {

	public final static String DELIMITER =";";
	
	public static CitizensManager citizens;
	public static VaultManager vault;
	
	static public void init() {
		load();
		SQLCYMManager.init();
		SQLCYMManager.load();
		new PeriodicCheck();
		org.bukkit.plugin.Plugin p = Bukkit.getPluginManager().getPlugin("Citizens");
		if(p == null)
			Plugin.log("Cannot find Citizens");
		else{
			citizens = new CitizensManager();
			citizens.init();
		}
		p = Bukkit.getPluginManager().getPlugin("Vault");
		if(p == null)
			Plugin.log("Cannot find Vault");
		else{
			vault = new VaultManager();
			vault.init();
		}
	}
	
	// ************************* NPC *************************
	public static boolean isNPC(Entity entity) {
		if(citizens != null) return citizens.isNPC(entity);
		return false;
	}
	
	public static boolean hasSelectedNPC(Player p) {
		if(citizens != null) return citizens.hasSelectedNPC(p);
		return false;
	}

	public static int getSelectedNPC(Player p) {
		if(citizens != null) return citizens.getSelectedNPC(p);
		return -1;
	}
	
	public static void addNpcEvent(INPCEvent npcevent){
		if(citizens != null) citizens.addNpcEvent(npcevent);
	}
	
	public static Map<Integer, String> getIdNameNPC(){
		if(citizens != null) return citizens.getIdName();
		return new HashMap<Integer, String>();
	}
	
	public static Entity getPlayerNPC(int idnpc){
		if(citizens != null) return citizens.getPlayerNPC(idnpc);
		return null;
	}
	
	public static void synchroCitizens(CommandSender sender){
		if(citizens != null) CYMNPC.synchroCitizens(citizens.getIdName());
	}
	
	// ************************* VAULT *************************
	public static boolean hasMoney(String playerName, double amount){
		if(vault != null) return vault.hasMoney(playerName, amount);
		return false;
	}
	
	
	// ************************* TOOLS *************************
	
	public static void tpWorld(Player p, World w, int x, int y, int z) {
		if(w != null && p != null)
			p.teleport(new Location(w, x, y, z));
	}
	
	public static void repute(CommandSender sender, String namePlayer, String option, int points, String repute) {
		CYMPlayer mp = CYMPlayer.get(namePlayer);
		if(mp != null){
			CYMReputation r = CYMReputation.get(repute);
			if(r != null){
				if(option.equalsIgnoreCase("init")) mp.initRepute(r, points, 0);
				else if(option.equalsIgnoreCase("add")) mp.addRepute(r, points, 0);
			}else sender.sendMessage(ChatColor.GRAY+repute+" doesn't exit !");
		}else sender.sendMessage(ChatColor.GRAY+namePlayer+" doesn't exit !");
	}
	
	// ************************* CLANS *************************
	
	public static boolean hasClan(Player p) {
		return CYMPlayer.get(p).hasClan();
	}
	
	public static void clanInfo(CommandSender sender) {
		if(sender instanceof Player){
			CYMPlayer mp = CYMPlayer.get((Player)sender);
			if(mp.hasClan()){
				clanInfo(sender, mp.getClan());
			}else sender.sendMessage(ChatColor.GRAY+"You have not clan.");
		}else sender.sendMessage(ChatColor.GRAY+"You are not a player.");
	}
	public static void clanInfo(CommandSender sender, String clan) {
		CYMClan mcClan = CYMClan.get(clan);
		if(mcClan != null) clanInfo(sender, mcClan);
		else sender.sendMessage(ChatColor.GRAY+clan+" doesn't exit !");
	}
	public static void clanInfo(CommandSender sender, CYMClan clan) {
		sender.sendMessage(ChatColor.GREEN+"*** "+clan.name+" : "+clan.getMembres().size()+" players ***");
		sender.sendMessage(ChatColor.GRAY+"* CHEF : "+((clan.chef == null)?"":clan.chef.name));
		for(CYMPlayer mp : clan.getMembres())
			sender.sendMessage(ChatColor.GRAY+"* "+mp.name);
	}
	
	public static void clanList(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN+"*** CLAN LIST ***");
		for(CYMClan mc : CYMClan.get())
			sender.sendMessage(ChatColor.GRAY+"* "+mc.name);
	}
	
	public static void clanHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "**** HELP CLAN ****");
		if(sender.hasPermission("cymmanager.clan.create")) sender.sendMessage(ChatColor.GRAY+"/mini clan create <clanTag> [<chef>] -  Create clan.");
		if(sender.hasPermission("cymmanager.clan.join")) sender.sendMessage(ChatColor.GRAY+"/mini clan join [<clanTag>] <player> - Join a clan.");
		if(sender.hasPermission("cymmanager.clan.leave")) sender.sendMessage(ChatColor.GRAY+"/mini clan leave [<player>] - Leave a clan.");
		if(sender.hasPermission("cymmanager.clan.remove")) sender.sendMessage(ChatColor.GRAY+"/mini clan remove [<clanTag>] - Remove a clan.");
		if(sender.hasPermission("cymmanager.clan.chef")) sender.sendMessage(ChatColor.GRAY+"/mini clan chef [<clanTag>] <player> - Add a chef to a clan.");
		if(sender.hasPermission("cymmanager.clan.repute")) sender.sendMessage(ChatColor.GRAY+"/mini clan repute <clanTag> <init|add> <points> <repute> -  Add player reputation");
		sender.sendMessage(ChatColor.GRAY+"/mini clan info [<clanTag>] - Information of clan.");
		sender.sendMessage(ChatColor.GRAY+"/mini clan list - List of clans.");
	}
	
	// ******** create ********
	public static void clanCreate(CommandSender sender, String clan) {
		if(sender instanceof Player){
			CYMPlayer mpChef = CYMPlayer.get((Player)sender);
			clanCreate(sender, clan, mpChef);
		}else sender.sendMessage(ChatColor.GRAY+"You are not a player.");
	}
	public static void clanCreate(CommandSender sender, String clan, String chef) {
		CYMPlayer mpChef = CYMPlayer.get(chef);
		if(mpChef == null) sender.sendMessage(ChatColor.GRAY+chef+" doesn't exit !");
		else clanCreate(sender, clan, mpChef);
	}
	public static void clanCreate(CommandSender sender, String clan, CYMPlayer chef) {
		if(chef == null) sender.sendMessage(ChatColor.GRAY+"Player doesn't exit !");
		else if(chef.hasClan()) sender.sendMessage(ChatColor.GRAY+chef.name+" is in a clan !");
		else{
			CYMClan mc = CYMClan.get(clan);
			if(mc == null){
				mc = new CYMClan(clan);
				CYMClan.add(mc);
				chef.join(mc);
				mc.setChef(chef);
				sender.sendMessage(ChatColor.GRAY+clan+" create.");
			}else sender.sendMessage(ChatColor.GRAY+clan+" exist !");
		}	
	}
	
	// ******** join ********
	public static void clanJoin(CommandSender sender, String membre) {
		if(sender instanceof Player){
			CYMPlayer mpChef = CYMPlayer.get((Player)sender);
			if(mpChef.isChef()){
				CYMPlayer mpMembre = CYMPlayer.get(membre);
				if(mpMembre == null) sender.sendMessage(ChatColor.GRAY+membre+" doesn't exist !");
				else mpMembre.join(mpChef.getClan());
			}else sender.sendMessage(ChatColor.GRAY+"You are not chef.");
		}else sender.sendMessage(ChatColor.GRAY+"You are not a player.");
	}
	public static void clanJoin(CommandSender sender, String clan, String membre) {
		if(sender.isOp()){
			CYMClan mc = CYMClan.get(clan);
			if(mc != null){
				CYMPlayer mp = CYMPlayer.get(membre);
				if(mp != null){
					mp.join(mc);
					sender.sendMessage(ChatColor.GRAY+membre+" join "+clan+".");
				}
				else sender.sendMessage(ChatColor.GRAY+membre+" doesn't exit !");
			}else sender.sendMessage(ChatColor.GRAY+clan+" doesn't exit !");
		}else sender.sendMessage(ChatColor.GRAY+"You are not a op.");
	}
	
	// ******** leave ********
	public static void clanLeave(CommandSender sender) {
		if(sender instanceof Player){
			CYMPlayer mp = CYMPlayer.get((Player)sender);
			if(mp.isChef()) sender.sendMessage(ChatColor.GRAY+"You are chef, you can't leave.");
			else mp.leave();
		}else sender.sendMessage(ChatColor.GRAY+"You are not a player.");
	}
	public static void clanLeave(CommandSender sender, String membre) {
		if(sender.isOp()){
			CYMPlayer mp = CYMPlayer.get(membre);
			if(mp != null){
				if(mp.hasClan()){
					mp.leave();
					sender.sendMessage(ChatColor.GRAY+membre+" leave.");
				} else sender.sendMessage(ChatColor.GRAY+membre+" doesn't in clan !");
			} else sender.sendMessage(ChatColor.GRAY+membre+" doesn't exit !");
		}else sender.sendMessage(ChatColor.GRAY+"You are not a op.");
	}
	
	// ******** remove ********
	public static void clanRemove(CommandSender sender) {
		if(sender instanceof Player){
			CYMPlayer mp = CYMPlayer.get((Player)sender);
			if(mp.isChef()) mp.getClan().del();
			else sender.sendMessage(ChatColor.GRAY+"You are not chef.");
		}else sender.sendMessage(ChatColor.GRAY+"You are not a player.");
	}
	public static void clanRemove(CommandSender sender, String clan) {
		if(sender.isOp()){
			CYMClan mc = CYMClan.get(clan);
			if(mc != null) mc.del();
			else sender.sendMessage(ChatColor.GRAY+clan+" doesn't exit !");
		}else sender.sendMessage(ChatColor.GRAY+"You are not a op.");
	}
	
	// ******** chef ********
	public static void clanChef(CommandSender sender, String membre) {
		if(sender instanceof Player){
			CYMPlayer mpChef = CYMPlayer.get((Player)sender);
			if(mpChef.isChef()){
				if(membre.equalsIgnoreCase("npc")) mpChef.getClan().setChef(null);
				else {
					CYMPlayer mp = CYMPlayer.get(membre);
					if(mp != null) mpChef.getClan().setChef(mp);
					else sender.sendMessage(ChatColor.GRAY+membre+" doesn't exit !");
				}
			}else sender.sendMessage(ChatColor.GRAY+"You are not chef.");
		}else sender.sendMessage(ChatColor.GRAY+"You are not a player.");
	}
	public static void clanChef(CommandSender sender, String clan, String membre) {
		if(sender.isOp()){
			CYMClan mc = CYMClan.get(clan);
			if(mc != null){
				if(membre.equalsIgnoreCase("npc")) mc.setChef(null);
				else {
					CYMPlayer mp = CYMPlayer.get(membre);
					if(mp != null && mp.getClan() == mc) mc.setChef(mp);
					else sender.sendMessage(ChatColor.GRAY+membre+" doesn't exit !");
				}
			}else sender.sendMessage(ChatColor.GRAY+clan+" doesn't exit !");
		}else sender.sendMessage(ChatColor.GRAY+"You are not a op.");
	}
	
	// ******** repute ********
	public static void clanRepute(CommandSender sender, String clan, String option, int points, String repute) {
		CYMClan mc = CYMClan.get(clan);
		if(mc != null){
			CYMReputation r = CYMReputation.get(repute);
			if(r != null){
				if(option.equalsIgnoreCase("init")) mc.initRepute(r, points, 0);
				else if(option.equalsIgnoreCase("add")) mc.addRepute(r, points, 0);
			}else sender.sendMessage(ChatColor.GRAY+repute+" doesn't exit !");
		}else sender.sendMessage(ChatColor.GRAY+clan+" doesn't exit !");
	}
	
	public static void help(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "**** HELP MINICRAFT MANAGER ****");
		if(sender.hasPermission("cymmanager.tp")) sender.sendMessage(ChatColor.GRAY+"/mini tp <player> <world> <x> <y> <z> - Teleport player to world coordonnate.");
		if(sender.hasPermission("cymmanager.repute")) sender.sendMessage(ChatColor.GRAY+"/mini repute <player> <init|add> <points> <repute> -  Initialise or add player reputation.");
		if(sender.hasPermission("cymmanager.synchronpc")) sender.sendMessage(ChatColor.GRAY+"/mini synchronpc -  Synchronization Citizens npc with Minicraft.");
		sender.sendMessage(ChatColor.GRAY+"/mini clan -  Help clan");
	}
	
	public static void load(){
		File dir = Plugin.it.getDataFolder();
		dir.mkdir();
		File file = new File(dir, "manager.yml");
		try {
			YamlConfiguration conf = new YamlConfiguration().loadConfiguration(file);
			if(file.createNewFile()){
				
				
				conf.save(file);
			}
			
			
		} catch (Exception e) {
			 Plugin.log("Error load manager : "+e.getMessage());
           e.printStackTrace();
		} 
	}
	
}