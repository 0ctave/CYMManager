package fr.craftyourmind.manager.checker;

import fr.craftyourmind.manager.CYMChecker.ICheckerClient;
import fr.craftyourmind.manager.CYMChecker.ICheckerController;
import fr.craftyourmind.manager.CYMChecker.ICheckerEntity;

public abstract class CheckerMeta implements ICheckerClient{
	
	private int id;
	private ICheckerEntity e;
	private ICheckerController c;
	
	public CheckerMeta(ICheckerEntity e, ICheckerController c) {
		id = e.getIdIncrementChecker();
		this.e = e;
		this.c = c;
		e.addChecker(this);
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void start() {
		/*ICheckerClient cc = p.getChecker(getType(), c);
		if(cc != null){
			if(cc.getId() != getId()){
				cc.stop();
				p.addChecker(this);
			}
		}else p.addChecker(this);*/
		//e.addChecker(this);
	}

	@Override
	public void stop() {
		e.removeChecker(this);
	}
	
	@Override
	public ICheckerController getChecker() {
		return c;
	}
	
	@Override
	public String getParams() {
		return "";
	}
	
	@Override
	public void loadParams(String params) {
		
	}
	
	@Override
	public void loadParams(String[] params) {
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(getClass() == obj.getClass()){
			CheckerMeta cc = (CheckerMeta) obj;
			return getId() == cc.getId();
		}
		return false;
	}
}
