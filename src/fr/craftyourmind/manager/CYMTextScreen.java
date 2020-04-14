package fr.craftyourmind.manager;

import org.bukkit.entity.Player;
import fr.craftyourmind.manager.command.CmdTextScreen;

public class CYMTextScreen {
	// position
	public static final int LEFTTOP = 0;
	public static final int MIDDLETOP = 1;
	public static final int RIGHTTOP = 2;
	public static final int RIGHTMIDDLE = 3;
	public static final int RIGHTBOTTOM = 4;
	public static final int MIDDLEBOTTOM = 5;
	public static final int MIDDLE = 6;
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	public boolean useTextScreen = true;
	public int id = 0;
	public String text = "";
	public int position = 0;
	public int timer = 0;
	public boolean showTimer = true;
	public int color = 0xffffff;
	public boolean background = false;
	public int colorBackground = 0xc0101010;
	public boolean shadow = true;
	public String idMat;
	public int dataMat, posMat;
	
	public void start(CYMPlayer p){
		if(id == 0) id = p.getIdIncrementChecker();
		CmdTextScreen.sendStart(p.getPlayer(), this);
	}
	
	public void stop(Player p){
		CmdTextScreen.sendStop(p, this);
	}
	
	public String getDatas(){
		return new StringBuilder(text).append(CYMManager.DELIMITER)
				.append(position).append(CYMManager.DELIMITER)
				.append(timer).append(CYMManager.DELIMITER)
				.append(showTimer).append(CYMManager.DELIMITER)
				.append(color).append(CYMManager.DELIMITER)
				.append(background).append(CYMManager.DELIMITER)
				.append(colorBackground).append(CYMManager.DELIMITER)
				.append(shadow).append(CYMManager.DELIMITER)
				.append(idMat).append(CYMManager.DELIMITER)
				.append(dataMat).append(CYMManager.DELIMITER)
				.append(posMat).toString();
	}
}