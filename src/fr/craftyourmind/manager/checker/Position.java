package fr.craftyourmind.manager.checker;

import fr.craftyourmind.manager.CYMChecker;
import fr.craftyourmind.manager.CYMChecker.ICheckerPlayer;
import fr.craftyourmind.manager.CYMChecker.ICheckerPosition;

public class Position extends CheckerClient{	
	
	public final static int INSIDE = 1;
	public final static int OUTSIDE = 2;
	
	private int state;
	private ICheckerPosition cp;
	
	public Position(ICheckerPlayer p, ICheckerPosition cp) {
		super(p, cp);
		this.cp = cp;
	}

	@Override
	public void tick() {
		if(state == INSIDE) cp.inside();
		else if(state == OUTSIDE) cp.outside();
	}
	
	@Override
	public int getType() {
		return CYMChecker.POSITION;
	}
	
	@Override
	public String getParams() {
		return cp.getWorld()+DELIMITER+cp.getX()+DELIMITER+cp.getY()+DELIMITER+cp.getZ()+DELIMITER+cp.getRadius();
	}

	@Override
	public void loadParams(String[] params) {
		state = Integer.valueOf(params[0]);
	}
}
