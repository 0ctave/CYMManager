package fr.craftyourmind.manager.checker;

import fr.craftyourmind.manager.CYMChecker;
import fr.craftyourmind.manager.CYMChecker.ICheckerPlayer;
import fr.craftyourmind.manager.CYMChecker.ICheckerUseItem;

public class UseItem extends CheckerClient{
	
	private ICheckerUseItem cui;
	private int slotSelected;
	
	public UseItem(ICheckerPlayer p, ICheckerUseItem cui) {
		super(p, cui);
		this.cui = cui;
	}
	@Override
	public void tick() {
		cui.setSlotSelected(slotSelected);
		cui.checker();
	}
	@Override
	public int getType() {
		return CYMChecker.USEITEM;
	}
	@Override
	public String getParams() {
		return new StringBuilder().append(cui.getIdItem()).append(DELIMITER).append(cui.getNameItem()).append(DELIMITER).append(cui.getIdData()).append(DELIMITER)
				.append(cui.getActionClick()).append(DELIMITER).append(cui.isSlotSkill()).append(DELIMITER).append(cui.isCancelMcEvent()).toString();
	}
	@Override
	public void loadParams(String[] params) {
		slotSelected = Integer.valueOf(params[0]);
	}
}