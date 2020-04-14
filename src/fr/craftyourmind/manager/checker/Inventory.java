package fr.craftyourmind.manager.checker;

import fr.craftyourmind.manager.CYMChecker;
import fr.craftyourmind.manager.CYMChecker.ICheckerInventory;
import fr.craftyourmind.manager.CYMChecker.ICheckerPlayer;

public class Inventory extends CheckerClient{

	private ICheckerInventory ci;
	
	public Inventory(ICheckerPlayer p, ICheckerInventory ci) {
		super(p, ci);
		this.ci = ci;
	}

	@Override
	public void tick() {
		ci.checker();
	}

	@Override
	public int getType() {
		return CYMChecker.INVENTORY;
	}

	@Override
	public String getParams() {
		return "";
	}

	@Override
	public void loadParams(String[] params) {
		
	}

}
