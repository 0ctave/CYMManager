package fr.craftyourmind.manager.checker;

import fr.craftyourmind.manager.CYMChecker;
import fr.craftyourmind.manager.CYMChecker.ICheckerPlayer;
import fr.craftyourmind.manager.CYMChecker.ICheckerTextScreen;

public class TextScreen extends CheckerClient{

	private ICheckerTextScreen cts;
	
	public TextScreen(ICheckerPlayer p, ICheckerTextScreen cts) {
		super(p, cts);
		this.cts = cts;
	}
	@Override
	public void tick() { }
	@Override
	public int getType() { return CYMChecker.TEXTSCREEN; }
	@Override
	public String getParams() {
		return cts.getTextScreen().getDatas();
	}
}