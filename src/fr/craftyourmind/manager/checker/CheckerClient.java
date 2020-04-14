package fr.craftyourmind.manager.checker;

import fr.craftyourmind.manager.CYMChecker.ICheckerController;
import fr.craftyourmind.manager.CYMChecker.ICheckerPlayer;
import fr.craftyourmind.manager.CYMManager;
import fr.craftyourmind.manager.packet.DataChecker;

public abstract class CheckerClient extends CheckerMeta{
	protected final static String DELIMITER = CYMManager.DELIMITER;
	private ICheckerPlayer p;
	
	public CheckerClient(ICheckerPlayer p, ICheckerController c) {
		super(p, c);
		this.p = p;
	}
	
	@Override
	public void start(){
		super.start();
		/*ICheckerClient cc = p.getChecker(getType(), c);
		if(cc != null){
			if(cc.getId() != id){
				cc.stop();
				p.addChecker(this);
			}
		}else p.addChecker(this);*/
		if(p.getPlayer() != null) 
			new DataChecker(DataChecker.START, this).send(p.getPlayer());
	}
	
	@Override
	public void stop(){
		super.stop();
		if(p.getPlayer() != null)
			new DataChecker(DataChecker.STOP, this).send(p.getPlayer());
	}	
	
	//@Override
	public void loadParams(String params) {
		loadParams(params.split(DELIMITER));
	}
	
}
