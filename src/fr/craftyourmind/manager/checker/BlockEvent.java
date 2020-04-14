package fr.craftyourmind.manager.checker;

import fr.craftyourmind.manager.CYMChecker;
import fr.craftyourmind.manager.CYMChecker.ICheckerBlockEvent;
import fr.craftyourmind.manager.CYMChecker.ICheckerPlayer;

public class BlockEvent extends CheckerClient{

	private ICheckerBlockEvent cb;
	
	public BlockEvent(ICheckerPlayer p, ICheckerBlockEvent cb) {
		super(p, cb);
		this.cb = cb;
	}

	@Override
	public void tick() {
		cb.event();
	}
	
	@Override
	public int getType() {
		return CYMChecker.BLOCKEVENT;
	}
	
	@Override
	public String getParams() {
		return cb.getTypeEvent()+DELIMITER+cb.getIdMat()+DELIMITER+cb.getData()+DELIMITER+cb.getCancelled()+DELIMITER+cb.getWorld()+DELIMITER+cb.getX()+DELIMITER+cb.getY()+DELIMITER+cb.getZ();
	}

	@Override
	public void loadParams(String[] params) {
		
	}	
	
}
