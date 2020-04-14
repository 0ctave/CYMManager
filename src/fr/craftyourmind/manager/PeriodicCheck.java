package fr.craftyourmind.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import fr.craftyourmind.manager.util.IChecker;

public class PeriodicCheck implements Runnable{

	private static List<IChecker> listChecker = new ArrayList<IChecker>();
	private static List<IChecker> listRemove= new ArrayList<IChecker>();
	private static List<IChecker> listAdd= new ArrayList<IChecker>();
	
	public PeriodicCheck(){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Plugin.it, this, 0, 5); // 0.25 secondes
	}
	
	public void run() {
		listChecker.removeAll(listRemove);
		listRemove.clear();
		listChecker.addAll(listAdd);
		listAdd.clear();
		for(IChecker c : listChecker) c.checker();
	}
	
	public static void add(IChecker c){
		listAdd.add(c);
		listRemove.remove(c);
	}
	
	public static void remove(IChecker c){
		listRemove.add(c);
		listAdd.remove(c);
	}
}