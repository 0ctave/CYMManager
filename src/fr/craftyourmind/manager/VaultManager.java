package fr.craftyourmind.manager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class VaultManager {

	private Economy econ;
	
	public void init() {
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) econ = rsp.getProvider();
	}
	
	public boolean hasMoney(String playerName, double amount){ 
		if(econ != null) return econ.has(playerName, amount);
		return false;
	}
	
}
